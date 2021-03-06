package com.bolyartech.forge.admin.misc;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.AuthenticationResponseCodes;
import com.bolyartech.forge.admin.app.CurrentUser;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.session.Session;
import com.bolyartech.scram_sasl.client.ScramClientFunctionality;
import com.bolyartech.scram_sasl.common.ScramException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


public class LoginHelperImpl implements LoginHelper {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static final String APP_TYPE = "1"; // android

    private final ForgeExchangeManager mForgeExchangeManager;
    private final ScramClientFunctionality mScramClientFunctionality;
    private final Session mSession;
    private final AppConfiguration mAppConfiguration;
    private final CurrentUserHolder mCurrentUserHolder;


    private ForgePostHttpExchangeBuilder mStep2builder;
    private String mUsername;
    private String mPassword;

    private Listener mListener;

    private volatile long mStep1XId;
    private volatile long mStep2XId;

    private volatile boolean mAbortLogin = false;


    @Inject
    public LoginHelperImpl(ForgeExchangeManager forgeExchangeManager,
                           ScramClientFunctionality scramClientFunctionality,
                           Session session,
                           AppConfiguration appConfiguration,
                           CurrentUserHolder currentUserHolder) {

        mForgeExchangeManager = forgeExchangeManager;
        mScramClientFunctionality = scramClientFunctionality;
        mSession = session;
        mAppConfiguration = appConfiguration;
        mCurrentUserHolder = currentUserHolder;
    }


    @Override
    public void initiate(
            ForgePostHttpExchangeBuilder step1builder,
            ForgePostHttpExchangeBuilder step2builder,
            String username, String password,
            Listener listener,
            boolean autologin) {

        mStep2builder = step2builder;
        mUsername = username;
        mPassword = password;
        mListener = listener;

        try {
            String clientFirst = mScramClientFunctionality.prepareFirstMessage(username);
            step1builder.addPostParameter("app_type", "");
            step1builder.addPostParameter("app_version", mAppConfiguration.getAppVersion());
            step1builder.addPostParameter("step", APP_TYPE);
            step1builder.addPostParameter("data", clientFirst);

            mStep1XId = mForgeExchangeManager.executeExchange(step1builder.build());
        } catch (ScramException e) {
            mLogger.error("Scram exception", e);
            mListener.onLoginFail(BasicResponseCodes.Errors.UNSPECIFIED_ERROR);
        }
    }


    @Override
    public void abortLogin() {
        mStep1XId = 0;
        mStep2XId = 0;
        mAbortLogin = true;
    }


    @Override
    public boolean handleExchange(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mStep1XId) {
            handleStep1(isSuccess, result);
            return true;
        } else if (exchangeId == mStep2XId) {
            handleStep2(isSuccess, result);
            return true;
        } else {
            return false;
        }
    }


    private void handleStep2(boolean isSuccess, ForgeExchangeResult result) {
        if (!mAbortLogin) {
            int code = result.getCode();

            if (isSuccess && code == BasicResponseCodes.OK) {
                try {
                    JSONObject jobj = new JSONObject(result.getPayload());
                    int sessionTtl = jobj.getInt("session_ttl");
                    JSONObject sessionInfo = jobj.optJSONObject("session_info");
                    String serverFinal = jobj.getString("final_message");

                    try {
                        if (mScramClientFunctionality.checkServerFinalMessage(serverFinal)) {
                            if (sessionInfo != null) {
                                mSession.startSession(sessionTtl);

                                mCurrentUserHolder.setCurrentUser(
                                        new CurrentUser(sessionInfo.getLong("user_id"),
                                                sessionInfo.getBoolean("super_admin")));

                                mLogger.debug("App login OK");


                                LoginPrefs lp = mAppConfiguration.getLoginPrefs();
                                lp.setUsername(mUsername);
                                lp.setPassword(mPassword);
                                lp.save();

                                mListener.onLoginOk();
                            } else {
                                mLogger.error("Missing session info");
                                mListener.onLoginFail(BasicResponseCodes.Errors.UNSPECIFIED_ERROR);
                            }
                        } else {
                            mListener.onLoginFail(code);
                        }
                    } catch (ScramException e) {
                        mLogger.debug("SCRAM checkServerFinalMessage failed");
                        mListener.onLoginFail(BasicResponseCodes.Errors.UNSPECIFIED_ERROR);
                    }
                } catch (JSONException e) {
                    mLogger.warn("Login exchange failed because cannot parse JSON");
                    mListener.onLoginFail(code);
                }
            } else {
                mListener.onLoginFail(code);
            }
        }
    }


    private void handleStep1(boolean isSuccess, ForgeExchangeResult result) {
        if (!mAbortLogin) {
            if (isSuccess) {
                int code = result.getCode();
                if (code == BasicResponseCodes.OK) {
                    String serverFirst = result.getPayload();
                    try {
                        String clientFinal = mScramClientFunctionality.prepareFinalMessage(mPassword,
                                serverFirst);

                        if (clientFinal != null) {
                            mStep2builder.addPostParameter("step", "2");
                            mStep2builder.addPostParameter("data", clientFinal);

                            mStep2XId = mForgeExchangeManager.executeExchange(mStep2builder.build());

                        } else {
                            mListener.onLoginFail(AuthenticationResponseCodes.Errors.INVALID_LOGIN);
                        }
                    } catch (ScramException e) {
                        mListener.onLoginFail(code);
                    }
                } else {
                    mListener.onLoginFail(code);
                }
            } else {
                mListener.onLoginFail(BasicResponseCodes.Errors.UNSPECIFIED_ERROR);
            }
        }
    }
}
