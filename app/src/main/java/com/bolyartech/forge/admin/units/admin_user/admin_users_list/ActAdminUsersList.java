package com.bolyartech.forge.admin.units.admin_user.admin_users_list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.units.admin_user.admin_user_create.ActAdminUserCreate;
import com.bolyartech.forge.admin.units.admin_user.admin_user_manage.ActAdminUserManage;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent.OpState;
import com.bolyartech.forge.android.misc.ActivityResult;
import com.bolyartech.forge.android.misc.ViewUtils;

import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActAdminUsersList extends SessionActivity<ResAdminUsersList> implements
        OperationResidentComponent.Listener,
        Df_CommWait.Listener {


    @SuppressWarnings("FieldCanBeLocal")
    private final String PARAM_REFRESH = "refresh";
    private final int ACT_USER_MANAGE = 1;
    private final int ACT_USER_CREATE = 2;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Provider<ResAdminUsersListAbstract> mRes_AdminUsersListImplProvider;

    private ListView mLvAdminUsers;
    private AdminUsersAdapter mAdminUsersAdapter;

    private ActivityResult mActivityResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_users_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getCurrentUser().isSuperadmin()) {
            initViews(getWindow().getDecorView());
        } else {
            finish();
        }
    }


    private void initViews(View view) {
        mLvAdminUsers = ViewUtils.findListViewX(view, R.id.lv_admin_users);
        mLvAdminUsers.setEmptyView(ViewUtils.findTextView(view, R.id.tv_empty));
        mLvAdminUsers.setOnItemClickListener((parent, view1, position, id) -> {
            AdminUser user = (AdminUser) parent.getItemAtPosition(position);

            Intent intent = new Intent(ActAdminUsersList.this, ActAdminUserManage.class);
            intent.putExtra(ActAdminUserManage.PARAM_USER, user);
            startActivityForResult(intent, ACT_USER_MANAGE);
        });
    }


    @NonNull
    @Override
    public ResAdminUsersList createResidentComponent() {
        return mRes_AdminUsersListImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mActivityResult == null) {
            handleState();
        } else {
            handleActivityResult(mActivityResult);
            mActivityResult = null;
        }
    }


    private void handleState() {
        OpState state = getRes().getOpState();
        mLogger.debug("State: {}", state);

        switch (state) {
            case IDLE:
                MyAppDialogs.hideGenericWaitDialog(getFragmentManager());
                break;
            case BUSY:
                MyAppDialogs.showGenericWaitDialog(getFragmentManager());
                break;
            case ENDED:
                MyAppDialogs.hideGenericWaitDialog(getFragmentManager());
                if (getRes().isSuccess()) {
                    showData();
                } else {
                    MyAppDialogs.showCommProblemDialog(getFragmentManager());
                }
                getRes().ack();
                break;
        }
    }


    private void showData() {
        List<AdminUser> users = getRes().getData();
        if (mAdminUsersAdapter == null) {
            mAdminUsersAdapter = new AdminUsersAdapter(this, 1, users);
            mLvAdminUsers.setAdapter(mAdminUsersAdapter);
        } else {
            mAdminUsersAdapter.clear();
            mAdminUsersAdapter.addAll(users);
            mAdminUsersAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
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
            getRes().loadAdminUsers();
        } else if (id == R.id.ab_create) {
            Intent intent = new Intent(ActAdminUsersList.this, ActAdminUserCreate.class);
            startActivityForResult(intent, ACT_USER_CREATE);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mActivityResult = new ActivityResult(requestCode, resultCode, data);
    }


    private void handleActivityResult(ActivityResult activityResult) {
        if (activityResult.requestCode == ACT_USER_MANAGE) {
            if (activityResult.resultCode == Activity.RESULT_OK) {
                if (activityResult.data.getBooleanExtra(PARAM_REFRESH, false)) {
                    getRes().loadAdminUsers();
                }
            }
        } else if (activityResult.requestCode == ACT_USER_CREATE && activityResult.resultCode == Activity.RESULT_OK) {
            getRes().loadAdminUsers();
        }
    }


    @Override
    public void onResidentOperationStateChanged() {
        handleState();
    }
}
