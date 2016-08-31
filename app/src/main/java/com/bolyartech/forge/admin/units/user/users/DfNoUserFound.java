package com.bolyartech.forge.admin.units.user.users;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.bolyartech.forge.admin.R;


public class DfNoUserFound extends DialogFragment {
    public static final String DIALOG_TAG = "Df_NoUserFound";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setMessage(R.string.dlg__no_user_found_msg);
        b.setCancelable(false);
        b.setNeutralButton(R.string.global_btn_close, null);
        return b.create();
    }
}
