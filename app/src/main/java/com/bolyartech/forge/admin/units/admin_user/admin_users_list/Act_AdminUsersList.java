package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.units.admin_user.admin_user_create.Act_AdminUserCreate;
import com.bolyartech.forge.admin.units.admin_user.admin_user_manage.Act_AdminUserManage;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_AdminUsersList extends SessionActivity implements Df_CommWait.Listener {
    @SuppressWarnings("FieldCanBeLocal")
    private final String PARAM_REFRESH = "refresh";
    private final int ACT_USER_MANAGE = 1;
    private final int ACT_USER_CREATE = 2;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Provider<Res_AdminUsersListImpl> mRes_AdminUsersListImplProvider;

    private Res_AdminUsersList mResident;

    private ListView mLvAdminUsers;
    private AdminUsersAdapter mAdminUsersAdapter;

    private volatile Runnable mOnResumePendingAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_users_list);

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
        mLvAdminUsers = ViewUtils.findListViewX(view, R.id.lv_admin_users);
        mLvAdminUsers.setEmptyView(ViewUtils.findTextView(view, R.id.tv_empty));
        mLvAdminUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AdminUser user = (AdminUser) parent.getItemAtPosition(position);

                Intent intent = new Intent(Act_AdminUsersList.this, Act_AdminUserManage.class);
                intent.putExtra(Act_AdminUserManage.PARAM_USER, user);
                startActivityForResult(intent, ACT_USER_MANAGE);
            }
        });
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_AdminUsersListImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_AdminUsersList) getResidentComponent();

        if (mOnResumePendingAction == null) {
            handleState(mResident.getState());
        } else {
            runOnUiThread(mOnResumePendingAction);
        }
    }


    private void handleState(Res_AdminUsersList.State state) {
        mLogger.debug("State: {}", state);

        switch (state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case WAITING_DATA:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case DATA_OK:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                showData();
                mResident.stateHandled();
                break;
            case DATA_FAIL:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                mResident.stateHandled();
                break;
        }
    }


    private void showData() {
        List<AdminUser> users = mResident.getData();
        if (mAdminUsersAdapter == null) {
            mAdminUsersAdapter = new AdminUsersAdapter(this, 1, users);
            mLvAdminUsers.setAdapter(mAdminUsersAdapter);
        } else {
            mAdminUsersAdapter.clear();
            mAdminUsersAdapter.addAll(users);
            mAdminUsersAdapter.notifyDataSetChanged();
        }
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    public void onCommWaitDialogCancelled() {
        mResident.stateHandled();
        MyAppDialogs.hideCommWaitDialog(getFragmentManager());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__admin_users_list, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_refresh) {
            mResident.loadAdminUsers();
        } else if (id == R.id.ab_create) {
            Intent intent = new Intent(Act_AdminUsersList.this, Act_AdminUserCreate.class);
            startActivityForResult(intent, ACT_USER_CREATE);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACT_USER_MANAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getBooleanExtra(PARAM_REFRESH, false)) {
                    createPendingRefresh();
                }
            }
        } else if (requestCode == ACT_USER_CREATE && resultCode == Activity.RESULT_OK) {
            createPendingRefresh();
        }
    }


    private void createPendingRefresh() {
        if (mOnResumePendingAction == null) {
            mOnResumePendingAction = new Runnable() {
                @Override
                public void run() {
                    mOnResumePendingAction = null;
                    mResident.loadAdminUsers();
                }
            };
        } else {
            throw new IllegalStateException("mOnResumePendingAction is not null");
        }
    }
}
