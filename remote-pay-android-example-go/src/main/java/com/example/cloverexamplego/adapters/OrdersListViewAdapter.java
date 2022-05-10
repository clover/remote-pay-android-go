package com.example.cloverexamplego.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cloverexamplego.R;
import com.example.cloverexamplego.model.GoOrder;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.utils.CurrencyUtils;

import java.util.List;
import java.util.Locale;

public class OrdersListViewAdapter extends ArrayAdapter<GoOrder> {

    public OrdersListViewAdapter(Context context, int resource, List<GoOrder> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_orders, null);
        } else {
            view = convertView;
        }

        TextView idColumn = view.findViewById(R.id.itemOrderId);
        TextView dateColumn = view.findViewById(R.id.itemOrderDate);
        TextView statusColumn = view.findViewById(R.id.itemOrderStatus);
        TextView totalColumn = view.findViewById(R.id.itemOrderAmt);

        GoOrder order = getItem(position);
        idColumn.setText(order.getId());
        dateColumn.setText(order.getDate().toString());
        statusColumn.setText(((GoPayment) order.getPayments().get(0)).getStatus().name());
        long totalAmt = order.getAmount() + (order.getTipAmount() > 0 ? order.getTipAmount() : ((GoPayment) order.getPayments().get(0)).getTipAmount());
        totalColumn.setText(CurrencyUtils.format(totalAmt, Locale.getDefault()));

        return view;
    }
}