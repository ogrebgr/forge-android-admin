package com.bolyartech.forge.admin.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.bolyartech.forge.admin.R;


/**
 * Created by ogre on 2015-10-13
 */
public class Df_InvalidLogin extends DialogFragment {
    public static final String DIALOG_TAG = "Df_InvalidLogin";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setIcon(R.drawable.dlg_icon_failure);
        b.setTitle(R.string.dlg__invalid_login__title);
        b.setMessage(R.string.dlg__invalid_login__msg);
        b.setCancelable(false);
        b.setNeutralButton(R.string.global_btn_ok, null);
        return b.create();
    }
}