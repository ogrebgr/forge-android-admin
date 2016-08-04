package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_AdminUserChpwdImpl extends SessionResidentComponent<Res_AdminUserChpwd.State> implements Res_AdminUserChpwd {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile Long mSaveXId;
    private int mLastError;


    @Inject
    public Res_AdminUserChpwdImpl(ForgeExchangeHelper forgeExchangeHelper,
                                  Session session,
                                  NetworkInfoProvider networkInfoProvider) {

        super(State.IDLE, forgeExchangeHelper, session, networkInfoProvider);
    }




    @Override
    public void save(long userId, String password) {
        if (getState() == State.IDLE) {
            mLastError = 0;
            switchToState(State.SAVING);

            ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("change_admin_password");
            b.addPostParameter("user", Long.toString(userId));
            b.addPostParameter("new_password", password);

            ForgeExchangeManager em = getForgeExchangeManager();
            mSaveXId = em.generateTaskId();
            em.executeExchange(b.build(), mSaveXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public int getLastError() {
        return  mLastError;
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mSaveXId) {
            handleSaveOutcome(isSuccess, result);
        }
    }


    private void handleSaveOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    switchToState(State.SAVE_OK);
                } else {
                    switchToState(State.SAVE_FAIL);
                }
            } else {
                mLastError = code;
                switchToState(State.SAVE_FAIL);
            }
        } else {
            switchToState(State.SAVE_FAIL);
        }
    }


    @Override
    public void stateHandled() {
        if (isInOneOfStates(State.SAVE_OK, State.SAVE_FAIL)) {
            resetState();
        }
    }
}
