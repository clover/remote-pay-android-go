package com.example.cloverexamplego.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.messages.AuthRequest;
import com.clover.remote.client.messages.CapturePreAuthRequest;
import com.clover.remote.client.messages.CloseoutRequest;
import com.clover.remote.client.messages.PreAuthRequest;
import com.clover.remote.client.messages.SaleRequest;
import com.clover.remote.client.messages.TransactionRequest;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.adapters.PreAuthListViewAdapter;
import static com.example.cloverexamplego.fragments.MiscFragment.PREF_SIGN_AMOUNT;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.utils.IdUtils;
import com.example.cloverexamplego.utils.PreferenceUtil;
import java.util.ArrayList;
import java.util.List;

public class RegisterFragment extends BaseFragment {
    public static final String TAG = "RegisterFragment";

    private EditText mAmtEditTxt;
    private EditText mPaymentNoteEditTxt;
    private EditText invoiceNoteEditTxt;

    private ICloverGoConnector mCloverConnector;

    private TextView preAuthTitleTxtVw;
    private ListView preAuthListView;
    private PreAuthListViewAdapter preAuthListAdapter;

    private List<GoPayment> preAuthPayments = new ArrayList<>();
    private long tipAmount = 0;

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
        invoiceNoteEditTxt = view.findViewById(R.id.regInvoiceNote);
        view.findViewById(R.id.regSaleBtn).setOnClickListener(v -> {
            if (isValidAmount()) {
                alertAddTipAmount();
            }
        });
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
            request.setTipMode(SaleRequest.TipMode.TIP_PROVIDED);
            request.setTipAmount(tipAmount);
            updateTransactionRequest(request);
            mCloverConnector.sale(request);
        }
    }

    private void doAuthTransaction() {
        if (isValidAmount()) {
            AuthRequest request = new AuthRequest(Long.valueOf(mAmtEditTxt.getText().toString()), IdUtils.getNextId());
            updateTransactionRequest(request);
            mCloverConnector.auth(request);
        }
    }

    private void doPreAuthTransaction() {
        if (isValidAmount()) {
            PreAuthRequest request = new PreAuthRequest(Long.valueOf(mAmtEditTxt.getText().toString()), IdUtils.getNextId());
            updateTransactionRequest(request);
            mCloverConnector.preAuth(request);
        }
    }

    private void updateTransactionRequest(TransactionRequest request) {
        request.setNote(mPaymentNoteEditTxt.getText().toString());
        request.setInvoiceNum(invoiceNoteEditTxt.getText().toString());
        request.setCardEntryMethods(getPOSActivity().getCloverGoCardEntryMethodState());

        if (!TextUtils.isEmpty(PreferenceUtil.getStringValue(getActivity(), PREF_SIGN_AMOUNT))) {
            request.setSignatureThreshold(Long.valueOf(PreferenceUtil.getStringValue(getActivity(), PREF_SIGN_AMOUNT)));
        }
    }

    private void closeOutPendingTransactions() {
        Toast.makeText(getActivity().getBaseContext(),
                "Attempting to close out pending transactions. This takes time. Please wait.",
                Toast.LENGTH_LONG).show();
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

    private void alertAddTipAmount() {
        EditText input = new EditText(getActivity());
        input.setHint(R.string.tip_adjust_hint);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(getActivity())
                .setTitle("Do you want to add tip amount?")
                .setView(input)
                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                    if (getPOSActivity().isValidAmount(input)) {
                        tipAmount = Long.parseLong(input.getText().toString());
                        doSaleTransaction();
                        dialog1.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog12, which) -> {
                    doSaleTransaction();
                    dialog12.cancel();
                }).show();
    }
}
