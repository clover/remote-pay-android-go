package com.example.cloverexamplego.dialogs;

import android.app.DialogFragment;

import com.example.cloverexamplego.activities.GoPOSActivity;

public abstract class BaseDialog extends DialogFragment {

    public abstract String getFragmentTag();

    protected GoPOSActivity getPOSActivity() {
        return (GoPOSActivity) getActivity();
    }
}