package com.bolyartech.forge.admin.dagger;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.app.App;
import com.bolyartech.forge.admin.app.AppUnitManager;
import com.bolyartech.forge.admin.misc.LoggingInterceptor;
import com.bolyartech.forge.android.task.ForgeAndroidTaskExecutor;
import com.bolyartech.forge.base.task.ForgeExchangeManager;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cookie;
import okhttp3.OkHttpClient;


public class DefaultAppDaggerComponent {
    private DefaultAppDaggerComponent() {
        throw new AssertionError("No instances allowed");
    }


    public static AppDaggerComponent create(App app, boolean debug) {
        HttpsDaggerModule httpsDaggerModule = new HttpsDaggerModule(createOkHttpClient(app, debug));

        AppUnitManager AppUnitManager = new AppUnitManager();

        return DaggerAppDaggerComponent.builder().
                appDaggerModule(createAppDaggerModule(app)).
                appInfoDaggerModule(createAppInfoDaggerModule(app)).
                exchangeDaggerModule(createExchangeDaggerModule(AppUnitManager, app)).
                httpsDaggerModule(httpsDaggerModule).
                unitManagerDaggerModule(new UnitManagerDaggerModule(AppUnitManager)).
                build();

    }


    public static OkHttpClient createOkHttpClient(App app, boolean debug) {
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        if (debug) {
            b.addInterceptor(new LoggingInterceptor());
            TrustManager tm = createDummyTrustManager();

            try {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{tm}, new SecureRandom());

                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                b.sslSocketFactory(sslSocketFactory);

                b.hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {

            try {
                KeyStore keyStore = createKeystore(app);

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                keyManagerFactory.init(keyStore, app.getString(R.string.bks_keystore_password).toCharArray());
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
                b.sslSocketFactory(sslContext.getSocketFactory());
            } catch (KeyStoreException | KeyManagementException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), createFakePersistor());
        b.cookieJar(cookieJar);

        return b.build();
    }


    private static CookiePersistor createFakePersistor() {
        return new CookiePersistor() {
            @Override
            public List<Cookie> loadAll() {
                return new ArrayList<>();
            }


            @Override
            public void saveAll(Collection<Cookie> collection) {
            }


            @Override
            public void removeAll(Collection<Cookie> collection) {
            }


            @Override
            public void clear() {
            }
        };
    }


    private static TrustManager createDummyTrustManager() {
        return new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }


            @SuppressLint("TrustAllX509TrustManager")
            public void checkClientTrusted(
                    X509Certificate[] certs, String authType) {
            }


            @SuppressLint("TrustAllX509TrustManager")
            public void checkServerTrusted(
                    X509Certificate[] certs, String authType) {
            }
        };
    }


    public static KeyStore createKeystore(App app) {
        InputStream is = app.getResources().openRawResource(R.raw.forge_skeleton);
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("BKS");
            ks.load(is, app.getString(R.string.bks_keystore_password).toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new IllegalStateException("Cannot create the keystore");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                System.out.print(e.getMessage());
            }
        }

        return ks;
    }


    public static ExchangeDaggerModule createExchangeDaggerModule(AppUnitManager AppUnitManager, App app) {
        ForgeAndroidTaskExecutor te = new ForgeAndroidTaskExecutor();
        ForgeExchangeManager fem = new ForgeExchangeManager(te);

        fem.addListener(AppUnitManager);
        fem.start();

        return new ExchangeDaggerModule(app.getString(R.string.build_conf_base_url),
                fem,
                te);
    }


    public static AppDaggerModule createAppDaggerModule(App app) {
        return new AppDaggerModule(app);
    }


    public static AppInfoDaggerModule createAppInfoDaggerModule(App app) {
        PackageInfo pInfo;
        try {
            pInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
            if (pInfo == null) {
                throw new NullPointerException("pInfo is null");
            }
        } catch (PackageManager.NameNotFoundException e1) {
            throw new IllegalStateException(e1);
        }

        return new AppInfoDaggerModule(String.valueOf(pInfo.versionCode));
    }


}
