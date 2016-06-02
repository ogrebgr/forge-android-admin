package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.AdminResponseCodes;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ActivityUtils;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

import java.text.MessageFormat;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_AdminUserChpwd extends SessionActivity implements Df_CommWait.Listener {
    public static final String PARAM_USER_ID = "user id";
    private EditText mEtPassword;
    private EditText mEtPassword2;

    @Inject
    Provider<Res_AdminUserChpwdImpl> mRes_AdminUserChpwdImplProvider;

    private Res_AdminUserChpwd mResident;

    private long mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_user_chpwd);

        mUserId = ActivityUtils.interceptLongParam(savedInstanceState, getIntent(), PARAM_USER_ID);
        if (mUserId == -1) {
            throw new IllegalArgumentException("Missing PARAM_USER_ID");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDependencyInjector().inject(this);

        if (getSession().getInfo().isSuperAdmin()) {
            initViews(getWindow().getDecorView());
        } else {
            finish();
        }
    }


    private void initViews(View view) {
        mEtPassword = ViewUtils.findEditTextX(view, R.id.et_password);
        mEtPassword2 = ViewUtils.findEditTextX(view, R.id.et_password2);
    }


    @Override
    public void onResume() {
        super.onResume();
        mResident = (Res_AdminUserChpwd) getResidentComponent();
        handleState(mResident.getState());
    }


    private void handleState(Res_AdminUserChpwd.State state) {
        switch (state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case SAVING:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case SAVE_OK:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                setResult(Activity.RESULT_OK);
                finish();
                break;
            case SAVE_FAIL:
                handlerFail();
                break;
        }
    }


    private void handlerFail() {
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
        int errorCode = mResident.getLastError();

        if (errorCode == AdminResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode()) {
            mEtPassword.setError(MessageFormat.format(getString(R.string.act__admin_user_create__err_password_too_short),
                    AdminUser.MIN_PASSWORD_LENGTH));
        }
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_AdminUserChpwdImplProvider.get();
    }


    private boolean check() {
        boolean ret = true;


        if (Strings.isNullOrEmpty(mEtPassword.getText().toString())) {
            mEtPassword.setError(getString(R.string.act__admin_user_create__err_mandatory_field));
            ret = false;
        }

        if (Strings.isNullOrEmpty(mEtPassword2.getText().toString())) {
            mEtPassword2.setError(getString(R.string.act__admin_user_create__err_mandatory_field));
            ret = false;
        } else {
            if (!AdminUser.isValidPasswordLength(mEtPassword.getText().toString())) {
                mEtPassword.setError(MessageFormat.format(getString(R.string.act__admin_user_create__err_password_too_short),
                        AdminUser.MIN_PASSWORD_LENGTH));
                ret = false;
            }

            if (!mEtPassword.getText().toString().equals(mEtPassword2.getText().toString())) {
                mEtPassword2.setError(getString(R.string.act__admin_user_create__err_passwords_dont_match));
                mEtPassword.setText("");
                mEtPassword2.setText("");

                ret = false;
            }
        }


        return ret;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__admin_user_chpwd, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_save) {
            if (check()) {
                mResident.save(mUserId, mEtPassword.getText().toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PARAM_USER_ID, mUserId);
    }
}
