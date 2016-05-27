package com.bolyartech.forge.admin.dagger;



import com.bolyartech.forge.admin.app.Session;
import com.bolyartech.forge.admin.app.SessionImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module
public class SessionDaggerModule {
    @Provides
    @Singleton
    Session provideSession(SessionImpl session) {
        return session;
    }
}
