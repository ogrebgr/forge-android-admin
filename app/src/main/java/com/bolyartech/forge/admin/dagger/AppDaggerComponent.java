package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.App;
import com.bolyartech.forge.admin.units.admin_users_list.Act_AdminUsersList;
import com.bolyartech.forge.admin.units.login.Act_Login;
import com.bolyartech.forge.admin.units.main.Act_Main;

import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by ogre on 2015-10-04
 */

@Component(modules = {AppDaggerModule.class,
        AppInfoDaggerModule.class,
        SessionDaggerModule.class,
        UnitManagerDaggerModule.class,
        NetworkInfoProviderDaggerModule.class,
        ExchangeDaggerModule.class,
})
@Singleton
public interface AppDaggerComponent {
    void inject(App app);
    void inject(Act_Main act);
    void inject(Act_Login act);
    void inject(Act_AdminUsersList act);
}
