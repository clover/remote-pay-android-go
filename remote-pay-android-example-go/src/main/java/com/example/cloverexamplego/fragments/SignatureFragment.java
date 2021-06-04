package com.example.cloverexamplego.fragments;

import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.example.cloverexamplego.R;

import java.util.ArrayList;

public class SignatureFragment extends BaseFragment {
    public static final String TAG = "SignatureFragment";

    private ICloverGoConnectorListener.SignatureCapture mSignatureCaptureListnr;
    private String mPaymentID;

    public static SignatureFragment newInstance(String paymentID, ICloverGoConnectorListener.SignatureCapture signatureCapture) {
        SignatureFragment fragment = new SignatureFragment();
        fragment.mSignatureCaptureListnr = signatureCapture;
        fragment.mPaymentID = paymentID;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_signature, container, false);

        GestureOverlayView signatureView = view.findViewById(R.id.SignatureView);
        signatureView.setKeepScreenOn(true);

        view.findViewById(R.id.AcceptButton).setOnClickListener(v -> {
            if (signatureView.getGesture() != null) {
                ArrayList<int[][]> signatureXY = new ArrayList<>();
                float[] points;
                int[][] xy;
                int count;

                for (int i = 0; i < signatureView.getGesture().getStrokesCount(); i++) {
                    points = signatureView.getGesture().getStrokes().get(i).points;
                    xy = new int[points.length / 2][2];
                    count = 0;

                    for (int j = 0; j < points.length; j += 2) {
                        xy[count][0] = (int) points[j];
                        xy[count][1] = (int) points[j + 1];
                        count++;
                    }

                    signatureXY.add(xy);
                }
                mSignatureCaptureListnr.captureSignature(mPaymentID, signatureXY);
            } else {
                getPOSActivity().showToast("Please Sign...");
            }
        });

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}