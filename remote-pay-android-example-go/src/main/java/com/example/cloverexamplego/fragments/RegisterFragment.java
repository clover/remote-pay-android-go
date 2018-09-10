package com.example.cloverexamplego.fragments;

import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.messages.AuthRequest;
import com.clover.remote.client.messages.CapturePreAuthRequest;
import com.clover.remote.client.messages.CloseoutRequest;
import com.clover.remote.client.messages.PreAuthRequest;
import com.clover.remote.client.messages.SaleRequest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.adapters.PreAuthListViewAdapter;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.utils.IdUtils;

import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends BaseFragment {
    public static final String TAG = "RegisterFragment";

    private EditText mAmtEditTxt;
    private EditText mPaymentNoteEditTxt;

    private ICloverGoConnector mCloverConnector;

    private TextView preAuthTitleTxtVw;
    private ListView preAuthListView;
    private PreAuthListViewAdapter preAuthListAdapter;

    private List<GoPayment> preAuthPayments = new ArrayList<>();

    public static RegisterFragment newInstance(ICloverConnector cloverConnector, List<GoPayment> preAuthPayments) {
        RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.mCloverConnector = (ICloverGoConnector) cloverConnector;
        registerFragment.preAuthPayments = preAuthPayments;
        return registerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mAmtEditTxt = view.findViewById(R.id.regAmtEditTxt);
        mPaymentNoteEditTxt = view.findViewById(R.id.regPaymentNote);
        view.findViewById(R.id.regSaleBtn).setOnClickListener(v -> doSaleTransaction());
        view.findViewById(R.id.regAuthBtn).setOnClickListener(v -> doAuthTransaction());
        view.findViewById(R.id.regCloseoutButton).setOnClickListener(v -> closeOutPendingTransactions());
        view.findViewById(R.id.regPreAuthBtn).setOnClickListener(v -> doPreAuthTransaction());
        preAuthListView = view.findViewById(R.id.preAuthListView);
        preAuthTitleTxtVw = view.findViewById(R.id.preAuthTitleTxtVw);

        preAuthListAdapter = new PreAuthListViewAdapter(getActivity(), R.id.preAuthListView, preAuthPayments);
        preAuthListView.setAdapter(preAuthListAdapter);
        preAuthListView.setOnItemClickListener((parent, v, position, id) -> {
            if (!isValidAmount()) {
                return;
            }
            Long amount = Long.valueOf(mAmtEditTxt.getText().toString());
            GoPayment goPayment = (GoPayment) preAuthListView.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String[] payOptions = new String[]{"Pay for current order"};

            builder.setTitle("Pay With PreAuth").
                    setItems(payOptions, (dialog, index) -> {
                        final ICloverConnector cloverConnector = mCloverConnector;
                        if (cloverConnector != null) {

                            switch (index) {
                                case 0: {
                                    if (goPayment != null) {
                                        CapturePreAuthRequest car = new CapturePreAuthRequest();
                                        car.setPaymentID(goPayment.getPayment().getId());
                                        car.setAmount(amount);
                                        cloverConnector.capturePreAuth(car);
                                        getPOSActivity().showProgressDialog("Processing transaction",
                                                "Please wait, processing order with amount " + amount + " (in cents)",
                                                false);
                                    }
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(getActivity().getBaseContext(), "Clover Connector is null", Toast.LENGTH_LONG).show();
                        }
                    });
            final Dialog dlg = builder.create();
            dlg.show();
        });

        if (preAuthPayments.size() > 0) {
            preAuthTitleTxtVw.setVisibility(View.VISIBLE);
        }

        return view;
    }


    private void doSaleTransaction() {
        if (isValidAmount()) {
            SaleRequest request = new SaleRequest(Long.valueOf(mAmtEditTxt.getText().toString()), IdUtils.getNextId());
            request.setNote(mPaymentNoteEditTxt.getText().toString());
            request.setCardEntryMethods(getPOSActivity().getCloverGoCardEntryMethodState());
            mCloverConnector.sale(request);
        }
    }

    private void doAuthTransaction() {
        if (isValidAmount()) {
            AuthRequest request = new AuthRequest(Long.valueOf(mAmtEditTxt.getText().toString()), IdUtils.getNextId());
            request.setCardEntryMethods(getPOSActivity().getCloverGoCardEntryMethodState());
            mCloverConnector.auth(request);
        }
    }

    private void doPreAuthTransaction() {
        if (isValidAmount()) {
            PreAuthRequest request = new PreAuthRequest(Long.valueOf(mAmtEditTxt.getText().toString()), IdUtils.getNextId());
            request.setCardEntryMethods(getPOSActivity().getCloverGoCardEntryMethodState());
            mCloverConnector.preAuth(request);
        }
    }

    private void closeOutPendingTransactions() {
        CloseoutRequest request = new CloseoutRequest();
        request.setAllowOpenTabs(false);
        request.setBatchId(null);
        mCloverConnector.closeout(request);
    }

    private boolean isValidAmount() {
        return getPOSActivity().isValidAmount(mAmtEditTxt);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

}
