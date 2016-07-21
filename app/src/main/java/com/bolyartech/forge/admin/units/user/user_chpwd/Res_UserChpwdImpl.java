package com.bolyartech.forge.admin.units.user.user_chpwd;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.EventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_UserChpwdImpl extends SessionResidentComponent implements Res_UserChpwd {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final StateManager<State> mStateManager;
    private volatile Long mSaveXId;
    private int mLastError;


    @Inject
    public Res_UserChpwdImpl(ForgeExchangeHelper forgeExchangeHelper,
                             Session session,
                             NetworkInfoProvider networkInfoProvider,
                             EventPoster eventPoster) {

        super(forgeExchangeHelper, session, networkInfoProvider, eventPoster);

        mStateManager = new StateManagerImpl<>(eventPoster, State.IDLE);
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void stateAcknowledged() {
        mStateManager.reset();
    }


    @Override
    public void save(long userId, String password) {
        if (mStateManager.getState() == State.IDLE) {
            mLastError = 0;
            mStateManager.switchToState(State.SAVING);

            ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("change_password");
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
                    mStateManager.switchToState(State.SAVE_OK);
                } else {
                    mStateManager.switchToState(State.SAVE_FAIL);
                }
            } else {
                mLastError = code;
                mStateManager.switchToState(State.SAVE_FAIL);
            }
        } else {
            mStateManager.switchToState(State.SAVE_FAIL);
        }
    }
}
