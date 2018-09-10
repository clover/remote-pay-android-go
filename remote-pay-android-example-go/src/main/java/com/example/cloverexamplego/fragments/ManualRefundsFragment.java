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

package com.example.cloverexamplego.fragments;

import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.clover.remote.client.messages.ManualRefundRequest;
import com.clover.remote.client.messages.ManualRefundResponse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.adapters.RefundsListViewAdapter;
import com.example.cloverexamplego.model.GoConnectorListener;
import com.example.cloverexamplego.model.GoNakedRefund;
import com.example.cloverexamplego.utils.IdUtils;

import java.util.ArrayList;

public class ManualRefundsFragment extends BaseFragment {
    public static final String TAG = ManualRefundsFragment.class.getSimpleName();

    private ArrayList<GoNakedRefund> goNakedRefunds = new ArrayList<>();
    private ICloverGoConnector iCloverGoConnector;
    private RefundsListViewAdapter itemsListViewAdapter;

    public static ManualRefundsFragment newInstance(ArrayList<GoNakedRefund> goNakedRefunds, ICloverConnector cloverConnector) {
        ManualRefundsFragment fragment = new ManualRefundsFragment();
        fragment.goNakedRefunds = goNakedRefunds;
        fragment.iCloverGoConnector = (ICloverGoConnector) cloverConnector;
        return fragment;
    }

    public ManualRefundsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refunds, container, false);

        ListView refundsListView = view.findViewById(R.id.RefundsListView);

        view.findViewById(R.id.RefundButton).setOnClickListener(v -> onManualRefundClick(((TextView) view.findViewById(R.id.ManualRefundTextView)).getText().toString()));

        itemsListViewAdapter = new RefundsListViewAdapter(view.getContext(), R.id.RefundsListView, goNakedRefunds);
        refundsListView.setAdapter(itemsListViewAdapter);
        itemsListViewAdapter.notifyDataSetChanged();

        iCloverGoConnector.addCloverGoConnectorListener(new GoConnectorListener(){
            @Override
            public void onManualRefundResponse(ManualRefundResponse response) {
                itemsListViewAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void onManualRefundClick(String amount) {
        try {
            long refundAmount = Long.parseLong(amount);
            ManualRefundRequest request = new ManualRefundRequest(refundAmount, IdUtils.getNextId());
            request.setAmount(refundAmount);
            request.setCardEntryMethods(getPOSActivity().getCloverGoCardEntryMethodState());
            iCloverGoConnector.manualRefund(request);
        } catch (NumberFormatException nfe) {
            getPOSActivity().showToast("Invalid value. Must be an integer.");
        } catch (UnsupportedOperationException e) {
            getPOSActivity().showToast(e.getMessage());
        }
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
