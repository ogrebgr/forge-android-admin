package com.bolyartech.forge.admin.units.login;

import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.CurrentUser;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.misc.LoginHelper;
import com.bolyartech.forge.admin.units.main.ResMain;
import com.bolyartech.forge.android.app_unit.AbstractSideEffectOperationResidentComponent;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.bolyartech.forge.base.exchange.builders.ForgePostHttpExchangeBuilder;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.session.Session;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


/**
 * Created by ogre on 2016-01-05 14:26
 */
public class ResLoginImpl extends AbstractSideEffectOperationResidentComponent<Void, Integer> implements ResLogin,
        LoginHelper.Listener {


    private final AppConfiguration mAppConfiguration;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final ForgeExchangeHelper mForgeExchangeHelper;
    private final Provider<LoginHelper> mLoginHelperProvider;

    private LoginHelper mLoginHelper;


    @Inject
    public ResLoginImpl(AppConfiguration appConfiguration,
                        ForgeExchangeHelper forgeExchangeHelper,
                        Provider<LoginHelper> loginHelperProvider) {

        mAppConfiguration = appConfiguration;
        mForgeExchangeHelper = forgeExchangeHelper;
        mLoginHelperProvider = loginHelperProvider;
    }


    @Override
    public void login(String username, String password) {
        if (isIdle()) {
            switchToBusyState();
            mLoginHelper = mLoginHelperProvider.get();
            mLoginHelper.initiate(mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login"),
                    mForgeExchangeHelper.createForgePostHttpExchangeBuilder("login"),
                    username,
                    password,
                    this,
                    true);
        } else {
            throw new IllegalStateException("Not in IDLE state");
        }
    }


    @Override
    public void abortLogin() {
        if (mLoginHelper != null) {
            mLoginHelper.abortLogin();
        }
        abort();
    }


    @Override
    public void onExchangeOutcome(long exchangeId, boolean isSuccess, ForgeExchangeResult result) {
        if (mLoginHelper != null) {
            mLoginHelper.handleExchange(exchangeId, isSuccess, result);
        }
    }


    @Override
    public void onLoginOk() {
        switchToEndedStateSuccess(null);
    }


    @Override
    public void onLoginFail(int code) {
        switchToEndedStateFail(code);
    }
}