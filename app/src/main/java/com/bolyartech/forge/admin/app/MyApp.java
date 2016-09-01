package com.bolyartech.forge.admin.app;

import android.os.StrictMode;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.dagger.DefaultAppDaggerComponent;
import com.bolyartech.forge.admin.dagger.DependencyInjector;
import com.bolyartech.forge.android.app_unit.UnitApplication;
import com.bolyartech.forge.android.task.ForgeAndroidTaskExecutor;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.squareup.leakcanary.LeakCanary;

import org.acra.ACRA;
import org.acra.ACRAConfiguration;
import org.acra.annotation.ReportsCrashes;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.inject.Inject;
import javax.inject.Provider;


/**
 * Created by ogre on 2015-11-15 15:19
 */
@ReportsCrashes(formUri = "placeholder")
public class MyApp extends UnitApplication {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass()
            .getSimpleName());


    @Inject
    MyAppUnitManager mMyAppUnitManager;

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

        mForgeExchangeManager.addListener(mMyAppUnitManager);
        mForgeExchangeManager.start(mForgeAndroidTaskExecutorProvider.get());
    }


    @Override
    protected void onInterfaceResumed() {
        super.onInterfaceResumed();

        if (!mForgeExchangeManager.isStarted()) {
            mForgeExchangeManager.addListener(mMyAppUnitManager);
            mForgeExchangeManager.start(mForgeAndroidTaskExecutorProvider.get());
        }
    }


    @Override
    protected void onInterfacePaused() {
        super.onInterfacePaused();

        mForgeExchangeManager.removeListener(mMyAppUnitManager);
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
        ACRAConfiguration conf = ACRA.getNewDefaultConfig(this);
        conf.setFormUri(getString(R.string.build_conf_acra_url));
        conf.setAdditionalSharedPreferences(new String[]{"glasuvalnik"});
        conf.setAdditionalSharedPreferences(new String[]{"login prefs"});
        conf.setExcludeMatchingSharedPreferencesKeys(new String[]{"^Username.*",
                "^Password.*"});

        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(getResources().openRawResource(R.raw.forge_skeleton), getString(R.string.bks_keystore_password).toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new AssertionError("Cannot initialize SSL cert for ACRA");
        }

        conf.setKeyStore(ks);
        ACRA.init(this, conf);
    }
}