package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.AppUnitManager;
import com.bolyartech.forge.android.app_unit.UnitManager;
import com.bolyartech.forge.base.session.Session;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class UnitManagerDaggerModule {
    @Provides
    @Singleton
    AppUnitManager provideMyAppUnitManagerForge(Session session) {
        return new AppUnitManager(session);
    }

    @Provides
    @Singleton
    UnitManager provideUnitManager(AppUnitManager my) {
        return my;
    }
}
