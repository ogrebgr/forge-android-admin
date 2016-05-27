package com.bolyartech.forge.admin.units.admin_users_list;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_AdminUsersList extends SessionActivity implements Df_CommWait.Listener {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Provider<Res_AdminUsersListImpl> mRes_AdminUsersListImplProvider;

    private Res_AdminUsersList mResident;

    private ListView mLvAdminUsers;
    private AdminUserAdapter mAdminUserAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_users_list);

        getDependencyInjector().inject(this);

        initViews(getWindow().getDecorView());
    }


    private void initViews(View view) {
        mLvAdminUsers = ViewUtils.findListViewX(view, R.id.lv_admin_users);
        mLvAdminUsers.setEmptyView(ViewUtils.findTextView(view, R.id.tv_empty));
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_AdminUsersListImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_AdminUsersList) getResidentComponent();

        handleState(mResident.getState());
    }


    private void handleState(Res_AdminUsersList.State state) {
        mLogger.debug("State: {}", state);

        switch (state) {
            case IDLE:
                break;
            case WAITING_DATA:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case DATA_OK:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                showData();
                mResident.reset();
                break;
            case DATA_FAIL:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                mResident.reset();
                break;
        }
    }


    private void showData() {
        List<AdminUser> users = mResident.getData();
        if (mAdminUserAdapter == null) {
            mAdminUserAdapter = new AdminUserAdapter(this, 1, users);
            mLvAdminUsers.setAdapter(mAdminUserAdapter);
        } else {
            mAdminUserAdapter.clear();
            mAdminUserAdapter.addAll(users);
            mAdminUserAdapter.notifyDataSetChanged();
        }
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }


    @Override
    public void onCommWaitDialogCancelled() {
        mResident.reset();
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
        }

        return super.onOptionsItemSelected(item);
    }
}
