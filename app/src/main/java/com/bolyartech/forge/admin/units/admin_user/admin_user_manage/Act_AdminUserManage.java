package com.bolyartech.forge.admin.units.admin_user.admin_user_manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.DoesLogin;
import com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd.Act_AdminUserChpwd;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ActivityUtils;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_AdminUserManage extends SessionActivity implements DoesLogin, Df_CommWait.Listener {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private static final int ACT_ADMIN_CHPWD = 1;
    public static String PARAM_USER = "user";

    private AdminUser mUser;
    private TextView mTvUsername;
    private TextView mTvName;
    private TextView mTvSuperadmin;
    private TextView mTvDisabled;

    private Res_AdminUserManage mResident;


    @Inject
    Provider<Res_AdminUserManageImpl> mRes_AdminUserManageImplProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_user_manage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDependencyInjector().inject(this);

        mUser = (AdminUser) ActivityUtils.interceptParcelableParam(savedInstanceState, getIntent(), PARAM_USER);
        if (mUser == null) {
            throw new NullPointerException("Missing PARAM_USER");
        }


        initViews(getWindow().getDecorView());
        showData();
    }


    private void initViews(View view) {
        mTvUsername = ViewUtils.findTextViewX(view, R.id.tv_username);
        mTvName = ViewUtils.findTextViewX(view, R.id.tv_name);
        mTvSuperadmin = ViewUtils.findTextViewX(view, R.id.tv_superadmin);
        mTvDisabled = ViewUtils.findTextViewX(view, R.id.tv_disabled);
    }


    private void showData() {
        mTvUsername.setText(mUser.getUsername());
        mTvName.setText(mUser.getName());
        if (mUser.isSuperAdmin()) {
            mTvSuperadmin.setVisibility(View.VISIBLE);
        } else {
            mTvSuperadmin.setVisibility(View.GONE);
        }

        if (mUser.isDisabled()) {
            mTvDisabled.setVisibility(View.VISIBLE);
        } else {
            mTvDisabled.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_AdminUserManage) getResidentComponent();
        handleState(mResident.getState());
    }


    private void handleState(Res_AdminUserManage.State state) {
        mLogger.debug("State: {}", state);

        switch(state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case DISABLING:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case DISABLE_OK:
                mUser = new AdminUser(mUser.getId(),
                        mUser.getUsername(),
                        mResident.getDisableResult(),
                        mUser.isSuperAdmin(),
                        mUser.getName());

                invalidateOptionsMenu();
                mResident.stateAcknowledged();
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                showData();
                break;
            case DISABLE_FAIL:
                mResident.stateAcknowledged();
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                break;
        }
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_AdminUserManageImplProvider.get();
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__admin_user_manage, menu);

        if (getSession().getInfo().isSuperAdmin()) {
            if (mUser.isDisabled()) {
                menu.findItem(R.id.ab_disable).setTitle(R.string.act__admin_user_manage__mi_enable);
            } else {
                menu.findItem(R.id.ab_disable).setTitle(R.string.act__admin_user_manage__mi_disable);
            }
        } else {
            menu.findItem(R.id.ab_disable).setVisible(false);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_disable) {
            if (mUser.isDisabled()) {
                mResident.enableUser(mUser);
            } else {
                mResident.disableUser(mUser);
            }
        } else if (id == R.id.ab_chpwd) {
            Intent intent = new Intent(this, Act_AdminUserChpwd.class);
            intent.putExtra(Act_AdminUserChpwd.PARAM_USER_ID, mUser.getId());
            startActivityForResult(intent, ACT_ADMIN_CHPWD);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARAM_USER, mUser);
    }
}
