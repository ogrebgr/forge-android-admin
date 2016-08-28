package com.bolyartech.forge.admin.units.login;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.AuthorizationResponseCodes;
import com.bolyartech.forge.admin.app.CurrentUser;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.android.app_unit.AbstractOperationResidentComponent;
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


/**
 * Created by ogre on 2016-01-05 14:26
 */
public class Res_LoginImpl extends AbstractOperationResidentComponent implements Res_Login {
    private volatile long mLoginXId;
    private volatile boolean mAbortLogin = false;

    private String mLastUsedUsername;
    private String mLastUsedPassword;


    private final AppConfiguration mAppConfiguration;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private LoginError mLoginError;
    private final ForgeExchangeHelper mForgeExchangeHelper;
    private final Session mSession;
    private final CurrentUserHolder mCurrentUserHolder;


    @Inject
    public Res_LoginImpl(AppConfiguration appConfiguration,
                         ForgeExchangeHelper forgeExchangeHelper,
                         Session session,
                         CurrentUserHolder currentUserHolder) {

        mAppConfiguration = appConfiguration;
        mForgeExchangeHelper = forgeExchangeHelper;
        mSession = session;
        mCurrentUserHolder = currentUserHolder;
    }


    @Override
    public void login(String username, String password) {
        if (isIdle()) {
            switchToBusyState();
            mLastUsedUsername = username;
            mLastUsedPassword = password;

            ForgePostHttpExchangeBuilder b = mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login");
            b.addPostParameter("username", username);
            b.addPostParameter("password", password);
            b.addPostParameter("app_type", "1");
            b.addPostParameter("app_version", mAppConfiguration.getAppVersion());

            ForgeExchangeManager em = mForgeExchangeHelper.getExchangeManager();
            mLoginXId = em.generateTaskId();
            em.executeExchange(b.build(), mLoginXId);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }



    @Override
    public void abortLogin() {
        mAbortLogin = true;
        switchToIdleState();
    }


    @Override
    public LoginError getLoginError() {
        return mLoginError;
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mLoginXId) {
            mLoginError = null;
            if (!mAbortLogin) {
                if (isSuccess) {
                    int code = result.getCode();

                    if (code > 0) {
                        if (code == BasicResponseCodes.Oks.OK.getCode()) {
                            try {
                                JSONObject jobj = new JSONObject(result.getPayload());
                                int sessionTtl = jobj.getInt("session_ttl");
                                JSONObject sessionInfo = jobj.optJSONObject("session_info");
                                if (sessionInfo != null) {
                                    mCurrentUserHolder.setCurrentUser(new CurrentUser(sessionInfo.getLong("user_id"),
                                            sessionInfo.getBoolean("super_admin")));

                                    mSession.startSession(sessionTtl);

                                    LoginPrefs lp = mAppConfiguration.getLoginPrefs();
                                    lp.setUsername(mLastUsedUsername);
                                    lp.setPassword(mLastUsedPassword);
                                    lp.save();

                                    mLogger.debug("App login OK");
                                    switchToCompletedStateSuccess();
                                } else {
                                    mLogger.error("Missing session info");
                                    mLoginError = LoginError.FAILED;
                                    switchToCompletedStateFail();
                                }
                            } catch (JSONException e) {
                                mLogger.warn("Login exchange failed because cannot parse JSON");
                                mLoginError = LoginError.FAILED;
                                switchToCompletedStateFail();
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
    }
}
