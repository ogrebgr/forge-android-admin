package com.bolyartech.forge.admin.dagger;

import android.content.Context;

import com.bolyartech.forge.admin.app.MyApp;
import com.bolyartech.forge.admin.app.CurrentUserHolder;
import com.bolyartech.forge.admin.app.LoginPrefs;
import com.bolyartech.forge.admin.app.LoginPrefsImpl;
import com.bolyartech.forge.android.misc.AndroidTimeProvider;
import com.bolyartech.forge.base.misc.TimeProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-11-15 15:20
 */
@Module
public class AppDaggerModule {
    private final MyApp mMyMyApp;


    public AppDaggerModule(MyApp myMyApp) {
        mMyMyApp = myMyApp;
    }

    @Provides
    @ForApplication
    Context providesApplicationContext() {
        return mMyMyApp;
    }


    @Provides
    @Singleton
    LoginPrefs provideLoginPrefs(LoginPrefsImpl impl) {
        return impl;
    }


    @Provides
    TimeProvider providesTimeProvider() {
        return new AndroidTimeProvider();
    }


    @Provides
    @Singleton
    CurrentUserHolder provideCurrentUserHolder() {
        return new CurrentUserHolder();
    }
}
