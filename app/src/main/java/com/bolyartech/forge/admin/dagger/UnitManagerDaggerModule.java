package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.AppUnitManager;
import com.bolyartech.forge.android.app_unit.UnitManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class UnitManagerDaggerModule {
    private final AppUnitManager mMyAppUnitManager;


    public UnitManagerDaggerModule(AppUnitManager myAppUnitManager) {
        mMyAppUnitManager = myAppUnitManager;
    }


    @Provides
    @Singleton
    UnitManager provideUnitManager() {
        return mMyAppUnitManager;
    }
}
