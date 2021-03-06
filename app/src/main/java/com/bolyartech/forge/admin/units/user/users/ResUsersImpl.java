package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.data.User;
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


public class ResUsersImpl extends OperationResidentComponentImpl implements ResUsers {

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mSearchXId;

    private List<User> mData;
    private Gson mGson;

    private final ForgeExchangeHelper mForgeExchangeHelper;


    @Inject
    public ResUsersImpl(ForgeExchangeHelper forgeExchangeHelper) {
        mGson = new Gson();
        mForgeExchangeHelper = forgeExchangeHelper;
    }


    @Override
    public void searchForUser(String pattern) {
        if (isIdle()) {
            switchToBusyState();
            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("user_find");
            b.addPostParameter("pattern", pattern);
            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mSearchXId = em.executeExchange(b.build());

        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public List<User> getData() {
        return mData;
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mSearchXId) {
            handleSearchOutcome(isSuccess, result);
        }
    }


    private void handleSearchOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.OK) {
                    Type listType = new TypeToken<ArrayList<User>>(){}.getType();
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
