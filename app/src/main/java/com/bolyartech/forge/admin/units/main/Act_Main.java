package com.bolyartech.forge.admin.units.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.SessionActivity;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.DoesLogin;
import com.bolyartech.forge.admin.units.admin_user.admin_users_list.Act_AdminUsersList;
import com.bolyartech.forge.admin.units.login.Act_Login;
import com.bolyartech.forge.admin.units.user.users.Act_Users;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.StateChangedEvent;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.squareup.otto.Subscribe;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


public class Act_Main extends SessionActivity implements DoesLogin, Df_CommWait.Listener {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private Res_Main mResident;

    private View mViewNoInet;
    private View mViewNotLoggedIn;
    private View mViewLoggedIn;
    private TextView mTvLoggedInAs;

    @Inject
    NetworkInfoProvider mNetworkInfoProvider;

    @Inject
    LoginPrefs mLoginPrefs;

    @Inject
    Provider<Res_MainImpl> mRes_MainImplProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getDependencyInjector().inject(this);

        initViews();
    }


    private void initViews() {
        View view = getWindow().getDecorView();

        mViewNoInet = ViewUtils.findViewX(view, R.id.v_no_inet);
        mViewNotLoggedIn = ViewUtils.findViewX(view, R.id.v_not_logged_in);
        mViewLoggedIn = ViewUtils.findViewX(view, R.id.v_logged_in);

        mTvLoggedInAs = ViewUtils.findTextViewX(view, R.id.tv_logged_in_as);

        ViewUtils.initButton(view, R.id.btn_login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResident.login();
            }
        });

        ViewUtils.initButton(view, R.id.btn_admin_users, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Act_Main.this, Act_AdminUsersList.class);
                startActivity(intent);
            }
        });

        ViewUtils.initButton(view, R.id.btn_users, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Act_Main.this, Act_Users.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act__main, menu);

        if (getSession().isLoggedIn()) {
            menu.findItem(R.id.ab_logout).setVisible(true);
            menu.findItem(R.id.ab_login_as).setVisible(false);
        } else {
            menu.findItem(R.id.ab_logout).setVisible(false);
            menu.findItem(R.id.ab_login_as).setVisible(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ab_logout) {
            mResident.logout();
        } else if (id == R.id.ab_login_as) {
            Intent intent = new Intent(Act_Main.this, Act_Login.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCommWaitDialogCancelled() {
        mResident.abortLogin();
    }


    @Override
    public ResidentComponent createResidentComponent() {
        return mRes_MainImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        mResident = (Res_Main) getResidentComponent();

        handleState(mResident.getState());
    }


    private synchronized void handleState(Res_Main.State state) {
        mLogger.debug("State: {}", state);
        invalidateOptionsMenu();
        switch (state) {
            case IDLE:
                if (mNetworkInfoProvider.isConnected()) {
                    if (getSession().isLoggedIn()) {
                        screenModeLoggedIn();
                    } else {
                        screenModeNotLoggedIn();
                    }
                } else {
                    screenModeNoInet();
                }

                break;
            case SESSION_STARTED_OK:
                MyAppDialogs.hideCommWaitDialog(getFragmentManager());
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                screenModeLoggedIn();
                mResident.resetState();
                break;
            case SESSION_START_FAIL:
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                mResident.resetState();
                screenModeNotLoggedIn();
                break;
            case LOGGING_IN:
                MyAppDialogs.showLoggingInDialog(getFragmentManager());
                break;
            case LOGIN_FAIL:
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                MyAppDialogs.showCommProblemDialog(getFragmentManager());
                mResident.resetState();
                screenModeNotLoggedIn();
                break;
            case LOGIN_INVALID:
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                MyAppDialogs.showInvalidAutologinDialog(getFragmentManager());
                mResident.resetState();
                screenModeNotLoggedIn();
                break;
            case UPGRADE_NEEDED:
                MyAppDialogs.showUpgradeNeededDialog(getFragmentManager());
                break;
        }
    }


    private void screenModeNoInet() {
        mViewNoInet.setVisibility(View.VISIBLE);
        mViewNotLoggedIn.setVisibility(View.GONE);
        mViewLoggedIn.setVisibility(View.GONE);
    }


    private void screenModeNotLoggedIn() {
        mViewNoInet.setVisibility(View.GONE);
        mViewNotLoggedIn.setVisibility(View.VISIBLE);
        mViewLoggedIn.setVisibility(View.GONE);

//        mBtnLogin.setVisibility(View.VISIBLE);
    }


    private void screenModeLoggedIn() {
        mViewNoInet.setVisibility(View.GONE);
        mViewNotLoggedIn.setVisibility(View.GONE);
        mViewLoggedIn.setVisibility(View.VISIBLE);

//        mBtnLogin.setVisibility(View.GONE);

        mTvLoggedInAs.setText(Html.fromHtml(String.format(getString(R.string.act__main__tv_logged_in), mLoginPrefs.getUsername())));
    }


    @Subscribe
    public void onStateChangedEvent(StateChangedEvent ev) {
        handleState(mResident.getState());
    }
}
