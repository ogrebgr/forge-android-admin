package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.EventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_MainImpl extends SessionResidentComponent implements Res_Main {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final StateManager<State> mStateManager;

    private final AppConfiguration mAppConfiguration;
    private final NetworkInfoProvider mNetworkInfoProvider;

    private volatile long mLoginXId;
    private volatile boolean mAbortLogin = false;


    @Inject
    public Res_MainImpl(ForgeExchangeHelper forgeExchangeHelper,
                        Session session,
                        NetworkInfoProvider networkInfoProvider,
                        EventPoster eventPoster,
                        AppConfiguration appConfiguration) {

        super(forgeExchangeHelper, session, networkInfoProvider, eventPoster);
        mStateManager = new StateManagerImpl<>(eventPoster, State.IDLE);
        mAppConfiguration = appConfiguration;
        mNetworkInfoProvider = networkInfoProvider;
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (mNetworkInfoProvider.isConnected()) {
            mStateManager.switchToState(State.IDLE);
            init();
        }
    }


    private void init() {
        if (mNetworkInfoProvider.isConnected()) {
            if (mAppConfiguration.getLoginPrefs().hasLoginCredentials()) {
                loginActual();
            }
        }
    }


    @Override
    public void login() {
        loginActual();
    }


    @Override
    public void startSession() {
        mStateManager.switchToState(State.SESSION_STARTED_OK);
    }


    @Override
    public void abortLogin() {
        mAbortLogin = true;
        mStateManager.switchToState(State.IDLE);
    }


    @Override
    public void logout() {
        getSession().logout();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ForgeGetHttpExchangeBuilder b = createForgeGetHttpExchangeBuilder("logout");
                ForgeExchangeManager em = getForgeExchangeManager();
                em.executeExchange(b.build(), em.generateTaskId());
            }
        });
        t.start();
        mStateManager.switchToState(State.IDLE);
    }


    @Override
    public void internetAvailable() {

    }


    @Override
    public void stateHandled() {
        mAbortLogin = false;
        mStateManager.switchToState(State.IDLE);
    }


    @Override
    public void onConnectivityChange() {

    }




    private void loginActual() {
        if (getState() == State.IDLE) {
            mStateManager.switchToState(State.LOGGING_IN);

            ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("login");
            b.addPostParameter("username", mAppConfiguration.getLoginPrefs().getUsername());
            b.addPostParameter("password", mAppConfiguration.getLoginPrefs().getPassword());
            b.addPostParameter("app_type", "1");
            b.addPostParameter("app_version", mAppConfiguration.getAppVersion());
            b.addPostParameter("session_info", "1");

            ForgeExchangeManager em = getForgeExchangeManager();
            mLoginXId = em.generateTaskId();
            em.executeExchange(b.build(), mLoginXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mLoginXId) {
            handleLoginOutcome(isSuccess, result);
        }
    }


    private void handleLoginOutcome(boolean isSuccess, ForgeExchangeResult result) {
        if (!mAbortLogin) {
            if (isSuccess) {
                int code = result.getCode();

                if (code > 0) {
                    if (code == BasicResponseCodes.Oks.OK.getCode()) {
                        try {
                            JSONObject jobj = new JSONObject(result.getPayload());
                            JSONObject sessionInfo = jobj.optJSONObject("session_info");
                            if (sessionInfo != null) {
                                Session.Info info = Session.Info.fromJson(sessionInfo);

                                int sessionTtl = jobj.getInt("session_ttl");
                                getSession().startSession(sessionTtl, info);
                                mLogger.debug("App login OK");

                                startSession();
                            } else {
                                mStateManager.switchToState(State.LOGIN_FAIL);
                                mLogger.error("Missing session info");
                            }
                        } catch (JSONException e) {
                            mStateManager.switchToState(State.LOGIN_FAIL);
                            mLogger.warn("Login exchange failed because cannot parse JSON");
                        }
                    } else {
                        // unexpected positive code
                        mStateManager.switchToState(State.LOGIN_FAIL);
                    }
                } else if (code == BasicResponseCodes.Errors.UPGRADE_NEEDED.getCode()) {
                    mStateManager.switchToState(State.UPGRADE_NEEDED);
                } else if (code == BasicResponseCodes.Errors.INVALID_LOGIN.getCode()) {
                    mStateManager.switchToState(State.LOGIN_INVALID);
                } else {
                    mStateManager.switchToState(State.LOGIN_FAIL);
                }
            } else {
                mStateManager.switchToState(State.LOGIN_FAIL);
            }
        }
    }
}
