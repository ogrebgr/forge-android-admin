package com.bolyartech.forge.admin.app;

import android.content.Intent;

import com.bolyartech.forge.admin.units.main.Act_Main;
import com.bolyartech.forge.admin.misc.DoesLogin;
import com.squareup.otto.Bus;

import org.slf4j.LoggerFactory;

import javax.inject.Inject;


abstract public class SessionActivity extends UnitBaseActivity {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    Session mSession;

    @Inject
    Bus mBus;


    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);

        if (!(this instanceof DoesLogin)) {
            if (!mSession.isLoggedIn()) {
                mLogger.warn("Session EXPIRED. Going home.");
                goHome();
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    public Session getSession() {
        return mSession;
    }


    public void goHome() {
        Intent intent = new Intent(getApplicationContext(), Act_Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
