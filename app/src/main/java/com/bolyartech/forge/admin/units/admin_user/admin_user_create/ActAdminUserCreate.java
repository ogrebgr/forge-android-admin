package com.bolyartech.forge.admin.units.admin_user.admin_user_create;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.AdminResponseCodes;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.google.common.base.Strings;

import java.text.MessageFormat;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActAdminUserCreate extends SessionActivity<ResAdminUserCreate> implements
        OperationResidentComponent.Listener, Df_CommWait.Listener {


    private EditText mEtUsername;
    private EditText mEtName;
    private EditText mEtPassword;
    private EditText mEtPassword2;
    private CheckBox mCbSuperuser;


    @Inject
    Provider<ResAdminUserCreateImpl> mRes_AdminUserCreateImplProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_user_create);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getCurrentUser().isSuperadmin()) {
            initViews(getWindow().getDecorView());
        } else {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        handleState();
    }


    private void initViews(View view) {
        mEtUsername = ViewUtils.findEditTextX(view, R.id.et_username);
        mEtName = ViewUtils.findEditTextX(view, R.id.et_name);
        mEtPassword = ViewUtils.findEditTextX(view, R.id.et_password);
        mEtPassword2 = ViewUtils.findEditTextX(view, R.id.et_password2);
        mCbSuperuser = ViewUtils.findCheckBoxX(view, R.id.cb_superadmin);
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @NonNull
    @Override
    public ResAdminUserCreate createResidentComponent() {
        return mRes_AdminUserCreateImplProvider.get();
    }


    private void handleState() {
        OperationResidentComponent.OpState state = getRes().getOpState();
        switch(state) {
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
        getRes().ack();
    }


    private void handlerFail() {
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
        int errorCode = getRes().getLastError();

        if (errorCode == AdminResponseCodes.Errors.INVALID_USERNAME.getCode()) {
            mEtUsername.setError(getString(R.string.act__admin_user_create__err_invalid_username));
        } else if (errorCode == AdminResponseCodes.Errors.USERNAME_EXISTS.getCode()) {
            mEtUsername.setError(getString(R.string.act__admin_user_create__err_username_taken_msg));
        } else if (errorCode == AdminResponseCodes.Errors.INVALID_NAME.getCode()) {
            mEtName.setError(getString(R.string.act__admin_user_create__err_invalid_name));
        } else if (errorCode == AdminResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode()) {
            mEtPassword.setError(MessageFormat.format(getString(R.string.act__admin_user_create__err_password_too_short),
                    AdminUser.MIN_PASSWORD_LENGTH));
        } else {
            MyAppDialogs.showCommProblemDialog(getFragmentManager());
        }

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
                getRes().save(mEtUsername.getText().toString(),
                        mEtName.getText().toString(),
                        mEtPassword.getText().toString(),
                        mCbSuperuser.isChecked());
            }

        }

        return super.onOptionsItemSelected(item);
    }


    private boolean check() {
        boolean ret = true;

        if (Strings.isNullOrEmpty(mEtUsername.getText().toString())) {
            mEtUsername.setError(getString(R.string.act__admin_user_create__err_mandatory_field));
            ret = false;
        } else {
            if (!AdminUser.isValidUsername(mEtUsername.getText().toString())) {
                mEtUsername.setError(getString(R.string.act__admin_user_create__err_invalid_username));
                ret = false;
            }
        }

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

        if (Strings.isNullOrEmpty(mEtName.getText().toString())) {
            mEtName.setError(getString(R.string.act__admin_user_create__err_mandatory_field));
            ret = false;
        } else {
            if (!AdminUser.isValidName(mEtName.getText().toString())) {
                mEtName.setError(getString(R.string.act__admin_user_create__err_invalid_name));
                ret = false;
            }
        }

        return ret;
    }


    @Override
    public void onResidentOperationStateChanged() {
        handleState();
    }
}
