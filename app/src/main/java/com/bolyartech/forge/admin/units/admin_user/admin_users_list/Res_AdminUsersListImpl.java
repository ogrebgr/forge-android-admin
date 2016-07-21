package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.EventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class Res_AdminUsersListImpl extends SessionResidentComponent implements Res_AdminUsersList {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final StateManager<State> mStateManager;

    private volatile long mLoadXId;

    private List<AdminUser> mData;
    private Gson mGson;


    @Inject
    public Res_AdminUsersListImpl(ForgeExchangeHelper forgeExchangeHelper,
                                  Session session,
                                  NetworkInfoProvider networkInfoProvider,
                                  EventPoster eventPoster) {


        super(forgeExchangeHelper, session, networkInfoProvider, eventPoster);
        mStateManager = new StateManagerImpl<>(eventPoster, State.IDLE);

        mGson = new Gson();
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        loadAdminUsers();
    }


    @Override
    public void loadAdminUsers() {
        if (getState() == State.IDLE) {
            mStateManager.switchToState(State.WAITING_DATA);
            ForgeGetHttpExchangeBuilder b = createForgeGetHttpExchangeBuilder("admin_users");
            ForgeExchangeManager em = getForgeExchangeManager();
            mLoadXId = em.generateTaskId();
            em.executeExchange(b.build(), mLoadXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }



    @Override
    public List<AdminUser> getData() {
        return mData;
    }


    @Override
    public void stateHandled() {
        mData = null;
        mStateManager.reset();
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mLoadXId) {
            handleLoadUsers(isSuccess, result);
        }
    }


    private void handleLoadUsers(boolean isSuccess, ForgeExchangeResult result) {
        if (isSuccess) {
            int code = result.getCode();

            if (code > 0) {
                if (code == BasicResponseCodes.Oks.OK.getCode()) {
                    Type listType = new TypeToken<ArrayList<AdminUser>>(){}.getType();
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
