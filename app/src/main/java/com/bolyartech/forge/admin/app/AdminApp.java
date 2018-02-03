package com.bolyartech.forge.admin.app;

import android.os.StrictMode;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.dagger.DefaultAppDaggerComponent;
import com.bolyartech.forge.admin.dagger.DependencyInjector;
import com.bolyartech.forge.admin.misc.AcraKeyStoreFactory;
import com.bolyartech.forge.android.app_unit.UnitApplication;
import com.bolyartech.forge.android.task.ForgeAndroidTaskExecutor;
import com.bolyartech.forge.base.exchange.ForgeExchangeManager;
import com.squareup.leakcanary.LeakCanary;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;


/**
 * Created by ogre on 2015-11-15 15:19
 */
@ReportsCrashes(formUri = "placeholder")
public class AdminApp extends UnitApplication {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass()
            .getSimpleName());


    @Inject
    AppUnitManager mAppUnitManager;

    @Inject
    ForgeExchangeManager mForgeExchangeManager;

    @Inject
    Provider<ForgeAndroidTaskExecutor> mForgeAndroidTaskExecutorProvider;


    @Override
    public void onCreate() {
        super.onCreate();

        initInjector();

        if (getResources().getBoolean(R.bool.build_conf_dev_mode)) {
            if (!getResources().getBoolean(R.bool.build_conf_disable_acra)) {
                initAcra();
            }
        }

        if (getResources().getBoolean(R.bool.build_conf_dev_mode)) {
            enableStrictMode();
            LeakCanary.install(this);
        }

        mForgeExchangeManager.addListener(mAppUnitManager);
        mForgeExchangeManager.start(mForgeAndroidTaskExecutorProvider.get());
    }


    @Override
    protected void onInterfaceResumed() {
        super.onInterfaceResumed();

        if (!mForgeExchangeManager.isStarted()) {
            mForgeExchangeManager.addListener(mAppUnitManager);
            mForgeExchangeManager.start(mForgeAndroidTaskExecutorProvider.get());
        }
    }


    @Override
    protected void onInterfacePaused() {
        super.onInterfacePaused();

        mForgeExchangeManager.removeListener(mAppUnitManager);
        mForgeExchangeManager.shutdown();
    }


    protected void initInjector() {
        DependencyInjector.init(DefaultAppDaggerComponent.create(this,
                getResources().getBoolean(R.bool.build_conf_dev_mode)));

        DependencyInjector.getInstance().inject(this);

        onInjectorInitialized();
    }


    protected void onInjectorInitialized() {
    }


    private void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .penaltyDeath()
                .build());

        StrictMode.VmPolicy.Builder b = new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath();
        StrictMode.setVmPolicy(b.build());
    }


    private void initAcra() {
        ConfigurationBuilder b = new ConfigurationBuilder(this);
        b.setKeyStoreFactoryClass(AcraKeyStoreFactory.class);
        b.setAdditionalSharedPreferences("forge", "login prefs");
        b.setFormUri(getString(R.string.build_conf_acra_url));
        b.setExcludeMatchingSharedPreferencesKeys("^Username.*", "^Password.*");
        b.setReportingInteractionMode(ReportingInteractionMode.SILENT);
        b.setAlsoReportToAndroidFramework(true);

        try {
            ACRA.init(this, b.build());
        } catch (ACRAConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
