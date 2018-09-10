package com.example.cloverexamplego.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.example.cloverexamplego.R;

public class PaymentTypeDialog extends BaseDialog {
    public static final String TAG = "PaymentTypeDialog";

    private boolean show350, show450, showKeyed;

    public static PaymentTypeDialog newInstance(boolean show350, boolean show450, boolean showKeyed) {
        PaymentTypeDialog fragment = new PaymentTypeDialog();
        fragment.show350 = show350;
        fragment.show450 = show450;
        fragment.showKeyed = showKeyed;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_payment_type, container, false);

        Button rp450Button = (Button) view.findViewById(R.id.rp450_button);
        rp450Button.setOnClickListener(v -> continueTransaction(ICloverGoConnector.GoPaymentType.RP450));
        rp450Button.setVisibility(show450 ? View.VISIBLE : View.GONE);

        Button rp350Button = (Button) view.findViewById(R.id.rp350_button);
        rp350Button.setOnClickListener(v -> continueTransaction(ICloverGoConnector.GoPaymentType.RP350));
        rp350Button.setVisibility(show350 ? View.VISIBLE : View.GONE);

        Button keyEnterButton = (Button) view.findViewById(R.id.key_enter_button);
        keyEnterButton.setOnClickListener(v -> continueTransaction(ICloverGoConnector.GoPaymentType.KEYED));
        keyEnterButton.setVisibility(showKeyed ? View.VISIBLE : View.GONE);

        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
//                getPOSActivity().goPaymentTypeCanceled();
            dismiss();
        });

        return view;
    }

    private void continueTransaction(ICloverGoConnector.GoPaymentType paymentType) {
        getPOSActivity().goPaymentTypeSelected(paymentType);
        dismiss();
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}