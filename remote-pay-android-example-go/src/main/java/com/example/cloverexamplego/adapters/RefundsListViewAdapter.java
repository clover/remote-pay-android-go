/*
 * Copyright (C) 2016 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cloverexamplego.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.model.GoNakedRefund;
import com.example.cloverexamplego.utils.CurrencyUtils;

import java.util.List;
import java.util.Locale;

public class RefundsListViewAdapter extends ArrayAdapter<GoNakedRefund> {
    public RefundsListViewAdapter(Context context, int resource) {
        super(context, resource);
    }

    public RefundsListViewAdapter(Context context, int resource, List<GoNakedRefund> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.refunds_row, null);
        }

        GoNakedRefund posRefund = getItem(position);

        if (posRefund != null) {
            TextView amountColumn = v.findViewById(R.id.RefundsRowAmount);
            TextView dateColumn = v.findViewById(R.id.RefundsRowDate);

            amountColumn.setText(CurrencyUtils.format(posRefund.Amount, Locale.getDefault()));
            dateColumn.setText("" + posRefund.date);
        }

        return v;
    }
}
