package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.AbstractOperationResidentComponent;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ResAdminUsersListImpl extends AbstractOperationResidentComponent implements ResAdminUsersList {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mLoadXId;

    private List<AdminUser> mData;
    private Gson mGson;

    private final ForgeExchangeHelper mForgeExchangeHelper;


    @Inject
    public ResAdminUsersListImpl(ForgeExchangeHelper forgeExchangeHelper) {

        mGson = new Gson();

        mForgeExchangeHelper = forgeExchangeHelper;
    }


    @Override
    public void onCreate() {
        loadAdminUsers();
    }


    @Override
    public void loadAdminUsers() {
        if (isIdle()) {
            switchToBusyState();
            ForgeGetHttpExchangeBuilder b = mForgeExchangeHelper.createForgeGetHttpExchangeBuilder("admin_users");
            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mLoadXId = em.generateTaskId();
            em.executeExchange(b.build(), mLoadXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }



    @Override
    public List<AdminUser> getData() {
        return mData;
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mLoadXId) {
            handleLoadUsers(isSuccess, result);
        }
    }


    private void handleLoadUsers(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    Type listType = new TypeToken<ArrayList<AdminUser>>(){}.getType();
                    mData = mGson.fromJson(result.getPayload(), listType);
                    switchToCompletedStateSuccess();
                } else {
                    switchToCompletedStateFail();
                }
            } else {
                switchToCompletedStateFail();
            }
        } else {
            switchToCompletedStateFail();
        }
    }
}