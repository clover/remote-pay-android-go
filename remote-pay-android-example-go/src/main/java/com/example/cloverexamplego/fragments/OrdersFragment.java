package com.example.cloverexamplego.fragments;

import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.messages.BaseResponse;
import com.clover.remote.client.messages.RefundPaymentRequest;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.RetrievePaymentRequest;
import com.clover.remote.client.messages.TipAdjustAuthRequest;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.VoidPaymentRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.sdk.v3.order.VoidReason;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.adapters.OrdersListViewAdapter;
import com.example.cloverexamplego.adapters.PaymentsListViewAdapter;
import com.example.cloverexamplego.model.GoConnectorListener;
import com.example.cloverexamplego.model.GoExchange;
import com.example.cloverexamplego.model.GoOrder;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.model.GoRefund;

import java.util.ArrayList;
import java.util.HashMap;

public class OrdersFragment extends BaseFragment {
    public static final String TAG = OrdersFragment.class.getSimpleName();

    private ListView mOrdersListView;
    private ListView mPaymentsListView;
    private OrdersListViewAdapter mOrdersListViewAdapter;
    private PaymentsListViewAdapter mPaymentsListViewAdapter;

    private ArrayList<GoOrder> mOrdersList;
    private ArrayList<GoExchange> mPaymentsList;
    private HashMap<String, GoOrder> mGoOrdersMap;
    private ICloverGoConnector mCloverGoConnector;
    private GoOrder mSelectedOrder;

    private static final String VOID_PAYMENT = "Void Payment";
    private static final String FULL_REFUND_PAYMENT = "Full Refund Payment";
    private static final String PARTIAL_REFUND_PAYMENT = "Partial Refund Payment";
    private static final String TIP_ADJUST_PAYMENT = "Tip Adjust Payment";
    private static final String RECEIPT_OPTIONS = "Receipt Options";
    private static final String GET_PAYMENT_DETAILS = "Get Payment Details";

