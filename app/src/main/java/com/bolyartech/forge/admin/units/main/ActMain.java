package com.bolyartech.forge.admin.units.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.AuthenticationResponseCodes;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.OpSessionActivity;
import com.bolyartech.forge.admin.dialogs.Df_CommWait;
import com.bolyartech.forge.admin.dialogs.MyAppDialogs;
import com.bolyartech.forge.admin.misc.PerformsLogin;
import com.bolyartech.forge.admin.units.admin_user.admin_users_list.ActAdminUsersList;
import com.bolyartech.forge.admin.units.login.ActLogin;
import com.bolyartech.forge.admin.units.user.users.ActUsers;
import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.android.misc.ViewUtils;
import com.bolyartech.forge.base.exchange.forge.BasicResponseCodes;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;

import static com.bolyartech.forge.android.app_unit.OperationResidentComponent.*;


public class ActMain extends OpSessionActivity<ResMain> implements Listener,
        PerformsLogin, Df_CommWait.Listener {

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private View mViewNoInet;
    private View mViewNotLoggedIn;
    private View mViewLoggedIn;
    private TextView mTvLoggedInAs;

    @Inject
    NetworkInfoProvider mNetworkInfoProvider;

    @Inject
    LoginPrefs mLoginPrefs;

    @Inject
    Provider<ResMainImpl> mRes_MainImplProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDependencyInjector().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.act__main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
    }


    private void initViews() {
        View view = getWindow().getDecorView();

        mViewNoInet = ViewUtils.findViewX(view, R.id.v_no_inet);
        mViewNotLoggedIn = ViewUtils.findViewX(view, R.id.v_not_logged_in);
        mViewLoggedIn = ViewUtils.findViewX(view, R.id.v_logged_in);

        mTvLoggedInAs = ViewUtils.findTextViewX(view, R.id.tv_logged_in_as);

        ViewUtils.initButton(view, R.id.btn_login, v -> {
            if (mLoginPrefs.hasLoginCredentials()) {
                getRes().login();
            } else {
                Intent intent = new Intent(ActMain.this, ActLogin.class);
                startActivity(intent);
            }
        });

        ViewUtils.initButton(view, R.id.btn_admin_users, v -> {
            Intent intent = new Intent(ActMain.this, ActAdminUsersList.class);
            startActivity(intent);
        });

        ViewUtils.initButton(view, R.id.btn_users, v -> {
            Intent intent = new Intent(ActMain.this, ActUsers.class);
            startActivity(intent);
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
            getRes().logout();
        } else if (id == R.id.ab_login_as) {
            Intent intent = new Intent(ActMain.this, ActLogin.class);
            startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCommWaitDialogCancelled() {
        getRes().abortLogin();
    }


    @NonNull
    @Override
    public ResMain createResidentComponent() {
        return mRes_MainImplProvider.get();
    }


    @Override
    public void onResume() {
        super.onResume();

        handleState();
    }


    @Override
    public synchronized void handleState() {
        mLogger.debug("State: {}", getRes().getOpState());
        invalidateOptionsMenu();
        switch (getRes().getOpState()) {
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
            case BUSY:
                MyAppDialogs.showLoggingInDialog(getFragmentManager());
                break;
            case ENDED:
                MyAppDialogs.hideLoggingInDialog(getFragmentManager());
                handleCompleted();
                getRes().ack();
                break;
        }
    }


    private void handleCompleted() {
        if (getRes().isSuccess()) {
            screenModeLoggedIn();
        } else {
            switch (getRes().getLoginError()) {
                case AuthenticationResponseCodes.Errors.INVALID_LOGIN:
                    screenModeNotLoggedIn();
                    MyAppDialogs.showInvalidAutologinDialog(getFragmentManager());
                    break;
                case BasicResponseCodes.Errors.UPGRADE_NEEDED:
                    MyAppDialogs.showUpgradeNeededDialog(getFragmentManager());
                    break;
                default:
                    MyAppDialogs.showCommProblemDialog(getFragmentManager());
                    screenModeNotLoggedIn();
                    break;
            }
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
}
