package com.bolyartech.forge.admin.units.admin_user_create;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.AndroidEventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_AdminUserCreateImpl extends SessionResidentComponent implements Res_AdminUserCreate {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final StateManager<State> mStateManager;
    private volatile Long mCreateXId;
    private int mLastError;


    @Inject
    public Res_AdminUserCreateImpl(ForgeExchangeHelper forgeExchangeHelper,
                                   Session session,
                                   NetworkInfoProvider networkInfoProvider,
                                   AndroidEventPoster androidEventPoster) {
        super(forgeExchangeHelper, session, networkInfoProvider, androidEventPoster);

        mStateManager = new StateManagerImpl<>(androidEventPoster, State.IDLE);
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void resetState() {
        mStateManager.reset();
    }


    @Override
    public void save(String username,
                     String name,
                     String password,
                     boolean superAdmin) {

        mLastError = 0;

        if (mStateManager.getState() == State.IDLE) {
            mStateManager.switchToState(State.SAVING);
            ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("create_user");
            b.addPostParameter("username", username);
            b.addPostParameter("name", name);
            b.addPostParameter("password", password);
            b.addPostParameter("super_admin", superAdmin ? "1" : "0");

            ForgeExchangeManager em = getForgeExchangeManager();
            mCreateXId = em.generateTaskId();
            em.executeExchange(b.build(), mCreateXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public int getLastError() {
        return mLastError;
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mCreateXId) {
            handleCreateOutcome(isSuccess, result);
        }
    }


    private void handleCreateOutcome(boolean isSuccess, ForgeExchangeResult result) {
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
