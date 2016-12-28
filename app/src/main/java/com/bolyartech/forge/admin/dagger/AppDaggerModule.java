package com.bolyartech.forge.admin.dagger;

import android.content.Context;

import com.bolyartech.forge.admin.app.AdminApp;
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
    private final AdminApp mMyAdminApp;


    public AppDaggerModule(AdminApp myAdminApp) {
        mMyAdminApp = myAdminApp;
    }

    @Provides
    @ForApplication
    Context providesApplicationContext() {
        return mMyAdminApp;
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
