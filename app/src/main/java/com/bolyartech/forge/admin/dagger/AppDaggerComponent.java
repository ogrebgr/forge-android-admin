package com.bolyartech.forge.admin.dagger;


import com.bolyartech.forge.admin.app.AdminApp;
import com.bolyartech.forge.admin.units.admin_user.admin_user_chpwd.ActAdminUserChpwd;
import com.bolyartech.forge.admin.units.admin_user.admin_user_create.ActAdminUserCreate;
import com.bolyartech.forge.admin.units.admin_user.admin_user_manage.ActAdminUserManage;
import com.bolyartech.forge.admin.units.admin_user.admin_users_list.ActAdminUsersList;
import com.bolyartech.forge.admin.units.login.ActLogin;
import com.bolyartech.forge.admin.units.main.ActMain;
import com.bolyartech.forge.admin.units.user.user_chpwd.ActUserChpwd;
import com.bolyartech.forge.admin.units.user.user_manage.ActUserManage;
import com.bolyartech.forge.admin.units.user.users.ActUsers;

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
        LoginModule.class
})
@Singleton
public interface AppDaggerComponent {
    void inject(AdminApp adminApp);
    void inject(ActMain act);
    void inject(ActLogin act);
    void inject(ActAdminUsersList act);
    void inject(ActAdminUserManage act);
    void inject(ActAdminUserCreate act);
    void inject(ActAdminUserChpwd act);
    void inject(ActUsers act);
    void inject(ActUserManage act);
    void inject(ActUserChpwd act);
}
