package com.example.cloverexamplego.fragments;

import android.app.Fragment;

import com.example.cloverexamplego.activities.GoPOSActivity;

public abstract class BaseFragment extends Fragment {

    public abstract String getFragmentTag();

    protected GoPOSActivity getPOSActivity() {
        return (GoPOSActivity) getActivity();
    }
}