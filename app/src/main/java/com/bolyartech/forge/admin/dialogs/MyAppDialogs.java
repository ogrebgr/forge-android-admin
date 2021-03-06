package com.bolyartech.forge.admin.dialogs;


import android.app.DialogFragment;
import android.app.FragmentManager;


public class MyAppDialogs {

    // Non-instantiable utility class
    private MyAppDialogs() {
        throw new AssertionError("No instances allowed");
    }


    public static void showNoInetDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_NoInet.DIALOG_TAG) == null) {
            Df_NoInet fra = new Df_NoInet();
            fra.show(fm, Df_NoInet.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showInvalidLoginDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_InvalidLogin.DIALOG_TAG) == null) {
            Df_InvalidLogin fra = new Df_InvalidLogin();
            fra.show(fm, Df_InvalidLogin.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showInvalidAutologinDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_InvalidAutologin.DIALOG_TAG) == null) {
            Df_InvalidAutologin fra = new Df_InvalidAutologin();
            fra.show(fm, Df_InvalidAutologin.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showUpgradeNeededDialog(FragmentManager fm) {
        //TODO - show button "Go to market" 
        if (fm.findFragmentByTag(Df_UpgradeNeeded.DIALOG_TAG) == null) {
            Df_UpgradeNeeded fra = new Df_UpgradeNeeded();
            fra.show(fm, Df_UpgradeNeeded.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showCommProblemDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_CommProblem.DIALOG_TAG) == null) {
            Df_CommProblem fra = new Df_CommProblem();
            fra.show(fm, Df_CommProblem.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showCommWaitDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_CommWait.DIALOG_TAG) == null) {
            Df_CommWait fra = new Df_CommWait();
            fra.show(fm, Df_CommWait.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    /**
     *
     * @param fm FragmentManager instance
     * @return true if dialog is found and dismissed, false otherwise
     */
    public static boolean hideCommWaitDialog(FragmentManager fm) {
        DialogFragment df = (DialogFragment) fm.findFragmentByTag(Df_CommWait.DIALOG_TAG);
        if (df != null) {
            df.dismiss();
            return true;
        } else {
            return false;
        }
    }


    public static void showNeedReloginDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_NeedRelogin.DIALOG_TAG) == null) {
            Df_NeedRelogin fra = new Df_NeedRelogin();
            fra.show(fm, Df_NeedRelogin.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showGenericWaitDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_GenericWait.DIALOG_TAG) == null) {
            Df_GenericWait fra = new Df_GenericWait();
            fra.show(fm, Df_GenericWait.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void hideGenericWaitDialog(FragmentManager fm) {
        DialogFragment df = (DialogFragment) fm.findFragmentByTag(Df_GenericWait.DIALOG_TAG);
        if (df != null) {
            df.dismiss();
        }
    }


    public static void showCannotStartSessionDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_CannotStartSession.DIALOG_TAG) == null) {
            Df_CannotStartSession fra = new Df_CannotStartSession();
            fra.show(fm, Df_CannotStartSession.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void showLoggingInDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_LoggingIn.DIALOG_TAG) == null) {
            Df_LoggingIn fra = new Df_LoggingIn();
            fra.show(fm, Df_LoggingIn.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void hideLoggingInDialog(FragmentManager fm) {
        DialogFragment df = (DialogFragment) fm.findFragmentByTag(Df_LoggingIn.DIALOG_TAG);
        if (df != null) {
            df.dismiss();
        }
    }


    public static void showLoggingOutDialog(FragmentManager fm) {
        if (fm.findFragmentByTag(Df_LoggingOut.DIALOG_TAG) == null) {
            Df_LoggingOut fra = new Df_LoggingOut();
            fra.show(fm, Df_LoggingOut.DIALOG_TAG);
            fm.executePendingTransactions();
        }
    }


    public static void hideLoggingOutDialog(FragmentManager fm) {
        DialogFragment df = (DialogFragment) fm.findFragmentByTag(Df_LoggingOut.DIALOG_TAG);
        if (df != null) {
            df.dismiss();
        }
    }
}
