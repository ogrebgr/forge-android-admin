package com.bolyartech.forge.admin.units.user.users;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class Res_UsersImpl extends SessionResidentComponent<Res_Users.State> implements Res_Users {

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mSearchXId;

    private List<User> mData;
    private Gson mGson;


    @Inject
    public Res_UsersImpl(ForgeExchangeHelper forgeExchangeHelper,
                         Session session,
                         NetworkInfoProvider networkInfoProvider,
                         Bus bus) {
        super(new StateManagerImpl<>(bus, State.IDLE),
                forgeExchangeHelper,
                session,
                networkInfoProvider);

        mGson = new Gson();
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
        super.stateHandled();
        mData = null;
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

                    switchToState(State.DATA_OK);
                } else {
                    switchToState(State.DATA_FAIL);
                }
            } else {
                switchToState(State.DATA_FAIL);
            }
        } else {
            switchToState(State.DATA_FAIL);
        }
    }
}
