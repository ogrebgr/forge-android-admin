package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.EventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class Res_UsersImpl extends SessionResidentComponent implements Res_Users {

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final StateManager<State> mStateManager;

    private volatile long mSearchXId;

    private List<User> mData;
    private Gson mGson;


    @Inject
    public Res_UsersImpl(ForgeExchangeHelper forgeExchangeHelper,
                         Session session,
                         NetworkInfoProvider networkInfoProvider,
                         EventPoster eventPoster) {
        super(forgeExchangeHelper, session, networkInfoProvider, eventPoster);

        mGson = new Gson();
        mStateManager = new StateManagerImpl<>(eventPoster, State.IDLE);
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void searchForUser(String pattern) {
        if (getState() == State.IDLE) {
            ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("user_find");
            b.addPostParameter("pattern", pattern);
            ForgeExchangeManager em = getForgeExchangeManager();
            mSearchXId = em.generateTaskId();
            em.executeExchange(b.build(), mSearchXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public List<User> getData() {
        return mData;
    }


    @Override
    public void stateHandled() {
        mData = null;
        mStateManager.reset();
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mSearchXId) {
            handleSearchOutcome(isSuccess, result);
        }
    }


    private void handleSearchOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                    mData = mGson.fromJson(result.getPayload(), listType);

                    mStateManager.switchToState(State.DATA_OK);
                } else {
                    mStateManager.switchToState(State.DATA_FAIL);
                }
            } else {
                mStateManager.switchToState(State.DATA_FAIL);
            }
        } else {
            mStateManager.switchToState(State.DATA_FAIL);
        }
    }
}
