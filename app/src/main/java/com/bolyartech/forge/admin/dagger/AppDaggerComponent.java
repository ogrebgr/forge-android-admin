package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.App;
import com.bolyartech.forge.admin.units.admin_user.admin_user_create.Act_AdminUserCreate;
import com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd.Act_AdminUserChpwd;
import com.bolyartech.forge.admin.units.admin_user.admin_user_manage.Act_AdminUserManage;
import com.bolyartech.forge.admin.units.admin_user.admin_users_list.Act_AdminUsersList;
import com.bolyartech.forge.admin.units.login.Act_Login;
import com.bolyartech.forge.admin.units.main.Act_Main;
import com.bolyartech.forge.admin.units.user.user_chpwd.Act_UserChpwd;
import com.bolyartech.forge.admin.units.user.user_manage.Act_UserManage;
import com.bolyartech.forge.admin.units.user.users.Act_Users;

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
    void inject(Act_AdminUserManage act);
    void inject(Act_AdminUserCreate act);
    void inject(Act_AdminUserChpwd act);
    void inject(Act_Users act);
    void inject(Act_UserManage act);
    void inject(Act_UserChpwd act);
}
