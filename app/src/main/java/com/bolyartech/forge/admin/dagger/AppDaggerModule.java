package com.bolyartech.forge.admin.dagger;

import android.content.Context;

import com.bolyartech.forge.admin.app.App;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.LoginPrefsImpl;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-11-15 15:20
 */
@Module
public class AppDaggerModule {
    private final App mMyApp;


    public AppDaggerModule(App myApp) {
        mMyApp = myApp;
    }

    @Provides
    @ForApplication
    Context providesApplicationContext() {
        return mMyApp;
    }


    @Provides
    @Singleton
    Bus provideOttoBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    LoginPrefs provideLoginPrefs(LoginPrefsImpl impl) {
        return impl;
    }
}
