package com.bolyartech.forge.admin.dagger;

import android.content.Context;

import com.bolyartech.forge.android.misc.NetworkInfoProvider;
import com.bolyartech.forge.android.misc.NetworkInfoProviderImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class NetworkInfoProviderDaggerModule {
    @Provides
    @Singleton
    NetworkInfoProvider provideNetworkInfoProvider(@ForApplication Context context) {
        return new NetworkInfoProviderImpl(context);
    }
}
