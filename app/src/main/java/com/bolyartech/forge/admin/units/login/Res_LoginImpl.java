package com.bolyartech.forge.admin.units.login;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.ForgeExchangeHelper;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionResidentComponent;
import com.bolyartech.forge.android.app_unit.StateManager;
import com.bolyartech.forge.android.app_unit.StateManagerImpl;
import com.bolyartech.forge.android.misc.AndroidEventPoster;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.base.exchange.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.task.ForgeExchangeManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


/**
 * Created by ogre on 2016-01-05 14:26
 */
public class Res_LoginImpl extends SessionResidentComponent implements Res_Login {
    private final StateManager<State> mStateManager;
    private BasicResponseCodes.Errors mLastError;

    private volatile long mLoginXId;
    private volatile boolean mAbortLogin = false;

    private String mLastUsedUsername;
    private String mLastUsedPassword;


    private final AppConfiguration mAppConfiguration;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());


    @Inject
    public Res_LoginImpl(AppConfiguration appConfiguration,
                         ForgeExchangeHelper forgeExchangeHelper,
                         Session session,
                         NetworkInfoProvider networkInfoProvider,
                         AndroidEventPoster androidEventPoster) {

        super(forgeExchangeHelper, session, networkInfoProvider, androidEventPoster);

        mAppConfiguration = appConfiguration;

        mStateManager = new StateManagerImpl<>(androidEventPoster, State.IDLE);
    }


    @Override
    public State getState() {
        return mStateManager.getState();
    }


    @Override
    public void login(String username, String password) {
        mStateManager.switchToState(State.LOGGING_IN);
        mLastUsedUsername = username;
        mLastUsedPassword = password;

        ForgePostHttpExchangeBuilder b = createForgePostHttpExchangeBuilder("login");
        b.addPostParameter("username", username);
        b.addPostParameter("password", password);
        b.addPostParameter("app_type", "1");
        b.addPostParameter("app_version", mAppConfiguration.getAppVersion());

        ForgeExchangeManager em = getForgeExchangeManager();
        mLoginXId = em.generateTaskId();
        em.executeExchange(b.build(), mLoginXId);
    }


    @Override
    public BasicResponseCodes.Errors getLastError() {
        return mLastError;
    }


    @Override
    public void abortLogin() {
        mAbortLogin = true;
        mStateManager.switchToState(State.IDLE);
    }


    @Override
    public void onSessionExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (exchangeId == mLoginXId) {
            mLastError = null;
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
                                    getSession().startSession(sessionTtl, Session.Info.fromJson(sessionInfo));
                                    mLogger.debug("App login OK");

                                    LoginPrefs lp = mAppConfiguration.getLoginPrefs();
                                    lp.setUsername(mLastUsedUsername);
                                    lp.setPassword(mLastUsedPassword);
                                    lp.save();

                                    startSession();
                                } else {
                                    mLogger.error("Missing session info");
                                    mStateManager.switchToState(State.LOGIN_FAIL);

                                }
                            } catch (JSONException e) {
                                mLogger.warn("Login exchange failed because cannot parse JSON");
                                mStateManager.switchToState(State.LOGIN_FAIL);
                            }
                        } else {
                            // unexpected positive code
                            mStateManager.switchToState(State.LOGIN_FAIL);
                        }
                    } else {
                        mLogger.warn("Login exchange failed with code {}", code);
                        mLastError = BasicResponseCodes.Errors.fromInt(code);
                        mStateManager.switchToState(State.LOGIN_FAIL);
                    }
                } else {
                    mStateManager.switchToState(State.LOGIN_FAIL);
                }
            }
        }
    }


    private void startSession() {
        // here is the place to initiate additional exchanges that retrieve app state/messages/etc
        mStateManager.switchToState(State.SESSION_STARTED_OK);
    }

}
