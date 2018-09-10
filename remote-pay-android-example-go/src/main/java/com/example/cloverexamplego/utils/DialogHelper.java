package com.example.cloverexamplego.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

/**
 * Created by Avdhesh Akhani on 1/12/17.
 */

public class DialogHelper {

    public static Dialog createMessageDialog(Context context, String title, String message, String btnName, DialogInterface.OnClickListener listener) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(btnName, listener);
        dialog = builder.create();

        return dialog;
    }

    public static Dialog createConfirmDialog(Context context, String title, String message, String positiveBtnName,
                                             String negativeBtnName, DialogInterface.OnClickListener positiveBtnListener,
                                             DialogInterface.OnClickListener negativeBtnListener) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(positiveBtnName, positiveBtnListener);
        builder.setNegativeButton(negativeBtnName, negativeBtnListener);
        dialog = builder.create();

        return dialog;
    }

    public static ProgressDialog showProgressDialog(Context context, String title, String message, boolean isCancelable, String btnName, DialogInterface.OnClickListener listener) {
        ProgressDialog m_Dialog = new ProgressDialog(context);
        m_Dialog.setMessage(message);
        m_Dialog.setTitle(title);
        m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_Dialog.setIndeterminate(true);
        m_Dialog.setCancelable(false);
        if (isCancelable) {
            m_Dialog.setButton(DialogInterface.BUTTON_NEGATIVE, btnName, listener);
        }

        return m_Dialog;
    }

    public static AlertDialog createAlertDialog(Context context, String title, String message, String btnName, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(btnName, listener);

        return builder.create();
    }
}
