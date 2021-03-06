package com.bolyartech.forge.admin.dagger;



import com.bolyartech.forge.base.session.Session;
import com.bolyartech.forge.base.session.SessionImpl;

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