    public static OrdersFragment newInstance(ArrayList<GoOrder> orders, ICloverConnector cloverConnector) {
        OrdersFragment fragment = new OrdersFragment();
        fragment.mCloverGoConnector = (ICloverGoConnector) cloverConnector;
        fragment.mOrdersList = orders;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        mSelectedOrder = mOrdersList.isEmpty() ? null : mOrdersList.get(0);
        mPaymentsList = new ArrayList<>();
        mGoOrdersMap = new HashMap<>();

        for (GoOrder order : mOrdersList) {
            mGoOrdersMap.put(order.getId(), order);
        }

        mOrdersListView = view.findViewById(R.id.ordersListView);
        mOrdersListViewAdapter = new OrdersListViewAdapter(view.getContext(), R.id.ordersListView, mOrdersList);
        mOrdersListView.setAdapter(mOrdersListViewAdapter);

        mOrdersListView.setOnItemClickListener((parent, view1, position, id) -> {
            mSelectedOrder = (GoOrder) mOrdersListView.getItemAtPosition(position);
            mPaymentsList.clear();
            mPaymentsList.addAll(mSelectedOrder.getPayments());
            mPaymentsListViewAdapter.notifyDataSetChanged();
        });

        mPaymentsListView = view.findViewById(R.id.paymentsListView);
        mPaymentsListViewAdapter = new PaymentsListViewAdapter(view.getContext(), R.id.paymentsListView, mPaymentsList);
        mPaymentsListView.setAdapter(mPaymentsListViewAdapter);
        mPaymentsListView.setOnItemClickListener((parent, view12, position, id) -> {
            GoExchange goExchange = (GoExchange) mPaymentsListView.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String[] options;

            if (goExchange instanceof GoPayment) {
                GoPayment goPayment = (GoPayment) goExchange;
                if (goPayment.getStatus() == GoPayment.Status.AUTHORIZED ||
                    ((GoPayment) goExchange).getStatus() == GoPayment.Status.PREAUTHORIZED) {
                    options = new String[]{GET_PAYMENT_DETAILS, VOID_PAYMENT, FULL_REFUND_PAYMENT, PARTIAL_REFUND_PAYMENT, TIP_ADJUST_PAYMENT, RECEIPT_OPTIONS};

                } else if (goPayment.getStatus() == GoPayment.Status.PAID) {
                    options = new String[]{GET_PAYMENT_DETAILS, VOID_PAYMENT, FULL_REFUND_PAYMENT, PARTIAL_REFUND_PAYMENT, RECEIPT_OPTIONS};

                } else if (goPayment.getStatus() == GoPayment.Status.REFUNDED || goPayment.getStatus() == GoPayment.Status.PARTIALLYREFUNDED) {
                    long totalRefund = 0;

                    for (GoExchange exchangeItem : mSelectedOrder.getPayments()) {
                        if (exchangeItem instanceof GoRefund) {
                            totalRefund += ((GoRefund) exchangeItem).getRefund().getAmount();
                        }
                    }

                    if (goPayment.getPayment().getAmount() != totalRefund) {
                        options = new String[]{GET_PAYMENT_DETAILS, VOID_PAYMENT, PARTIAL_REFUND_PAYMENT, RECEIPT_OPTIONS};

                    } else {
                        options = new String[]{GET_PAYMENT_DETAILS, RECEIPT_OPTIONS};

                    }

                } else if (goPayment.getStatus() == GoPayment.Status.VOIDED) {
                    options = new String[]{RECEIPT_OPTIONS};
                } else {
                    return;
                }

                final String[] finalPaymentOptions = options;
                builder.setTitle("Payment Actions")
                        .setItems(finalPaymentOptions, (dialog, index) -> {
                            if (mCloverGoConnector != null) {
                                final String option = finalPaymentOptions[index];
                                switch (option) {
                                    case GET_PAYMENT_DETAILS:
                                        RetrievePaymentRequest retrievePaymentRequest = new RetrievePaymentRequest(((GoPayment) goExchange).getPayment().getExternalPaymentId());
                                        mCloverGoConnector.retrievePayment(retrievePaymentRequest);
                                        showProgressDialog("Retrieve Payment Response",
                                                "Please wait retrieving payment info for external payment id: "
                                                + ((GoPayment) goExchange).getPayment()
                                                        .getExternalPaymentId());
                                        break;
                                    case VOID_PAYMENT:
                                        VoidPaymentRequest vpr = new VoidPaymentRequest();
                                        vpr.setPaymentId(((GoPayment) goExchange).getPayment().getId());
                                        vpr.setOrderId(mSelectedOrder.getId());
                                        vpr.setVoidReason(VoidReason.USER_CANCEL.name());
                                        vpr.setDisablePrinting(false);
                                        vpr.setDisableReceiptSelection(false);
                                        mCloverGoConnector.voidPayment(vpr);
                                        showProgressDialog("Processing Void Payment",
                                                "Please wait voiding payment "
                                                + ((GoPayment) goExchange).getPayment()
                                                        .getOrder().getId());
                                        break;
                                    case FULL_REFUND_PAYMENT:
                                        RefundPaymentRequest rpr = new RefundPaymentRequest();
                                        rpr.setPaymentId(((GoPayment) goExchange).getPayment().getId());
                                        rpr.setOrderId(mSelectedOrder.getId());
                                        rpr.setFullRefund(true);
                                        rpr.setDisablePrinting(false);
                                        rpr.setDisableReceiptSelection(false);
                                        mCloverGoConnector.refundPayment(rpr);
                                        showProgressDialog("Processing Refund",
                                                "Please wait refunding amount "
                                                + ((GoPayment) goExchange)
                                                        .getPayment().getAmount() + " cents");
                                        break;
                                    case PARTIAL_REFUND_PAYMENT:
                                        EditText refund_input = new EditText(getActivity());
                                        refund_input.setInputType(InputType.TYPE_CLASS_NUMBER);

                                        new AlertDialog.Builder(getActivity())
                                                .setView(refund_input)
                                                .setTitle(getString(R.string.partial_refund_title))
                                                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                                                    if (getPOSActivity().isValidAmount(refund_input)) {
                                                        double val = Double.parseDouble(refund_input.getText().toString());
                                                        long value = (long) val;
                                                        if (mSelectedOrder.getAmount() < value) {
                                                            getPOSActivity().showAlertDialog("Partial Refund Error",
                                                                    "Please enter an amount less than "
                                                                    + mSelectedOrder.getAmount()
                                                                    + " cents to process the refund");
                                                            return;
                                                        }

                                                        RefundPaymentRequest refundRequest = new RefundPaymentRequest();
                                                        refundRequest.setPaymentId(((GoPayment) goExchange).getPayment().getId());
                                                        refundRequest.setOrderId(mSelectedOrder.getId());
                                                        refundRequest.setFullRefund(false);
                                                        refundRequest.setAmount(value);
                                                        refundRequest.setDisablePrinting(false);
                                                        refundRequest.setDisableReceiptSelection(false);
                                                        mCloverGoConnector.refundPayment(refundRequest);
                                                        showProgressDialog("Processing Partial Refund",
                                                                "Please wait refunding amount "
                                                                + value + " cents");
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.cancel())
                                                .show();
                                        break;
                                    case TIP_ADJUST_PAYMENT:
                                        EditText input = new EditText(getActivity());
                                        input.setHint(R.string.tip_adjust_hint);
                                        input.setInputType(InputType.TYPE_CLASS_NUMBER);

                                        new AlertDialog.Builder(getActivity())
                                                .setTitle(getString(R.string.tip_adjust))
                                                .setView(input)
                                                .setPositiveButton(android.R.string.ok, (dialog1, which) -> {
                                                    if (getPOSActivity().isValidAmount(input)) {
                                                        double val = Double.parseDouble(input.getText().toString());
                                                        long value = (long) val;

                                                        TipAdjustAuthRequest taar = new TipAdjustAuthRequest();
                                                        taar.setPaymentId(((GoPayment) goExchange).getPayment().getId());
                                                        taar.setOrderId(mSelectedOrder.getId());
                                                        taar.setTipAmount(value);
                                                        mCloverGoConnector.tipAdjustAuth(taar);
                                                        showProgressDialog("Adjusting Tip Amount",
                                                                "Please wait updating tip amount to"
                                                                + value + " cents");
                                                        dialog1.dismiss();
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.cancel())
                                                .show();
                                        break;
                                    case RECEIPT_OPTIONS:
                                        SendReceiptFragment sendReceiptFragment = SendReceiptFragment.newInstance(mSelectedOrder.getId(), mCloverGoConnector);
                                        getFragmentManager().beginTransaction().add(R.id.mainContainer, sendReceiptFragment).commit();
                                        break;
                                }
                            } else {
                                getPOSActivity().showToast("Clover Connector is null");
                            }
                        }).show();
            }
        });

        mCloverGoConnector.addCloverGoConnectorListener(new GoConnectorListener() {
            @Override
            public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
                updateView(response);
            }

            @Override
            public void onRefundPaymentResponse(RefundPaymentResponse response) {
                if (response.isSuccess()) {
                    if (mGoOrdersMap.containsKey(response.getOrderId())) {
                        mPaymentsList.clear();
                        GoOrder goOrder = mGoOrdersMap.get(response.getOrderId());
                        Long totalRefund = 0L;
                        for (GoExchange exchangeItem : goOrder.getPayments()) {
                            if (exchangeItem instanceof GoRefund) {
                                totalRefund += ((GoRefund) exchangeItem).getRefund().getAmount();
                            }
                        }

                        if (goOrder.getAmount() == totalRefund) {
                            ((GoPayment)goOrder.getPayments().get(0)).setStatus(GoPayment.Status.REFUNDED);
                        } else {
                            ((GoPayment)goOrder.getPayments().get(0)).setStatus(GoPayment.Status.PARTIALLYREFUNDED);
                        }

                        mPaymentsList.addAll(mGoOrdersMap.get(response.getOrderId()).getPayments());
                        updateView(response);
                    }
                }
            }

            @Override
            public void onVoidPaymentResponse(VoidPaymentResponse response) {
                updateView(response);
            }
        });

        return view;
    }

    private void showProgressDialog(String title, String message) {
        getPOSActivity().showProgressDialog(title, message, false);
    }

    private void updateView(BaseResponse response) {
        if (response.isSuccess()) {
            mOrdersListViewAdapter.notifyDataSetChanged();
            mPaymentsListViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

}