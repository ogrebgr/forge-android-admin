package com.bolyartech.forge.admin.units.user.user_manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.DoesLogin;
import com.bolyartech.forge.admin.units.user.user_chpwd.Act_UserChpwd;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ActivityUtils;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_UserManage extends SessionActivity implements DoesLogin, Df_CommWait.Listener {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public static final String PARAM_USER = "user";

    private User mUser;
    private TextView mTvUsername;
    private TextView mTvName;
    private TextView mTvDisabled;

    @Inject
    Provider<Res_UserManageImpl> mRes_UserManageImplProvider;

    private Res_UserManage mResident;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__user_manage);

        mUser = (User) ActivityUtils.interceptParcelableParam(savedInstanceState, getIntent(), PARAM_USER);
        if (mUser == null) {
            throw new IllegalArgumentException("Missing PARAM_USER");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDependencyInjector().inject(this);

        initViews(getWindow().getDecorView());
        showData();
    }


    private void showData() {
        mTvUsername.setText(mUser.getUsername());
        mTvName.setText(mUser.getScreenName());

        if (mUser.isDisabled()) {
            mTvDisabled.setVisibility(View.VISIBLE);
        } else {
            mTvDisabled.setVisibility(View.GONE);
        }
    }


    private void initViews(View view) {
        mTvUsername = ViewUtils.findTextViewX(view, R.id.tv_username);
        mTvName = ViewUtils.findTextViewX(view, R.id.tv_name);
        mTvDisabled = ViewUtils.findTextViewX(view, R.id.tv_disabled);
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_UserManageImplProvider.get();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARAM_USER, mUser);
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_UserManage) getResidentComponent();
        handleState(mResident.getState());
    }


    private void handleState(Res_UserManage.State state) {
        mLogger.debug("State: {}", state);

        switch(state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case DISABLING:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case DISABLE_OK:
                mUser = new User(mUser.getId(),
                        mUser.getUsername(),
                        mResident.getDisableResult(),
                        mUser.getScreenName());

                invalidateOptionsMenu();
                mResident.resetState();
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                showData();
                break;
            case DISABLE_FAIL:
                mResident.resetState();
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__admin_user_manage, menu);

        if (getSession().getInfo().isSuperAdmin()) {
            if (mUser.isDisabled()) {
                menu.findItem(R.id.ab_disable).setTitle(R.string.act__user_manage__mi_enable);
            } else {
                menu.findItem(R.id.ab_disable).setTitle(R.string.act__user_manage__mi_disable);
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
            Intent intent = new Intent(Act_UserManage.this, Act_UserChpwd.class);
            intent.putExtra(Act_UserChpwd.PARAM_USER_ID, mUser.getId());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
