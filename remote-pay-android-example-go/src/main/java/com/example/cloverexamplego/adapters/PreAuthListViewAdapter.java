package com.example.cloverexamplego.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cloverexamplego.R;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.utils.CurrencyUtils;

import java.util.List;
import java.util.Locale;

public class PreAuthListViewAdapter extends ArrayAdapter<GoPayment> {

    public PreAuthListViewAdapter(@NonNull Context context, int resource, List<GoPayment> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.preauth_row, null);
        }

        GoPayment goPayment = getItem(position);

        if (goPayment != null) {
            TextView nameColumn = view.findViewById(R.id.PreAuthNameColumn);
            TextView amountColumn = view.findViewById(R.id.PreAuthAmountColumn);
            TextView orderTxtVw = view.findViewById(R.id.preAuthOrderIdTxtVw);
            TextView paymentTxtVw = view.findViewById(R.id.preAuthPayIdTxtVw);
            TextView externalPayTxtVw = view.findViewById(R.id.preAuthExternalPayIdTxtVw);

            nameColumn.setText(R.string.pre_authorized);
            amountColumn.setText(CurrencyUtils.format(goPayment.getPayment().getAmount(), Locale.getDefault()));
            orderTxtVw.setText(view.getContext().getString(R.string.order_id_with_amount,
                    goPayment.getPayment().getOrder().getId()));
            paymentTxtVw.setText(view.getContext().getString(R.string.payment_id_with_amount,
                    goPayment.getPayment().getId()));
            externalPayTxtVw.setText(view.getContext().getString(R.string.external_payment_id_with_amount,
                    goPayment.getPayment().getExternalPaymentId()));
        }

        return view;
    }

}
