package com.bolyartech.forge.admin.app;

import android.support.annotation.NonNull;

import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.UnitActivity;
import com.bolyartech.forge.android.app_unit.UnitActivityDelegate;


/**
 * Created by ogre on 2016-01-10 12:45
 */
abstract public class UnitBaseActivity<T extends ResidentComponent>
        extends BaseActivity implements UnitActivity<T> {

    private UnitActivityDelegate<T> mDelegate = new UnitActivityDelegate<>();


    @Override
    public void setResident(@NonNull T resident) {
        mDelegate.setResident(resident);
    }


    @Override
    @NonNull
    public T getResident() {
        return mDelegate.getResident();
    }


    @Override
    @NonNull
    public T getRes() {
        return mDelegate.getRes();
    }
}
