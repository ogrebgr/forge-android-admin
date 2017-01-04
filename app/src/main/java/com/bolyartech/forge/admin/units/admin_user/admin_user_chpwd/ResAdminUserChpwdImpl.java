package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import com.bolyartech.forge.android.app_unit.AbstractSideEffectOperationResidentComponent;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class ResAdminUserChpwdImpl extends AbstractSideEffectOperationResidentComponent<Void, Integer> implements ResAdminUserChpwd {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile Long mSaveXId;
    private int mLastError;

    private final ForgeExchangeHelper mForgeExchangeHelper;


    @Inject
    public ResAdminUserChpwdImpl(ForgeExchangeHelper forgeExchangeHelper) {

        mForgeExchangeHelper = forgeExchangeHelper;
    }




    @Override
    public void save(long userId, String password) {
        if (isIdle()) {
            mLastError = 0;
            switchToBusyState();

            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("admin_user_chpwd");
            b.addPostParameter("user", Long.toString(userId));
            b.addPostParameter("new_password", password);

            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mSaveXId = em.generateTaskId();
            em.executeExchange(b.build(), mSaveXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mSaveXId) {
            handleSaveOutcome(isSuccess, result);
        }
    }


    private void handleSaveOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.OK) {
                    switchToEndedStateSuccess(null);
                } else {
                    switchToEndedStateFail(null);
                }
            } else {
                mLastError = code;
                switchToEndedStateFail(code);
            }
        } else {
            switchToEndedStateFail(null);
        }
    }
}
