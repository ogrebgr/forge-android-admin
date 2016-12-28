package com.bolyartech.forge.admin.units.user.users;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.data.User;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.units.user.user_manage.ActUserManage;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent;
import com.bolyartech.forge.android.app_unit.OperationResidentComponent.OpState;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.google.common.base.Strings;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;


public class ActUsers extends SessionActivity<ResUsers> implements OperationResidentComponent.Listener,
        Df_CommWait.Listener {


    private final int ACT_USER_MANAGE = 1;

    @Inject
    Provider<Res_UsersImpl> mRes_UserListImplProvider;

    private UsersAdapter mUsersAdapter;

    private EditText mEtSearch;
    private ListView mLvUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__users);

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

        mLvUsers.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ActUsers.this, ActUserManage.class);
            intent.putExtra(ActUserManage.PARAM_USER, (User) parent.getItemAtPosition(position));
            startActivity(intent);
        });
    }


    private void handleState() {
        OpState state = getRes().getOpState();
        switch (state) {
            case IDLE:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                break;
            case BUSY:
                MyAppDialogs.showCommWaitDialog(getFragmentManager());
                break;
            case ENDED:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
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
        List<User> users = getRes().getData();
        if (mUsersAdapter == null) {
            mUsersAdapter = new UsersAdapter(this, 1, users);
            mLvUsers.setAdapter(mUsersAdapter);
        } else {
            mUsersAdapter.clear();
            mUsersAdapter.addAll(users);
            mUsersAdapter.notifyDataSetChanged();
        }

        if (users.size() == 0) {
            showNoUserFoundDialog(getFragmentManager());
        }
    }


    private void initViews(View view) {
        mEtSearch = ViewUtils.findEditTextX(view, R.id.et_search);
        ViewUtils.initButton(view, R.id.btn_search, new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                if (getRes().isIdle()) {
                    if (!Strings.isNullOrEmpty(mEtSearch.getText().toString().trim())) {
                        getRes().searchForUser(mEtSearch.getText().toString());
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


    @NonNull
    @Override
    public ResUsers createResidentComponent() {
        return mRes_UserListImplProvider.get();
    }


    public static void showNoUserFoundDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(DfNoUserFound.DIALOG_TAG) == null) {
            DfNoUserFound fra = new DfNoUserFound();
            fra.show(fm, DfNoUserFound.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    @Override
    public void onResidentOperationStateChanged() {
        handleState();
    }
}