package com.bolyartech.forge.admin.units.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.BasicResponseCodes;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.DoesLogin;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.bolyartech.forge.base.misc.StringUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_Login extends SessionActivity implements DoesLogin {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Provider<Res_LoginImpl> mRes_LoginImplProvider;


    @Inject
    LoginPrefs mLoginPrefs;


    private EditText mEtUsername;
    private EditText mEtPassword;


    private Res_Login mResident;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSession() != null && getSession().isLoggedIn()) {
            mLogger.error("Already logged in. Logout first before attempting new login.");
            finish();
        }

        setContentView(R.layout.act__login);

        getDependencyInjector().inject(this);

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


        ViewUtils.initButton(view, R.id.btn_login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid()) {
                    mResident.login(mEtUsername.getText().toString(), mEtPassword.getText().toString());
                }
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
    public ResidentComponent createResidentComponent() {
        return mRes_LoginImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_Login) getResidentComponent();
        handleState(mResident.getState());
    }


    private void handleState(Res_Login.State state) {
        switch (state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case LOGGING_IN:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case LOGIN_FAIL:
                handleLoginFail();
                break;
            case STARTING_SESSION:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case SESSION_STARTED_OK:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case SESSION_START_FAIL:
                handleError();
                break;
        }
    }


    private void handleLoginFail() {
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
        if (mResident.getLastError() == BasicResponseCodes.Errors.INVALID_LOGIN) {
            MyAppDialogs.showInvalidLoginDialog(getFragmentManager());
        } else {
            MyAppDialogs.showCommProblemDialog(getFragmentManager());
        }
    }


    private void handleError() {
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
        if (mResident.getLastError() != null) {
            switch (mResident.getLastError()) {
                case UPGRADE_NEEDED:
                    MyAppDialogs.showUpgradeNeededDialog(getFragmentManager());
                    break;
            }
        }
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
