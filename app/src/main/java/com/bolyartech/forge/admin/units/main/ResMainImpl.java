package com.bolyartech.forge.admin.units.main;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.misc.LoginHelper;
import com.bolyartech.forge.android.app_unit.AbstractMultiOperationResidentComponent;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgeGetHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeManagerListener;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.session.Session;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class ResMainImpl extends AbstractMultiOperationResidentComponent<ResMain.Operation> implements ResMain,
        ForgeExchangeManagerListener, LoginHelper.Listener  {


    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final AppConfiguration mAppConfiguration;

    private final ForgeExchangeHelper mForgeExchangeHelper;
    private final Session mSession;
    private final Provider<LoginHelper> mLoginHelperProvider;

    private LoginHelper mLoginHelper;

    private int mLoginError;


    @Inject
    public ResMainImpl(
                        ForgeExchangeHelper forgeExchangeHelper,
                        Session session,
                        AppConfiguration appConfiguration,
                        Provider<LoginHelper> loginHelperProvider) {

        mAppConfiguration = appConfiguration;
        mForgeExchangeHelper = forgeExchangeHelper;
        mSession = session;
        mLoginHelperProvider = loginHelperProvider;
    }


    @Override
    public void login() {
        loginActual();
    }



    @Override
    public void abortLogin() {
        if (mLoginHelper != null) {
            mLoginHelper.abortLogin();
        }
        abort();
    }


    @Override
    public void logout() {
        if (isIdle()) {
            switchToBusyState(Operation.LOGOUT);
            mSession.logout();
            ForgeGetHttpExchangeBuilder b = mForgeExchangeHelper.createForgeGetHttpExchangeBuilder("logout");
            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            em.executeExchange(b.build(), em.generateTaskId());

            switchToEndedStateSuccess();
        } else {
            mLogger.warn("Not in IDLE state");
        }
    }


    @Override
    public void onLoginOk() {
        switchToEndedStateSuccess();
    }


    @Override
    public void onLoginFail(int code) {
        mLoginError = code;
        switchToEndedStateFail();
    }


    private void loginActual() {
        if (isIdle()) {
            switchToBusyState(Operation.LOGIN);
            mLoginHelper = mLoginHelperProvider.get();
            mLoginHelper.initiate(mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login"),
                    mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login"),
                    mAppConfiguration.getLoginPrefs().getUsername(),
                    mAppConfiguration.getLoginPrefs().getPassword(),
                    this,
                    true);

//            mAbortLogin = false;
//            switchToBusyState(Operation.LOGIN);
//
//            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login");
//            b.addPostParameter("username", mAppConfiguration.getLoginPrefs().getUsername());
//            b.addPostParameter("password", mAppConfiguration.getLoginPrefs().getPassword());
//            b.addPostParameter("app_type", "1");
//            b.addPostParameter("app_version", mAppConfiguration.getAppVersion());
//            b.addPostParameter("session_info", "1");
//
//            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
//            mLoginXId = em.generateTaskId();
//            em.executeExchange(b.build(), mLoginXId);
        } else {
            mLogger.warn("Not in IDLE state");
        }
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (mLoginHelper != null) {
            mLoginHelper.handleExchange(exchangeId, isSuccess, result);
        }
    }


//    private void handleLoginOutcome(boolean isSuccess, ForgeExchangeResult result) {
//        if (!mAbortLogin) {
//            if (isSuccess) {
//                int code = result.getCode();
//
//                if (code > 0) {
//                    if (code == BasicResponseCodes.OK) {
//                        try {
//                            JSONObject jobj = new JSONObject(result.getPayload());
//                            JSONObject sessionInfo = jobj.optJSONObject("session_info");
//                            if (sessionInfo != null) {
//                                mCurrentUserHolder.setCurrentUser(new CurrentUser(sessionInfo.getLong("user_id"),
//                                        sessionInfo.getBoolean("super_admin")));
//
//                                int sessionTtl = jobj.getInt("session_ttl");
//                                mSession.startSession(sessionTtl);
//                                mLogger.debug("App login OK");
//
//                                switchToEndedStateSuccess();
//                            } else {
//                                mLoginError = LoginError.FAILED;
//                                switchToEndedStateFail();
//                                mLogger.error("Missing session info");
//                            }
//                        } catch (JSONException e) {
//                            mLoginError = LoginError.FAILED;
//                            switchToEndedStateFail();
//                            mLogger.warn("Login exchange failed because cannot parse JSON");
//                        }
//                    } else {
//                        // unexpected positive code
//                        mLoginError = LoginError.FAILED;
//                        switchToEndedStateFail();
//                    }
//                } else if (code == BasicResponseCodes.Errors.UPGRADE_NEEDED) {
//                    mLoginError = LoginError.UPGRADE_NEEDED;
//                    switchToEndedStateFail();
//                } else if (code == AuthenticationResponseCodes.Errors.INVALID_LOGIN) {
//                    mLoginError = LoginError.INVALID_LOGIN;
//                    switchToEndedStateFail();
//                } else {
//                    mLoginError = LoginError.FAILED;
//                    switchToEndedStateFail();
//                }
//            } else {
//                mLoginError = LoginError.FAILED;
//                switchToEndedStateFail();
//            }
//        }
//    }


    @Override
    public int getLoginError() {
        return mLoginError;
    }
}
