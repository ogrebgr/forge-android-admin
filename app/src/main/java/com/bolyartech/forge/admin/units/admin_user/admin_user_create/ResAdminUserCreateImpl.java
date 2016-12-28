package com.bolyartech.forge.admin.units.admin_user.admin_user_create;

import com.bolyartech.forge.android.app_unit.AbstractSideEffectOperationResidentComponent;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
//
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class ResAdminUserCreateImpl extends AbstractSideEffectOperationResidentComponent<Void, Integer>
        implements ResAdminUserCreate {


    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile Long mCreateXId;

    private final ForgeExchangeHelper mForgeExchangeHelper;


    @Inject
    public ResAdminUserCreateImpl(ForgeExchangeHelper forgeExchangeHelper) {
        mForgeExchangeHelper = forgeExchangeHelper;
    }


    @Override
    public void save(String username,
                     String name,
                     String password,
                     boolean superAdmin) {

        if (isIdle()) {
            switchToBusyState();
            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("create_admin_user");
            b.addPostParameter("username", username);
            b.addPostParameter("name", name);
            b.addPostParameter("password", password);
            b.addPostParameter("super_admin", superAdmin ? "1" : "0");

            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mCreateXId = em.generateTaskId();
            em.executeExchange(b.build(), mCreateXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mCreateXId) {
            handleCreateOutcome(isSuccess, result);
        }
    }


    private void handleCreateOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.OK) {
                    switchToEndedStateSuccess(null);
                } else {
                    switchToEndedStateFail(null);
                }
            } else {
                switchToEndedStateFail(code);
            }
        } else {
            switchToEndedStateFail(null);
        }
    }

}
