package com.bolyartech.forge.admin.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.bolyartech.forge.admin.R;


public class Df_LoggingIn extends DialogFragment {
    public static final String DIALOG_TAG = "Df_LoggingIn";
    private Listener mListener;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getTargetFragment() instanceof Listener) {
            mListener = (Listener) getTargetFragment();
        } else if (getActivity() instanceof Listener) {
            mListener = (Listener) getActivity();
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        ProgressDialog d = new ProgressDialog(getActivity());
        d.setIndeterminate(true);
        d.setCancelable(false);
        d.setMessage(getString(R.string.dlg__loggin_in));
        d.setCancelable(true);
        return d;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (mListener != null) {
            mListener.onLoggingInDialogCancelled();
        }
    }


    public interface Listener {
        void onLoggingInDialogCancelled();
    }
}
