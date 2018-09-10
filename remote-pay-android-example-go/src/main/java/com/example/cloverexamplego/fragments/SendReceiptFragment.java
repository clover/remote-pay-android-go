package com.example.cloverexamplego.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.utils.Validator;

public class SendReceiptFragment extends BaseFragment {
    public static final String TAG = "SendReceiptFragment";

    private ICloverGoConnector mCloverConnector;
    private String mOrderID;

    private ICloverGoConnectorListener.SendReceipt mSendReceiptListnr;

    public static SendReceiptFragment newInstance(String orderID, ICloverConnector cloverConnector) {
        SendReceiptFragment sendReceiptFragment = new SendReceiptFragment();
        sendReceiptFragment.mCloverConnector = (ICloverGoConnector) cloverConnector;
        sendReceiptFragment.mOrderID = orderID;
        return sendReceiptFragment;
    }

    public static SendReceiptFragment newInstance(String orderID, ICloverGoConnectorListener.SendReceipt sendReceipt) {
        SendReceiptFragment sendReceiptFragment = new SendReceiptFragment();
        sendReceiptFragment.mSendReceiptListnr = sendReceipt;
        sendReceiptFragment.mOrderID = orderID;
        return sendReceiptFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_send_receipt, container, false);

        EditText emailEditText = view.findViewById(R.id.sendReceiptEmailEdit);
        EditText phoneEditText = view.findViewById(R.id.sendReceiptPhoneEdit);

        view.findViewById(R.id.sendReceiptBtn).setOnClickListener(v -> {
            String phoneNumber = phoneEditText.getText().toString().replaceAll("\\D", "");
            String email = emailEditText.getText().toString();

            if (Validator.validateEmailInput(email) || Validator.validatePhoneNumberInput(phoneNumber)) {
                if (mSendReceiptListnr != null) {
                    mSendReceiptListnr.sendRequestedReceipt(email, phoneNumber, mOrderID);
                } else {
                    mCloverConnector.sendReceipt(email, phoneNumber, mOrderID);
                }

                getPOSActivity().hideKeyboard();
                getActivity().getFragmentManager().beginTransaction().hide(SendReceiptFragment.this).commit();

            } else {
                getPOSActivity().showToast("Please enter a valid phone number or email");

            }

        });

        view.findViewById(R.id.noReceiptBtn).setOnClickListener(v -> {
            if (mSendReceiptListnr != null) {
                mSendReceiptListnr.noReceipt();
            }

            getPOSActivity().hideKeyboard();
            getActivity().getFragmentManager().beginTransaction().remove(SendReceiptFragment.this).commit();
        });

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}