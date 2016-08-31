package com.bolyartech.forge.admin.units.user.user_manage;

import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.AbstractOperationResidentComponent;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class ResUserManageImpl extends AbstractOperationResidentComponent implements ResUserManage {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mDisableXId;

    private boolean mDisableResult;

    private final ForgeExchangeHelper mForgeExchangeHelper;

    @Inject
    public ResUserManageImpl(ForgeExchangeHelper forgeExchangeHelper) {
        mForgeExchangeHelper = forgeExchangeHelper;
    }


    @Override
    public void disableUser(User user) {
        if (isIdle()) {
            switchToBusyState();
            disableEnable(user.getId(), true);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    private void disableEnable(long id, boolean disable) {
        ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("user_disable");
        b.addPostParameter("user", Long.toString(id));
        b.addPostParameter("disable", disable ? "1" : "0");

        ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
        mDisableXId = em.generateTaskId();
        em.executeExchange(b.build(), mDisableXId);
    }


    @Override
    public void enableUser(User user) {
        if (isIdle()) {
            switchToBusyState();
            disableEnable(user.getId(), false);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public boolean getDisableResult() {
        return mDisableResult;
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mDisableXId) {
            handleDisableResult(isSuccess, result);
        }
    }


    private void handleDisableResult(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    try {
                        JSONObject jobj = new JSONObject(result.getPayload());
                        mDisableResult = jobj.getBoolean("disabled");
                        switchToCompletedStateSuccess();
                    } catch (JSONException e) {
                        switchToCompletedStateFail();
                    }
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
