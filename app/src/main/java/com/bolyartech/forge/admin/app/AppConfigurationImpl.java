package com.bolyartech.forge.admin.app;


import javax.inject.Inject;
import javax.inject.Named;


public class AppConfigurationImpl implements AppConfiguration {
    private final String mAppVersion;

    private final LoginPrefs mLoginPrefs;


    @Inject
    public AppConfigurationImpl(@Named("app version") String appVersion,
                                LoginPrefs loginPrefs
                                ) {
        mAppVersion = appVersion;
        mLoginPrefs = loginPrefs;
    }


    @Override
    public String getAppVersion() {
        return mAppVersion;
    }



    @Override
    public LoginPrefs getLoginPrefs() {
        return mLoginPrefs;
    }
}
