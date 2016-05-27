package com.bolyartech.forge.admin.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class AcraDaggerModule {
    private final String mAcraUrl;


    public AcraDaggerModule(String acraUrl) {
        mAcraUrl = acraUrl;
    }


    @Provides
    @Named("acra url")
    String provideAcraUrl() {
        return mAcraUrl;
    }
}
