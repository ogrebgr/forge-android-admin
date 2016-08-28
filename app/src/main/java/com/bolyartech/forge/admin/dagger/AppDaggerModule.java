package com.bolyartech.forge.admin.dagger;

import android.content.Context;

import com.bolyartech.forge.admin.app.App;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.LoginPrefsImpl;
import com.bolyartech.forge.base.misc.TimeProvider;
import com.bolyartech.forge.base.misc.TimeProviderImpl;

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
    LoginPrefs provideLoginPrefs(LoginPrefsImpl impl) {
        return impl;
    }


    @Provides
    TimeProvider providesTimeProvider() {
        return new TimeProviderImpl();
    }


    @Provides
    @Singleton
    CurrentUserHolder provideCurrentUserHolder() {
        return new CurrentUserHolder();
    }
}
