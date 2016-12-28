package com.bolyartech.forge.admin.units.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.AuthenticationResponseCodes;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.OpSessionActivity;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.PerformsLogin;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;
import com.bolyartech.forge.base.misc.StringUtils;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActLogin extends OpSessionActivity<ResLogin> implements OperationResidentComponent.Listener,
        PerformsLogin {

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Provider<ResLoginImpl> mRes_LoginImplProvider;


    @Inject
    LoginPrefs mLoginPrefs;


    private EditText mEtUsername;
    private EditText mEtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);

        if (getSession() != null && getSession().isLoggedIn()) {
            mLogger.error("Already logged in. Logout first before attempting new login.");
            finish();
        }

        setContentView(R.layout.act__login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view = getWindow().getDecorView();
        initViews(view);
    }


    private void initViews(View view) {
        mEtUsername = ViewUtils.findEditTextX(view, R.id.et_username);
        mEtPassword = ViewUtils.findEditTextX(view, R.id.et_password);

        mEtUsername.setText(mLoginPrefs.getUsername());
        mEtPassword.setText(mLoginPrefs.getPassword());


        ViewUtils.initButton(view, R.id.btn_login, v -> {
            if (isDataValid()) {
                getRes().login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
            }
        });
    }


    @SuppressWarnings("RedundantIfStatement")
    private boolean isDataValid() {
        if (StringUtils.isEmpty(mEtUsername.getText().toString())) {
            return false;
        }

        if (StringUtils.isEmpty(mEtPassword.getText().toString())) {
            return false;
        }

        return true;
    }


    @Override
    public ResLogin createResidentComponent() {
        return mRes_LoginImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        handleState();
    }


    public void handleState() {

        switch (getRes().getOpState()) {
            case IDLE:
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                break;
            case BUSY:
                MyAppDialogs.showLoggingInDialog(getFragmentManager());
                break;
            case ENDED:
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                handleCompleted();
                getRes().ack();
                break;
        }
    }


    private void handleCompleted() {
        if (getRes().isSuccess()) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            switch (getRes().getLastError()) {
                case AuthenticationResponseCodes.Errors.INVALID_LOGIN:
                    MyAppDialogs.showInvalidLoginDialog(getFragmentManager());
                    break;
                case BasicResponseCodes.Errors.UPGRADE_NEEDED:
                    MyAppDialogs.showUpgradeNeededDialog(getFragmentManager());
                    break;
                default:
                    MyAppDialogs.showCommProblemDialog(getFragmentManager());
                    break;
            }
        }
    }
}
