package com.bolyartech.forge.admin.units.admin_user_manage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bolyartech.forge.admin.R;
import com.bolyartech.forge.admin.data.AdminUser;
import com.bolyartech.forge.android.misc.ActivityUtils;


public class Act_AdminUserManage extends AppCompatActivity {
    public static String PARAM_USER = "user";

    private AdminUser mUser;
    private TextView mTvUsername;
    private TextView mTvName;
    private TextView mTvSuperadmin;
    private TextView mTvDisabled;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act__admin_user_manage);

        mUser = (AdminUser) ActivityUtils.interceptParcelableParam(savedInstanceState, getIntent(), PARAM_USER);
        if (mUser == null) {
            throw new NullPointerException("Missing PARAM_USER");
        }

        initViews(getWindow().getDecorView());
    }


    private void initViews(View view) {

    }


    @Override
    protected void onResume() {
        super.onResume();


    }
}
