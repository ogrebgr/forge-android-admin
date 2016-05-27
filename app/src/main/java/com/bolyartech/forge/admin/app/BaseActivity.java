package com.bolyartech.forge.admin.app;

import android.support.v7.app.AppCompatActivity;

import com.bolyartech.forge.admin.dagger.AppDaggerComponent;
import com.bolyartech.forge.admin.dagger.DependencyInjector;


/**
 * Created by ogre on 2016-01-05 12:49
 */
abstract public class BaseActivity extends AppCompatActivity {
    protected AppDaggerComponent getDependencyInjector() {
        return DependencyInjector.getInstance();
    }

}
