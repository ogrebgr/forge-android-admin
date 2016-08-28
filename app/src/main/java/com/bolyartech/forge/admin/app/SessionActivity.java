package com.bolyartech.forge.admin.app;

import android.content.Intent;

import com.bolyartech.forge.admin.misc.PerformsLogin;
import com.bolyartech.forge.admin.units.main.Act_Main;
import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.base.session.Session;

import javax.inject.Inject;


abstract public class SessionActivity<T extends ResidentComponent>
        extends UnitBaseActivity<T> {

    @Inject
    Session mSession;

    @Inject
    CurrentUserHolder mCurrentUserHolder;


    @Override
    public void onResume() {
        super.onResume();

        if (!(this instanceof PerformsLogin)) {
            if (!mSession.isLoggedIn()) {
                goHome();
            }
        }
    }


    protected Session getSession() {
        return mSession;
    }


    protected void goHome() {
        Intent intent = new Intent(getApplicationContext(), Act_Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    protected CurrentUser getCurrentUser() {
        return mCurrentUserHolder.getCurrentUser();
    }


}
