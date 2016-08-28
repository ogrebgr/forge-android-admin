package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.AppUnitManager;
import com.bolyartech.forge.admin.app.AuthorizationResponseCodes;
import com.bolyartech.forge.admin.app.CurrentUser;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.android.app_unit.AbstractOperationResidentComponent;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.session.Session;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class Res_MainImpl extends AbstractOperationResidentComponent implements Res_Main {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final AppConfiguration mAppConfiguration;
    private final NetworkInfoProvider mNetworkInfoProvider;

    private volatile long mLoginXId;
    private volatile boolean mAbortLogin = false;

    private final ForgeExchangeHelper mForgeExchangeHelper;
    private final Session mSession;
    private final CurrentUserHolder mCurrentUserHolder;

    private LoginError mLoginError;

    @Inject
    AppUnitManager mMyAppUnitManager;

    @Inject
    ForgeExchangeManager mForgeExchangeManager;



    @Inject
    public Res_MainImpl(
                        AppUnitManager myAppUnitManager,
                        ForgeExchangeManager forgeExchangeManager,
                        ForgeExchangeHelper forgeExchangeHelper,
                        Session session,
                        NetworkInfoProvider networkInfoProvider,
                        AppConfiguration appConfiguration,
                        CurrentUserHolder currentUserHolder) {

        mMyAppUnitManager = myAppUnitManager;
        mForgeExchangeManager = forgeExchangeManager;
        mAppConfiguration = appConfiguration;
        mNetworkInfoProvider = networkInfoProvider;
        mForgeExchangeHelper = forgeExchangeHelper;
        mSession = session;
        mCurrentUserHolder = currentUserHolder;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mForgeExchangeManager.addListener(mMyAppUnitManager);
        mForgeExchangeManager.start();


        if (mNetworkInfoProvider.isConnected()) {
            init();
        }
    }


    @Override
    public void onActivityFinishing() {
        super.onActivityFinishing();
        mForgeExchangeManager.removeListener(mMyAppUnitManager);
        mForgeExchangeManager.shutdown();
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
    public void abortLogin() {
        mAbortLogin = true;
        switchToIdleState();
    }


    @Override
    public void logout() {
        mSession.logout();
        Thread t = new Thread(() -> {
            ForgeGetHttpExchangeBuilder b = mForgeExchangeHelper.createForgeGetHttpExchangeBuilder("logout");
            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            em.executeExchange(b.build(), em.generateTaskId());
        });
        t.start();
        switchToIdleState();
    }


    @Override
    public void internetAvailable() {

    }


    @Override
    public void onConnectivityChange() {

    }


    private void loginActual() {
        if (isIdle()) {
            mAbortLogin = false;
            switchToBusyState();

            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login");
            b.addPostParameter("username", mAppConfiguration.getLoginPrefs().getUsername());
            b.addPostParameter("password", mAppConfiguration.getLoginPrefs().getPassword());
            b.addPostParameter("app_type", "1");
            b.addPostParameter("app_version", mAppConfiguration.getAppVersion());
            b.addPostParameter("session_info", "1");

            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mLoginXId = em.generateTaskId();
            em.executeExchange(b.build(), mLoginXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
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
                                mCurrentUserHolder.setCurrentUser(new CurrentUser(sessionInfo.getLong("user_id"),
                                        sessionInfo.getBoolean("super_admin")));

                                int sessionTtl = jobj.getInt("session_ttl");
                                mSession.startSession(sessionTtl);
                                mLogger.debug("App login OK");

                                switchToCompletedStateSuccess();
                            } else {
                                mLoginError = LoginError.FAILED;
                                switchToCompletedStateFail();
                                mLogger.error("Missing session info");
                            }
                        } catch (JSONException e) {
                            mLoginError = LoginError.FAILED;
                            switchToCompletedStateFail();
                            mLogger.warn("Login exchange failed because cannot parse JSON");
                        }
                    } else {
                        // unexpected positive code
                        mLoginError = LoginError.FAILED;
                        switchToCompletedStateFail();
                    }
                } else if (code == BasicResponseCodes.Errors.UPGRADE_NEEDED.getCode()) {
                    mLoginError = LoginError.UPGRADE_NEEDED;
                    switchToCompletedStateFail();
                } else if (code == AuthorizationResponseCodes.Errors.INVALID_LOGIN.getCode()) {
                    mLoginError = LoginError.INVALID_LOGIN;
                    switchToCompletedStateFail();
                } else {
                    mLoginError = LoginError.FAILED;
                    switchToCompletedStateFail();
                }
            } else {
                mLoginError = LoginError.FAILED;
                switchToCompletedStateFail();
            }
        }
    }


    @Override
    public LoginError getLoginError() {
        return mLoginError;
    }
}
