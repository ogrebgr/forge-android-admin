package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class Res_AdminUsersListImpl extends SessionResidentComponent<Res_AdminUsersList.State> implements Res_AdminUsersList {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private volatile long mLoadXId;

    private List<AdminUser> mData;
    private Gson mGson;


    @Inject
    public Res_AdminUsersListImpl(ForgeExchangeHelper forgeExchangeHelper,
                                  Session session,
                                  NetworkInfoProvider networkInfoProvider) {


        super(State.IDLE, forgeExchangeHelper, session, networkInfoProvider);

        mGson = new Gson();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        loadAdminUsers();
    }


    @Override
    public void loadAdminUsers() {
        if (getState() == State.IDLE) {
            switchToState(State.WAITING_DATA);
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
        if (isInOneOfStates(State.DATA_OK, State.DATA_FAIL)) {
            resetState();
        }
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
