package com.example.cloverexamplego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cloverexamplego.R;
import com.example.cloverexamplego.model.GoExchange;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.model.GoRefund;
import com.example.cloverexamplego.utils.CurrencyUtils;

import java.util.List;
import java.util.Locale;

public class PaymentsListViewAdapter extends ArrayAdapter<GoExchange> {

    public PaymentsListViewAdapter(Context context, int resource, List<GoExchange> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_payments, null);
        } else {
            view = convertView;
        }

        TextView paymentStatus = view.findViewById(R.id.itemPaymentStatus);
        TextView paymentAmount = view.findViewById(R.id.itemPaymentAmount);
        TextView paymentTip = view.findViewById(R.id.itemPaymentTip);
        TextView paymentTipLabel = view.findViewById(R.id.itemPaymentTipLabel);
        TextView paymentExternalId = view.findViewById(R.id.itemPaymentExternalId);

        if (getItem(position) instanceof GoPayment) {
            GoPayment payment = (GoPayment) getItem(position);
            String externalPaymentId = payment.getPayment().getExternalPaymentId();

            paymentStatus.setText(payment.getStatus().name());
            paymentTip.setText(payment.getTipAmount() > 0 ? CurrencyUtils.format(payment.getTipAmount(), Locale.getDefault()) : CurrencyUtils.format(payment.getPayment().getTipAmount(), Locale.getDefault()));
            paymentAmount.setText(CurrencyUtils.format(payment.getPayment().getAmount(), Locale.getDefault()));
            paymentExternalId.setText(externalPaymentId != null ? externalPaymentId : "<unset ext id>");

        } else if (getItem(position) instanceof GoRefund) {
            GoRefund refund = (GoRefund) getItem(position);

            paymentStatus.setText("REFUND");
            paymentAmount.setText(CurrencyUtils.format(refund.getRefund().getAmount(), Locale.getDefault()));
            paymentTip.setVisibility(View.GONE);
            paymentTipLabel.setVisibility(View.GONE);
            paymentExternalId.setVisibility(View.GONE);
        }

        return view;
    }
}