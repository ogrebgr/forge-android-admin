package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.MyAppConfiguration;
import com.bolyartech.forge.admin.app.MyAppConfigurationImpl;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class AppInfoDaggerModule {
    private final String mAppVersion;


    public AppInfoDaggerModule(String appVersion) {
        mAppVersion = appVersion;
    }


    @Provides
    @Named("app version")
    String provideAppVersion() {
        return mAppVersion;
    }

    @Provides
    @Singleton
    MyAppConfiguration provideAppConfiguration(MyAppConfigurationImpl impl) {
        return impl;
    }
}
