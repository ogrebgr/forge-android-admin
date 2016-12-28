package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.AppConfiguration;
import com.bolyartech.forge.admin.app.AppConfigurationImpl;

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
    AppConfiguration provideAppConfiguration(AppConfigurationImpl impl) {
        return impl;
    }
}
