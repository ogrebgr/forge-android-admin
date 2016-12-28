package com.bolyartech.forge.admin.dagger;

import com.bolyartech.forge.admin.misc.LoginHelper;
import com.bolyartech.forge.admin.misc.LoginHelperImpl;
import com.bolyartech.scram_sasl.client.ScramClientFunctionality;
import com.bolyartech.scram_sasl.client.ScramClientFunctionalityImpl;

import dagger.Module;
import dagger.Provides;


@Module
public class LoginModule {
    private static final String DIGEST = "SHA-512";
    private static final String HMAC = "HmacSHA512";


    @Provides
    ScramClientFunctionality provideScramClientFunctionality() {
        return new ScramClientFunctionalityImpl(DIGEST, HMAC);
    }

    @Provides
    LoginHelper provideLoginHelper(LoginHelperImpl impl) {
        return impl;
    }

}
