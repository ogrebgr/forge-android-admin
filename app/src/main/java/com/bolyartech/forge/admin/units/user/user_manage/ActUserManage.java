package com.bolyartech.forge.admin.units.user.user_manage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.OpSessionActivity;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.units.user.user_chpwd.ActUserChpwd;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent.OpState;
import com.bolyartech.forge.android.misc.ActivityUtils;
import com.bolyartech.forge.android.misc.ViewUtils;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActUserManage extends OpSessionActivity<ResUserManage> implements
        OperationResidentComponent.Listener, Df_CommWait.Listener {


    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public static final String PARAM_USER = "user";

    private User mUser;
    private TextView mTvUsername;
    private TextView mTvName;
    private TextView mTvDisabled;

    @Inject
    Provider<ResUserManageImpl> mRes_UserManageImplProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__user_manage);

        mUser = (User) ActivityUtils.interceptParcelableParam(savedInstanceState, getIntent(), PARAM_USER);
        if (mUser == null) {
            throw new IllegalArgumentException("Missing PARAM_USER");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


    @NonNull
    @Override
    public ResUserManage createResidentComponent() {
        return mRes_UserManageImplProvider.get();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PARAM_USER, mUser);
    }




    @Override
    public void onResume() {
        super.onResume();

        handleState();
    }


    @Override
    public void handleState() {
        OpState state = getRes().getOpState();

        mLogger.debug("State: {}", state);

        switch(state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case BUSY:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case ENDED:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                handleCompleted();
                getRes().ack();
                break;
        }
    }


    private void handleCompleted() {
        if (getRes().isSuccess()) {
            mUser = new User(mUser.getId(),
                    mUser.getUsername(),
                    getRes().getDisableResult(),
                    mUser.getScreenName());

            invalidateOptionsMenu();
            showData();
        } else {
            MyAppDialogs.showCommProblemDialog(getFragmentManager());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__admin_user_manage, menu);

        if (getCurrentUser().isSuperadmin()) {
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
                getRes().enableUser(mUser);
            } else {
                getRes().disableUser(mUser);
            }
        } else if (id == R.id.ab_chpwd) {
            Intent intent = new Intent(ActUserManage.this, ActUserChpwd.class);
            intent.putExtra(ActUserChpwd.PARAM_USER_ID, mUser.getId());
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
