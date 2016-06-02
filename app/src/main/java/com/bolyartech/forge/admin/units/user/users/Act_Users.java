package com.bolyartech.forge.admin.units.user.users;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.google.common.base.Strings;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_Users extends SessionActivity implements Df_CommWait.Listener {
    private final int ACT_USER_MANAGE = 1;

    @Inject
    Provider<Res_UsersImpl> mRes_UserListImplProvider;

    private Res_Users mResident;

    private volatile Runnable mOnResumePendingAction;
    private UsersAdapter mUsersAdapter;

    private EditText mEtSearch;
    private ListView mLvUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__users);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDependencyInjector().inject(this);

        if (getSession().getInfo().isSuperAdmin()) {
            initViews(getWindow().getDecorView());
        } else {
            finish();
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_Users) getResidentComponent();

        if (mOnResumePendingAction == null) {
            handleState(mResident.getState());
        } else {
            runOnUiThread(mOnResumePendingAction);
        }
    }


    private void handleState(Res_Users.State state) {
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
                mResident.resetState();
                break;
            case DATA_FAIL:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                mResident.resetState();
                break;
        }
    }


    private void showData() {
        List<User> users = mResident.getData();
        if (mUsersAdapter == null) {
            mUsersAdapter = new UsersAdapter(this, 1, users);
            mLvUsers.setAdapter(mUsersAdapter);
        } else {
            mUsersAdapter.clear();
            mUsersAdapter.addAll(users);
            mUsersAdapter.notifyDataSetChanged();
        }

    }


    private void initViews(View view) {
        mEtSearch = ViewUtils.findEditTextX(view, R.id.et_search);
        ViewUtils.initButton(view, R.id.btn_search, new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                if (mResident.getState() == Res_Users.State.IDLE) {
                    if (!Strings.isNullOrEmpty(mEtSearch.getText().toString().trim())) {
                        mResident.searchForUser(mEtSearch.getText().toString());
                    }
                }
            }
        });

        mLvUsers = ViewUtils.findListViewX(view, R.id.lv_users);
    }


    @Override
    public void onCommWaitDialogCancelled() {
        finish();
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_UserListImplProvider.get();
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }
}