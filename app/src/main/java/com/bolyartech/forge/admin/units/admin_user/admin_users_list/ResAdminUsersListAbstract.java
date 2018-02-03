package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.OperationResidentComponentImpl;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class ResAdminUsersListAbstract extends OperationResidentComponentImpl implements ResAdminUsersList {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mLoadXId;

    private List<AdminUser> mData;
    private Gson mGson;

    private final ForgeExchangeHelper mForgeExchangeHelper;


    @Inject
    public ResAdminUsersListAbstract(ForgeExchangeHelper forgeExchangeHelper) {

        mGson = new Gson();

        mForgeExchangeHelper = forgeExchangeHelper;

        loadAdminUsers();
    }


    @Override
    public void loadAdminUsers() {
        if (isIdle()) {
            switchToBusyState();
            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("admin_users");
            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mLoadXId = em.executeExchange(b.build());

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
                if (code == BasicResponseCodes.OK) {
                    Type listType = new TypeToken<ArrayList<AdminUser>>(){}.getType();
                    mData = mGson.fromJson(result.getPayload(), listType);
                    switchToEndedStateSuccess();
                } else {
                    switchToEndedStateFail();
                }
            } else {
                switchToEndedStateFail();
            }
        } else {
            switchToEndedStateFail();
        }
    }
}
