package com.example.cloverexamplego.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Avdhesh Akhani on 1/12/17.
 */

public class DialogHelper {

    public static Dialog createConfirmDialog(Context context, String title, String message, String positiveBtnName,
                                             String negativeBtnName, DialogInterface.OnClickListener positiveBtnListener,
                                             DialogInterface.OnClickListener negativeBtnListener) {
        return new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(positiveBtnName, positiveBtnListener)
                .setNegativeButton(negativeBtnName, negativeBtnListener)
                .create();
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message, boolean isCancelable,
                                                    String btnName, DialogInterface.OnClickListener listener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        if (isCancelable) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, btnName, listener);
        }

        return dialog;
    }

    public static AlertDialog createAlertDialog(Context context, String title, String message, String btnName,
                                                DialogInterface.OnClickListener listener) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(btnName, listener)
                .create();
    }
}