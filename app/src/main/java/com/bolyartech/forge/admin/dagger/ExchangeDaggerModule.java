package com.bolyartech.forge.admin.dagger;

import com.bolyartech.forge.android.task.ForgeAndroidTaskExecutor;
import com.bolyartech.forge.base.exchange.ResultProducer;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelper;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeHelperImpl;
import com.bolyartech.forge.base.exchange.forge.ForgeExchangeResult;
import com.bolyartech.forge.base.exchange.forge.ForgeHeaderResultProducer;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.bolyartech.forge.base.task.TaskExecutor;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


/**
 * Created by ogre on 2015-07-15
 */
@Module(includes = {HttpsDaggerModule.class})
public class ExchangeDaggerModule {
    private final String mBaseUrl;


    public ExchangeDaggerModule(String baseUrl) {
        mBaseUrl = baseUrl;
    }


    @Provides
    @Named("base url")
    String provideBaseUrl() {
        return mBaseUrl;
    }


    @Provides
    @Singleton
    public ForgeExchangeManager provideForgeExchangeManager(ForgeAndroidTaskExecutor te) {
        return new ForgeExchangeManager(te);
    }


    @Provides
    @Singleton
    @Named("forge result producer")
    public ResultProducer<ForgeExchangeResult> provideForgeResultProducer(ForgeHeaderResultProducer rp) {
        return rp;
    }


    @Provides
    @Singleton
    public ForgeAndroidTaskExecutor provideTaskExecutor() {
        return new ForgeAndroidTaskExecutor();
    }


    @Provides
    @Singleton
    ForgeExchangeHelper provideForgeExchangeHelper(ForgeExchangeHelperImpl impl) {
        return impl;
    }
}
