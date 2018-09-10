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
import com.clover.remote.client.messages.VaultCardResponse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.adapters.CardsListViewAdapter;
import com.example.cloverexamplego.model.GoCard;
import com.example.cloverexamplego.model.GoConnectorListener;

import java.util.ArrayList;
import java.util.List;

public class CardsFragment extends BaseFragment {
    private static final String ARG_STORE = "store";
    public static final String TAG = CardsFragment.class.getSimpleName();

    private List<GoCard> goCards = new ArrayList<>();
    private ICloverGoConnector iCloverGoConnector;
    private ListView cardsListView;
    private CardsListViewAdapter cardsListViewAdapter;

    public static CardsFragment newInstance(List<GoCard> goCards, ICloverConnector cloverConnector) {
        CardsFragment fragment = new CardsFragment();
        fragment.goCards = goCards;
        fragment.iCloverGoConnector = (ICloverGoConnector) cloverConnector;
        return fragment;
    }

    public CardsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_cards, container, false);

        cardsListView = view.findViewById(R.id.CardsListView);
        cardsListViewAdapter = new CardsListViewAdapter(view.getContext(), R.id.CardsListView, goCards);
        cardsListView.setAdapter(cardsListViewAdapter);

        iCloverGoConnector.addCloverGoConnectorListener(new GoConnectorListener(){
            @Override
            public void onVaultCardResponse(VaultCardResponse response) {
                if (response.isSuccess()) {
                    cardsListViewAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}
