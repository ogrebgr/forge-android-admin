package com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.OpSessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.AdminResponseCodes;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.misc.ActivityUtils;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.google.common.base.Strings;

import java.text.MessageFormat;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActAdminUserChpwd extends OpSessionActivity<ResAdminUserChpwd> implements
        Df_CommWait.Listener, OperationResidentComponent.Listener {


    public static final String PARAM_USER_ID = "user id";
    private EditText mEtPassword;
    private EditText mEtPassword2;

    @Inject
    Provider<ResAdminUserChpwdImpl> mRes_AdminUserChpwdImplProvider;

    private long mUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_user_chpwd);

        mUserId = ActivityUtils.interceptLongParam(savedInstanceState, getIntent(), PARAM_USER_ID, -1);
        if (mUserId == -1) {
            throw new IllegalArgumentException("Missing PARAM_USER_ID");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getCurrentUser().isSuperadmin()) {
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
        handleState();
    }


    @Override
    public void handleState() {
        switch (getRes().getOpState()) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case BUSY:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case ENDED:
                if (getRes().isSuccess()) {
                    MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    handlerFail();
                }
                break;
        }
    }


    private void handlerFail() {
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
        Integer errorCode = getRes().getLastError();

        if (errorCode != null && errorCode == AdminResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode()) {
            mEtPassword.setError(MessageFormat.format(getString(R.string.act__admin_user_create__err_password_too_short),
                    AdminUser.MIN_PASSWORD_LENGTH));
        }
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @Override
    public ResAdminUserChpwd createResidentComponent() {
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
                getRes().save(mUserId, mEtPassword.getText().toString());
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PARAM_USER_ID, mUserId);
    }
}
