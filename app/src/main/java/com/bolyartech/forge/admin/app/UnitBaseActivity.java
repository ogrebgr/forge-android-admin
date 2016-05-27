package com.bolyartech.forge.admin.app;

import com.bolyartech.forge.android.app_unit.ResidentComponent;
import com.bolyartech.forge.android.app_unit.UnitActivity;


/**
 * Created by ogre on 2016-01-10 12:45
 */
abstract public class UnitBaseActivity extends BaseActivity implements UnitActivity {
    private ResidentComponent mResidentComponent;


    @Override
    public void setResidentComponent(ResidentComponent res) {
        mResidentComponent = res;
    }


    @Override
    public ResidentComponent getResidentComponent() {
        return mResidentComponent;
    }
}
