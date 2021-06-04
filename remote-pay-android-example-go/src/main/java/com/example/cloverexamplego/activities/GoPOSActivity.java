package com.example.cloverexamplego.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clover.remote.CardData;
import com.clover.remote.Challenge;
import com.clover.remote.client.ConnectorFactory;
import com.clover.remote.client.Constants;
import com.clover.remote.client.ICloverConnector;
import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.clovergo.CloverGoConnector;
import com.clover.remote.client.clovergo.CloverGoConstants.TransactionType;
import com.clover.remote.client.clovergo.CloverGoDeviceConfiguration;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.clover.remote.client.clovergo.util.DeviceUtil;
import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.BaseRequest;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CardApplicationIdentifier;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.GoCardData;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.PaymentResponse;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.ReadCardDataRequest;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResultCode;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VoidPaymentRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.dialogs.KeyedTransactionDialog;
import com.example.cloverexamplego.dialogs.PaymentTypeDialog;
import com.example.cloverexamplego.fragments.BaseFragment;
import com.example.cloverexamplego.fragments.CardsFragment;
import com.example.cloverexamplego.fragments.ManualRefundsFragment;
import com.example.cloverexamplego.fragments.MiscFragment;
import com.example.cloverexamplego.fragments.OrdersFragment;
import com.example.cloverexamplego.fragments.RegisterFragment;
import com.example.cloverexamplego.fragments.SendReceiptFragment;
import com.example.cloverexamplego.fragments.SignatureFragment;
import com.example.cloverexamplego.interfaces.PaymentConfirmationListener;
import com.example.cloverexamplego.model.GoCard;
import com.example.cloverexamplego.model.GoConnectorListener;
import com.example.cloverexamplego.model.GoNakedRefund;
import com.example.cloverexamplego.model.GoOrder;
import com.example.cloverexamplego.model.GoPayment;
import com.example.cloverexamplego.model.GoRefund;
import com.example.cloverexamplego.model.GoStartupParams;
import com.example.cloverexamplego.utils.CurrencyUtils;
import com.example.cloverexamplego.utils.DialogHelper;
import com.example.cloverexamplego.utils.PreferenceUtil;
import com.firstdata.clovergo.domain.model.Order;
import com.firstdata.clovergo.domain.model.ReaderInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.clover.remote.client.Constants.CARD_TRANSACTION_EXTRA_APPLICATION_IDENTIFIER;
import static com.firstdata.clovergo.domain.model.ReaderInfo.ReaderType.RP350;
import static com.firstdata.clovergo.domain.model.ReaderInfo.ReaderType.RP450;

public class GoPOSActivity extends Activity {

    public static final String EXTRA_GO_PARAMS = "EXTRA_GO_PARAMS";
    private static final String TAG = GoPOSActivity.class.getSimpleName();

    private HashMap<ReaderInfo.ReaderType, CloverGoConnector> cGoConnectorMap;
    private HashMap<ReaderInfo.ReaderType, MerchantInfo> merchantInfoMap;
    private HashMap<String, GoOrder> processedOrdersMap;
    private HashMap<String, GoPayment> processedPaymentsMap;
    private ArrayList<ReaderInfo> arrayListReadersList;
    private ArrayList<String> arrayListReaderString;
    private ArrayList<GoOrder> processedOrders;
    private ArrayList<GoPayment> preAuthPayments;
    private ArrayList<GoNakedRefund> goNakedRefunds;
    private ArrayList<GoCard> goCards;
    private ArrayAdapter<String> readerArrayAdapter;

    private ProgressDialog progressDialog;
    private Dialog alertDialog;
    private Dialog confirmDialog;
    private Dialog readerListDialog;
    private BottomNavigationView navigationView;
    private Toast toast;
    private TextView merchantInfoTxtVw;

    private Payment currentPayment = null;
    private Challenge[] currentChallenges = null;
    private ReaderInfo.ReaderType goReaderType = RP450;
    private GoStartupParams startupParams;
    private MerchantInfo merchantInfo;

    private ICloverConnector cloverConnector;
    private ICloverGoConnectorListener ccGoListener;
    private ICloverGoConnectorListener.PaymentTypeSelection paymentTypeSelection;
    private PaymentConfirmationListener payConfListener;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener;

    private String preferred450Reader;
    private int cardEntryMethodState;
    private boolean isReadCardDemo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_pos);

        PreferenceUtil.saveString(this, "Device_ID", "");

        processedOrdersMap = new HashMap<>();
        processedPaymentsMap = new HashMap<>();
        cGoConnectorMap = new HashMap<>();
        merchantInfoMap = new HashMap<>();

        preAuthPayments = new ArrayList<>();
        processedOrders = new ArrayList<>();
        preAuthPayments = new ArrayList<>();
        goNakedRefunds = new ArrayList<>();
        goCards = new ArrayList<>();
        startupParams = (GoStartupParams) getIntent().getSerializableExtra(EXTRA_GO_PARAMS);

        isReadCardDemo = startupParams.getAccessToken().equals("readCard");

        cardEntryMethodState = getDefaultCloverGoCardEntryMethods();

        merchantInfoTxtVw = findViewById(R.id.merchantInfoTxtVw);

        navigationView = findViewById(R.id.bottom_navigation);
        payConfListener = new PaymentConfirmationListener() {
            @Override
            public void onRejectClicked(Challenge challenge) {
                // Reject payment and send the challenge along for logging/reason
                getCloverConnector().rejectPayment(currentPayment, challenge);
                currentChallenges = null;
                currentPayment = null;
            }

            @Override
            public void onAcceptClicked(final int challengeIndex) {
                if (challengeIndex == currentChallenges.length - 1) {
                    // no more challenges, so accept the payment
                    getCloverConnector().acceptPayment(currentPayment);
                    currentChallenges = null;
                    currentPayment = null;

                } else {
                    Challenge theChallenge = currentChallenges[challengeIndex + 1];

                    switch (theChallenge.type) {
                        case DUPLICATE_CHALLENGE:
                            showPaymentConfirmation(payConfListener, theChallenge, challengeIndex + 1);
                            break;

                        case PARTIAL_AUTH_CHALLENGE:
                            showPartialAuthChallenge(payConfListener, theChallenge, challengeIndex + 1);
                            break;

                        case OFFLINE_CHALLENGE:
                            showOfflineChallenge(payConfListener, theChallenge, challengeIndex + 1);
                            break;
                    }
                }
            }
        };

        initCGoConnectorListener();
        initializeReader(RP450);
        cloverConnector = cGoConnectorMap.get(RP450);

        if (isReadCardDemo) {
            navigationView.setOnNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.action_misc) {
                    showMiscReadCard(true);
                }
                return true;
            });
            navigationView.setSelectedItemId(R.id.action_misc);
        } else {
            navigationView.setOnNavigationItemSelectedListener(getNavigationListener());
            navigationView.setSelectedItemId(R.id.action_register);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectorFactory.disposeConnectors();
    }

    @Override
    public void onBackPressed() {
        showConfirmDialog("Exit", "Are you sure you want to close the app?", "Yes", "No",
                (dialog, which) -> {
                    dialog.dismiss();
                    GoPOSActivity.this.finish();
                },
                (dialog, which) -> dialog.dismiss()
        );
    }

    private void initializeReader(ReaderInfo.ReaderType readerType) {
        if (readerType == RP350 || readerType == RP450) {
            MerchantInfo merchantInfo = merchantInfoMap.get(readerType);

            // Need paymentType in addition to goReaderType in case key entered is selected.
            createCloverGoConnector(readerType);

            if (merchantInfo == null) {
                setDisconnectedStatus();
            } else {
                setConnectedStatus(merchantInfo);
            }
        }

        cGoConnectorMap = ConnectorFactory.getCloverGoConnectorMap();
    }

    public void initCGoConnectorListener() {
        if (getCloverConnector() != null) {
            getCloverConnector().dispose();
        }

        ccGoListener = new GoConnectorListener() {
            @Override
            public void onDisplayMessage(String message) {
                showToast(message);
            }

            @Override
            public void onSendReceipt(Order order, SendReceipt sendReceipt) {
                dismissDialogs();
                showToast("Transaction Complete");

                SendReceiptFragment sendReceiptFragment = SendReceiptFragment.newInstance(order.getId(), sendReceipt);
                showFragment(sendReceiptFragment);
            }

            @Override
            public void onVoidPayment(com.firstdata.clovergo.domain.model.Payment payment, String reason) {
                voidGoPayment(payment, reason);
            }

            @Override
            public void onSignatureRequired(com.firstdata.clovergo.domain.model.Payment payment, SignatureCapture signatureCapture) {
                dismissDialogs();

                SignatureFragment signatureFragment = SignatureFragment.newInstance(payment.getPaymentId(), signatureCapture);
                showFragment(signatureFragment);
            }

            @Override
            public void onDeviceDisconnected(ReaderInfo readerInfo) {
                merchantInfoMap.put(readerInfo.getReaderType(), null);
                showToast("Disconnected");
                Log.d(TAG, "disconnected");

                if (goReaderType == readerInfo.getReaderType()) {
                    setDisconnectedStatus();
                }
            }

            public void onDeviceConnected() {
                showToast("Connecting...");
            }

            @Override
            public void onCloverGoDeviceActivity(final CloverDeviceEvent deviceEvent) {
                switch (deviceEvent.getEventState()) {
                    case CARD_SWIPED:
                        showProgressDialog("Card Swiped", deviceEvent.getMessage(), false);
                        break;
                    case CARD_TAPPED:
                        showProgressDialog("Contactless Payment Started", deviceEvent.getMessage(), false);
                        break;
                    case EMV_COMPLETE:
                        showProgressDialog("EMV Transaction Completed", deviceEvent.getMessage(), false);
                        break;
                    case CARD_INSERTED:
                        showProgressDialog("Card Inserted", deviceEvent.getMessage(), false);
                        break;
                    case UPDATE_STARTED:
                        showProgressDialog("Reader Update", deviceEvent.getMessage(), false);
                        break;
                    case UPDATE_COMPLETED:
                        showAlertDialog("Reader Update", "Please disconnect and reconnect your reader.");
                        break;
                    case CANCEL_CARD_READ:
                        showToast(deviceEvent.getMessage());
                        break;
                    case CARD_REMOVED:
                        showToast(deviceEvent.getMessage());
                        break;
                    case PLEASE_SEE_PHONE_MSG:
                        showToast(deviceEvent.getMessage());
                        break;
                    case READER_READY:
                        showToast(deviceEvent.getMessage());
                        break;
                    case READ_CARD_DATA_COMPLETED:
                        showAlertDialog("CARD DATA ", deviceEvent.getMessage());
                        break;
                }
            }

            @Override
            public void onGetMerchantInfo() {
                showProgressDialog("Getting Merchant Info", "Please wait", false);
            }

            @Override
            public void onGetMerchantInfoResponse(MerchantInfo merchant) {
                if (merchant != null) {
                    dismissDialogs();
                    merchantInfo = merchant;
                    merchantInfoTxtVw.setText("Merchant: " + merchantInfo.getMerchantName());

                } else {
                    showAlertDialog("Initialization error", "Could not initialize the SDK. Please try again later.",
                            (dialog, which) -> {
                                GoPOSActivity.this.finish();
                                startActivity(new Intent(GoPOSActivity.this, GoStartupActivity.class));
                            });
                }
            }

            @Override
            public void onDeviceDiscovered(ReaderInfo readerInfo) {
                boolean isSelected = false;
                for (ReaderInfo readerInfoItem : arrayListReadersList) {
                    if (readerInfoItem.getBluetoothIdentifier().contentEquals(readerInfo.getBluetoothIdentifier())) {
                        isSelected = true;
                        break;
                    }
                }

                if (!isSelected) {
                    if (!TextUtils.isEmpty(preferred450Reader) && readerInfo.getBluetoothName().contains(preferred450Reader)) {
                        ((ICloverGoConnector) cloverConnector).connectToBluetoothDevice(readerInfo);
                        showToast("Auto-connecting to preferred reader");
                        readerListDialog.dismiss();
                        return;
                    }

                    arrayListReadersList.add(readerInfo);
                    arrayListReaderString.add(readerInfo.getBluetoothName());

                    if (readerArrayAdapter != null) {
                        readerArrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onAidMatch(final List<CardApplicationIdentifier> applicationIdentifiers, final AidSelection aidSelection) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(GoPOSActivity.this, android.R.layout.simple_list_item_1);
                for (CardApplicationIdentifier applicationIdentifier : applicationIdentifiers) {
                    arrayAdapter.add(applicationIdentifier.getApplicationLabel());
                }

                new AlertDialog.Builder(GoPOSActivity.this)
                        .setTitle("Please choose card")
                        .setCancelable(false)
                        .setSingleChoiceItems(arrayAdapter, 0, (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            aidSelection.selectApplicationIdentifier(applicationIdentifiers.get(i));
                        })
                        .setNegativeButton("Cancel", (dialogInterface, i) -> {
                            dismissDialogs();
                            dialogInterface.cancel();
                            showToast("Transaction cancelled Card is not charged");
                        })
                        .show();
            }

            @Override
            public void onPaymentTypeRequired(final int cardEntryMethods, List<ReaderInfo> connectedReaders, final PaymentTypeSelection paymentTypeSelection) {
                GoPOSActivity.this.paymentTypeSelection = paymentTypeSelection;
                showGoPaymentTypes(connectedReaders, cardEntryMethods);
            }

            @Override
            public void onManualCardEntryRequired(TransactionType transactionType, BaseRequest baseRequest,
                                                  ICloverGoConnector.GoPaymentType goPaymentType, ReaderInfo.ReaderType readerType,
                                                  boolean allowDuplicate, ManualCardEntry manualCardEntry) {
                KeyedTransactionDialog keyedTransactionDialog = KeyedTransactionDialog.newInstance(transactionType, baseRequest, manualCardEntry);
                keyedTransactionDialog.show(getFragmentManager(), keyedTransactionDialog.getTag());
            }

            void showGoPaymentTypes(List<ReaderInfo> connectedReaders, int cardEntryMethods) {
                boolean show350 = false, show450 = false, showKeyed = false;

                if ((cardEntryMethods & Constants.CARD_ENTRY_METHOD_MANUAL) == Constants.CARD_ENTRY_METHOD_MANUAL) {
                    showKeyed = true;
                }

                for (ReaderInfo connectedReader : connectedReaders) {
                    // Checking if connected again here in case it disconnected on the way to this logic
                    if (connectedReader.isConnected()) {
                        if (connectedReader.getReaderType() == RP350 &&
                                ((cardEntryMethods & Constants.CARD_ENTRY_METHOD_ICC_CONTACT) == Constants.CARD_ENTRY_METHOD_ICC_CONTACT)) {
                            show350 = true;

                        } else if (connectedReader.getReaderType() == RP450 &&
                                ((cardEntryMethods & Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS) == Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS)) {
                            show450 = true;

                        }
                    }
                }

                PaymentTypeDialog paymentTypeDialog = PaymentTypeDialog.newInstance(show350, show450, showKeyed);
                paymentTypeDialog.show(getFragmentManager(), paymentTypeDialog.getTag());
            }

            public void onDeviceReady(final MerchantInfo merchantInfo) {
                dismissDialogs();

                if (merchantInfo != null) {
                    if (goReaderType.name().equalsIgnoreCase(merchantInfo.getDeviceInfo().getModel())) {
                        setConnectedStatus(merchantInfo);
                    }

                    if (RP450.name().equalsIgnoreCase(merchantInfo.getDeviceInfo().getModel())) {
                        merchantInfoMap.put(RP450, merchantInfo);
                    } else if (RP350.name().equalsIgnoreCase(merchantInfo.getDeviceInfo().getModel())) {
                        merchantInfoMap.put(RP350, merchantInfo);
                    }
                }

                showToast("Device Ready!");
            }

            @Override
            public void notifyOnProgressDialog(String title, String message, boolean isCancelable) {
                showProgressDialog(title, message, isCancelable);
            }

            @Override
            public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {
                switch (deviceErrorEvent.getErrorType()) {
                    case READER_ERROR:
                    case CARD_ERROR:
                    case READER_TIMEOUT:
                    case COMMUNICATION_ERROR:
                    case LOW_BATTERY:
                    case PARTIAL_AUTH_REJECTED:
                    case DUPLICATE_TRANSACTION_REJECTED:
                        showAlertDialog(deviceErrorEvent.getErrorType().name().replace('_', ' '), deviceErrorEvent.getMessage());
                        break;
                    case MULTIPLE_CONTACT_LESS_CARD_DETECTED_ERROR:
                    case CONTACT_LESS_FAILED_TRY_CONTACT_ERROR:
                    case EMV_CARD_SWIPED_ERROR:
                    case DIP_FAILED_ALL_ATTEMPTS_ERROR:
                    case DIP_FAILED_ERROR:
                    case SWIPE_FAILED_ERROR:
                        showProgressDialog(deviceErrorEvent.getErrorType().name().replace('_', ' '), deviceErrorEvent.getMessage(), true);
                        break;
                    default:
                        showAlertDialog(deviceErrorEvent.getErrorType().name(), deviceErrorEvent.getMessage());
                        break;
                }
            }

            void voidGoPayment(com.firstdata.clovergo.domain.model.Payment payment, String reason) {
                VoidPaymentRequest voidPaymentRequest = new VoidPaymentRequest();
                voidPaymentRequest.setPaymentId(payment.getPaymentId());
                voidPaymentRequest.setOrderId(payment.getOrderId());
                voidPaymentRequest.setVoidReason(reason);
                getCloverConnector().voidPayment(voidPaymentRequest);
            }

            @Override
            public void onSaleResponse(final SaleResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    addNewProcessedOrder(new GoOrder(new GoPayment(response)));

                    showRegister();
                    showPaymentInfo(response);
                    showToast("Sale successfully processed");
                } else {
                    showAlertDialog(response.getReason(), response.getMessage());
                }
            }

            @Override
            public void onAuthResponse(final AuthResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    addNewProcessedOrder(new GoOrder(new GoPayment(response)));

                    showRegister();
                    showPaymentInfo(response);
                    showToast("Auth successfully processed.");

                } else {
                    showAlertDialog(response.getReason(), response.getMessage());

                }
            }

            @Override
            public void onPreAuthResponse(final PreAuthResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    preAuthPayments.add(new GoPayment(response));

                    showRegister(preAuthPayments);
                    showPaymentInfo(response);
                    showToast("PreAuth successfully processed.");

                } else {
                    showAlertDialog(response.getReason(), response.getMessage());

                }
            }

            @Override
            public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    GoOrder goOrder = processedOrdersMap.get(response.getPaymentId());

                    if (goOrder != null) {
                        processedPaymentsMap.get(response.getPaymentId())
                                .setTipAmount(response.getTipAmount());
                        showToast("Tip successfully adjusted");
                    }
                } else {
                    showToast("Tip adjust failed");
                }
            }

            @Override
            public void onCapturePreAuthResponse(CapturePreAuthResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    for (GoPayment goPayment : preAuthPayments) {
                        if (goPayment.getPayment().getId().equals(response.getPaymentID())) {
                            preAuthPayments.remove(goPayment);
                            goPayment.getPayment().setAmount(response.getAmount());
                            goPayment.setStatus(GoPayment.Status.AUTHORIZED);
                            addNewProcessedOrder(new GoOrder(goPayment));

                            showToast("PreAuth successfully processed.");
                            showFragment(RegisterFragment.newInstance(cloverConnector, preAuthPayments));

                            PaymentResponse paymentResponse = new PaymentResponse(true, ResultCode.SUCCESS);
                            paymentResponse.setPayment(goPayment.getPayment());
                            showPaymentInfo(paymentResponse);
                            break;
                        }
                    }
                } else {
                    showToast(response.getMessage());
                }
            }

            @Override
            public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
                if (request.getChallenges() == null) {
                    showToast("Error: The ConfirmPaymentRequest was missing the payment and/or challenges.");
                } else {
                    currentPayment = request.getPayment();
                    currentChallenges = request.getChallenges();
                    Challenge theChallenge = currentChallenges[0];

                    switch (theChallenge.type) {
                        case DUPLICATE_CHALLENGE:
                            showPaymentConfirmation(payConfListener, theChallenge, 0);
                            break;
                        case PARTIAL_AUTH_CHALLENGE:
                            showPartialAuthChallenge(payConfListener, theChallenge, 0);
                            break;
                        case OFFLINE_CHALLENGE:
                            showOfflineChallenge(payConfListener, theChallenge, 0);
                            break;
                    }
                }
            }

            @Override
            public void onCloseoutResponse(CloseoutResponse response) {
                if (response.isSuccess()) {
                    showToast("Closeout is scheduled.");
                } else {
                    showToast("Error getting current batch's transactions");
                }
            }

            @Override
            public void onRefundPaymentResponse(final RefundPaymentResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    GoRefund refund = new GoRefund(response.getRefund());
                    processedPaymentsMap.get(response.getPaymentId()).setStatus(GoPayment.Status.REFUNDED);
                    processedOrdersMap.get(response.getPaymentId()).addRefund(refund);
                    showToast("Payment successfully refunded");

                } else {
                    showAlertDialog("Refund Error", response.getMessage());

                }
            }

            @Override
            public void onTipAdded(TipAddedMessage message) {
                if (message.tipAmount > 0) {
                    showToast("Tip successfully added: " + CurrencyUtils.format(message.tipAmount, Locale.getDefault()));
                }
            }

            @Override
            public void onVoidPaymentResponse(VoidPaymentResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    if (processedPaymentsMap.get(response.getPaymentId()) != null) {
                        processedPaymentsMap.get(response.getPaymentId()).setStatus(GoPayment.Status.VOIDED);
                    }

                    showToast("Payment successfully voided");

                } else {
                    showAlertDialog("Void Error", response.getMessage());

                }
            }

            @Override
            public void onManualRefundResponse(final ManualRefundResponse response) {
                dismissDialogs();
                if (response.isSuccess()) {
                    Credit credit = response.getCredit();
                    final GoNakedRefund nakedRefund = new GoNakedRefund(null, credit.getAmount());
                    goNakedRefunds.add(nakedRefund);
                    showManualRefunds();
                    showToast("Manual Refund successfully processed");
                } else if (response.getResult() == ResultCode.CANCEL) {
                    showToast("User canceled the Manual Refund");
                } else {
                    showToast("Manual Refund Failed with code: " + response.getResult() + " - " + response.getMessage());
                }
            }

            @Override
            public void onReadCardDataResponse(final ReadCardDataResponse response) {
                dismissDialogs();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GoPOSActivity.this);
                        builder.setTitle("Read Card Data Response");
                        if (response.isSuccess()) {

                            LayoutInflater inflater = GoPOSActivity.this.getLayoutInflater();

                            View view = inflater.inflate(R.layout.card_data_table, null);
                            ListView listView = view.findViewById(R.id.cardDataListView);


                            if (listView != null) {
                                class RowData {
                                    RowData(String label, String value) {
                                        this.text1 = label;
                                        this.text2 = value;
                                    }

                                    final String text1;
                                    final String text2;
                                }

                                ArrayAdapter<RowData> data = new ArrayAdapter<RowData>(getBaseContext(), android.R.layout.simple_list_item_2) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View v = convertView;

                                        if (v == null) {
                                            LayoutInflater vi;
                                            vi = LayoutInflater.from(getContext());
                                            v = vi.inflate(android.R.layout.simple_list_item_2, null);
                                        }

                                        RowData rowData = getItem(position);

                                        if (rowData != null) {
                                            TextView primaryColumn = v.findViewById(android.R.id.text1);
                                            primaryColumn.setTextIsSelectable(true);
                                            TextView secondaryColumn = v.findViewById(android.R.id.text2);
                                            secondaryColumn.setTextIsSelectable(true);

                                            primaryColumn.setText(rowData.text2);
                                            secondaryColumn.setText(rowData.text1);
                                        }

                                        return v;
                                    }
                                };
                                listView.setAdapter(data);
                                CardData cardData = response.getCardData();
                                if (cardData != null) {
                                    data.addAll(new RowData("Encrypted", cardData.encrypted + ""));
                                    data.addAll(new RowData("Cardholder Name", cardData.cardholderName));
                                    data.addAll(new RowData("First Name", cardData.firstName));
                                    data.addAll(new RowData("Last Name", cardData.lastName));
                                    data.addAll(new RowData("Expiration", cardData.exp));
                                    data.addAll(new RowData("First 6", cardData.first6));
                                    data.addAll(new RowData("Last 4", cardData.last4));
                                    data.addAll(new RowData("Track 1", cardData.track1));
                                    data.addAll(new RowData("Track 2", cardData.track2));
                                    data.addAll(new RowData("Track 3", cardData.track3));
                                    data.addAll(new RowData("Masked Track 1", cardData.maskedTrack1));
                                    data.addAll(new RowData("Masked Track 2", cardData.maskedTrack2));
                                    data.addAll(new RowData("Masked Track 3", cardData.maskedTrack3));
                                    data.addAll(new RowData("Pan", cardData.pan));
                                }

                                //Displaying whatever data we are getting from the read card response, can alter it to display
                                //only the required information.
                                GoCardData goCardData = response.getGoCardData();
                                if (goCardData != null) {
                                    if (goCardData.getKsn() != null) {
                                        data.addAll(new RowData("KSN", goCardData.getKsn()));
                                    }

                                    if (goCardData.getEncryptedTrack() != null) {
                                        data.addAll(new RowData("Encrypted Track", goCardData.getEncryptedTrack()));
                                    }

                                    if (goCardData.getTlv() != null) {
                                        data.addAll(new RowData("TLV", goCardData.getTlv()));
                                    }

                                    if (goCardData.getTrack1() != null) {
                                        data.addAll(new RowData("Track 1", goCardData.getTrack1()));
                                    }

                                    if (goCardData.getTrack2() != null) {
                                        data.addAll(new RowData("Track 2", goCardData.getTrack2()));
                                    }

                                    if (goCardData.getEquivalentTrack2() != null) {
                                        data.addAll(new RowData("Equivalent Track 2", goCardData.getEquivalentTrack2()));
                                    }

                                }

                            }
                            builder.setView(view);

                        } else if (response.getResult() == ResultCode.CANCEL) {
                            builder.setMessage("Get card data canceled.");
                        } else {
                            builder.setMessage("Error getting card data. " + response.getReason() + ": " + response.getMessage());
                        }

                        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });
            }

            @Override
            public void onVaultCardResponse(final VaultCardResponse response) {
                dismissDialogs();

                if (response.isSuccess()) {
                    GoCard card = new GoCard();
                    card.setFirst6(response.getCard().getFirst6());
                    card.setLast4(response.getCard().getLast4());
                    card.setName(response.getCard().getCardholderName());
                    card.setMonth(response.getCard().getExpirationDate().substring(0, 2));
                    card.setYear(response.getCard().getExpirationDate().substring(2, 4));
                    card.setToken(response.getCard().getToken());
                    goCards.add(card);

                    showToast("Card successfully vaulted");
                    showVaultCards();
                } else {
                    if (response.getResult() == ResultCode.CANCEL) {
                        showToast("User canceled the operation");
                        getCloverConnector().showWelcomeScreen();
                    } else {
                        showToast("Error capturing card: " + response.getResult());
                        getCloverConnector().showMessage("Card was not saved");
                        SystemClock.sleep(4000);
                        getCloverConnector().showWelcomeScreen();
                    }
                }
            }

            @Override
            public void onRetrievePaymentResponse(RetrievePaymentResponse response) {
                dismissDialogs();
                if (response.isSuccess()) {
                    showAlertDialog("Payment Response", response.getPayment().getJSONObject().toString());
                } else {
                    showToast("Unable to retrieve payment response");
                }
            }
        };

        if ((goReaderType == RP450 || goReaderType == RP350) && getCloverConnector() instanceof ICloverGoConnector) {
            ((ICloverGoConnector) getCloverConnector()).addCloverGoConnectorListener(ccGoListener);
        }
    }

    private void setConnectedStatus(MerchantInfo merchantInfo) {
        merchantInfoTxtVw.setText(String.format(merchantInfo.getDeviceInfo().getModel() + " Connected: %s (%s)",
                merchantInfo.getDeviceInfo().getSerial(),
                merchantInfo.getMerchantName()));
    }

    private void setDisconnectedStatus() {
        String name = merchantInfo != null ? merchantInfo.getMerchantName() : "";
        merchantInfoTxtVw.setText("Merchant: " + name);
    }

    public void captureCardClick(View view) {
        try {
            cloverConnector.vaultCard(getDefaultCloverGoCardEntryMethods());
        } catch (UnsupportedOperationException e) {
            showToast(e.getMessage());
        }
    }

    public void readCardDataClick(View view) {
        try {
            cloverConnector.readCardData(new ReadCardDataRequest(null));
        } catch (UnsupportedOperationException e) {
            showToast(e.getMessage());
        }
    }

    private void createCloverGoConnector(ReaderInfo.ReaderType readerType) {
        goReaderType = readerType;

        if (cGoConnectorMap.get(readerType) == null) {
            CloverGoDeviceConfiguration config = new CloverGoDeviceConfiguration
                    .Builder(getApplicationContext(),
                    startupParams.getAccessToken(),
                    startupParams.getEnvironment(),
                    startupParams.getApiKey(),
                    startupParams.getSecret(),
                    startupParams.getAppId(),
                    startupParams.getAppVersion())
                    .deviceType(readerType)
                    .allowAutoConnect(false)
                    .enableQuickChip(startupParams.isQuickChip())
                    .build();

            ICloverGoConnector cloverGoConnector = (CloverGoConnector) ConnectorFactory.createCloverConnector(config);
            cloverGoConnector.addCloverGoConnectorListener(ccGoListener);
        }
    }

    private void showPaymentInfo(PaymentResponse response) {
        Payment payment = response.getPayment();
        showAlertDialog("Payment Info",
                "Payment ID: " + payment.getId()
                        + "\nPayment External ID: " + payment.getExternalPaymentId()
                        + "\nOrder ID: " + payment.getOrder().getId()
                        + "\nAmount: " + payment.getAmount()
                        + "\nCard Holder Name: " + payment.getCardTransaction().getCardholderName()
                        + "\nCard Type: " + payment.getCardTransaction().getCardType()
                        + "\nTransaction Type: " + payment.getCardTransaction().getType()
                        + "\nEntry Type: " + payment.getCardTransaction().getEntryType()
                        + "\nAuth Code: " + payment.getCardTransaction().getAuthCode()
                        + "\nFirst 6: " + payment.getCardTransaction().getFirst6()
                        + "\nLast 4: " + payment.getCardTransaction().getLast4()
                        + "\nCVM Result: " + response.getPaymentCvmResult()
                        + "\nApplication Identifier: " + payment.getCardTransaction().getExtra().get(CARD_TRANSACTION_EXTRA_APPLICATION_IDENTIFIER));
    }

    private void showPaymentConfirmation(PaymentConfirmationListener listenerIn, Challenge challengeIn, int challengeIndexIn) {
        showChallengeDialog(R.string.payment_confirmation, R.string.reject, R.string.accept, listenerIn, challengeIn, challengeIndexIn);
    }

    private void showPartialAuthChallenge(PaymentConfirmationListener listenerIn, Challenge challengeIn, int challengeIndexIn) {
        showChallengeDialog(R.string.partial_auth_error_lbl, R.string.no, R.string.yes, listenerIn, challengeIn, challengeIndexIn);
    }

    private void showOfflineChallenge(PaymentConfirmationListener listenerIn, Challenge challengeIn, int challengeIndexIn) {
        showChallengeDialog(R.string.offline_transaction_lbl, android.R.string.no, android.R.string.ok, listenerIn, challengeIn, challengeIndexIn);
    }

    private void showChallengeDialog(int titleResId, int negativeButtonResId, int positiveButtonResId,
                                     PaymentConfirmationListener listenerIn, Challenge challengeIn,
                                     int challengeIndexIn) {

        showConfirmDialog(getString(titleResId), challengeIn.message, getString(positiveButtonResId), getString(negativeButtonResId),
                (dialog, which) -> {
                    listenerIn.onAcceptClicked(challengeIndexIn);
                    dialog.dismiss();
                },
                (dialog, which) -> {
                    listenerIn.onRejectClicked(challengeIn);
                    dialog.dismiss();
                }
        );
    }

    public void showOrders() {
        if (isFragmentVisible(OrdersFragment.TAG)) {
            return;
        }

        OrdersFragment orders = OrdersFragment.newInstance(processedOrders, cloverConnector);
        showFragment(orders);
    }

    public void showRegister() {
        showRegister(preAuthPayments);
    }

    public void showRegister(List<GoPayment> preAuthPayments) {
        if (isFragmentVisible(RegisterFragment.TAG)) {
            return;
        }

        RegisterFragment register = RegisterFragment.newInstance(cloverConnector, preAuthPayments);
        showFragment(register);
    }

    public void showMisc() {
        showMiscReadCard(false);
    }

    public void showMiscReadCard(boolean isReadCard) {
        if (isFragmentVisible(MiscFragment.TAG)) {
            return;
        }

        MiscFragment misc = MiscFragment.newInstance();
        if (isReadCard) {
            misc = MiscFragment.newInstance(cGoConnectorMap.get(RP450));
        }
        showFragment(misc);
    }

    public void showManualRefunds() {
        if (isFragmentVisible(ManualRefundsFragment.TAG)) {
            return;
        }
        showFragment(ManualRefundsFragment.newInstance(goNakedRefunds, cloverConnector));
    }

    public void showVaultCards() {
        if (isFragmentVisible(CardsFragment.TAG)) {
            return;
        }
        showFragment(CardsFragment.newInstance(goCards, cloverConnector));
    }

    public void connect350Click() {
        createCloverGoConnector(RP350);

        if (merchantInfoMap.get(RP350) == null) {
            cGoConnectorMap.get(RP350).initializeConnection();

        } else {
            showToast("Reader 350 Already Connected");
        }
    }

    public void connect450Click() {
        if (isBluetoothEnabled() && isGPSEnabled()) {
            showToast("If you don't see your readers, check and make sure you have granted this sample app location permission. This is needed to detect bluetooth readers (RP450)");
            createCloverGoConnector(RP450);

            if (merchantInfoMap.get(RP450) == null) {
                preferred450Reader = PreferenceUtil.getStringValue(this, MiscFragment.PREF_450);
                arrayListReadersList = new ArrayList<>();
                arrayListReaderString = new ArrayList<>();

                cGoConnectorMap.get(RP450).initializeConnection();
                showBluetoothReaders();

            } else {
                showToast("Reader 450 Already Connected");

            }
        } else {
            showToast("Enable GPS and Bluetooth. Make sure to grant location permission as well.");

        }
    }

    public void getLogs() {
        showAlertDialog("Logs Generated", "Check Android/data/com.example.cloverexamplego/files/logFiles directory");
    }

    public void disconnect450() {
        ICloverGoConnector cloverGoConnector = cGoConnectorMap.get(ReaderInfo.ReaderType.RP450);

        if (cGoConnectorMap.get(ReaderInfo.ReaderType.RP450) != null) {
            cloverGoConnector.disconnectDevice();

        } else {
            showToast("No Bluetooth reader found.  To disconnect the RP350 Audio Jack reader, just pull it out of the audio jack.");
        }
    }

    private void showBluetoothReaders() {
        readerListDialog = new Dialog(this, R.style.selectReaderDialog);
        readerListDialog.setContentView(R.layout.dialog_layout);
        ((TextView) readerListDialog.findViewById(R.id.dialogTitle)).setText("Select Bluetooth Reader");
        ICloverGoConnector iCloverGoConnector = ((ICloverGoConnector) getCloverConnector());

        readerListDialog.setCancelable(false);
        readerListDialog.setCanceledOnTouchOutside(false);
        readerListDialog.setOnCancelListener(dialogInterface -> {
            if (iCloverGoConnector != null) {
                iCloverGoConnector.stopDeviceScan();
            }
            readerListDialog.dismiss();
        });

        readerListDialog.findViewById(R.id.dialogBtn).setOnClickListener(view -> {
            if (iCloverGoConnector != null) {
                iCloverGoConnector.stopDeviceScan();
            }
            readerListDialog.dismiss();
        });

        readerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrayListReaderString);
        ListView mBluetoothReadersListVw = readerListDialog.findViewById(R.id.dialogList);
        mBluetoothReadersListVw.setAdapter(readerArrayAdapter);
        mBluetoothReadersListVw.setOnItemClickListener((parent, view, position, id) -> {
            ReaderInfo readerInfo = arrayListReadersList.get(position);
            if (iCloverGoConnector != null) {
                iCloverGoConnector.connectToBluetoothDevice(readerInfo);
            }
            showToast(readerInfo.getBluetoothName() + "\n" + readerInfo.getBluetoothIdentifier());
            readerListDialog.dismiss();
        });
        readerArrayAdapter.notifyDataSetChanged();

        readerListDialog.show();
    }

    public void goPaymentTypeSelected(ICloverGoConnector.GoPaymentType paymentType) {
        if (!isReadCardDemo) {
            showRegister();
        }
        Log.d(TAG, "Proceeding with transaction, paymentType: " + paymentType.name());

        if (paymentType == ICloverGoConnector.GoPaymentType.RP350) {
            goReaderType = RP350;
        } else if (paymentType == ICloverGoConnector.GoPaymentType.RP450) {
            goReaderType = RP450;
        }
        paymentTypeSelection.selectPaymentType(paymentType, goReaderType);
    }

    private ICloverConnector getCloverConnector() {
        if (goReaderType != null && cGoConnectorMap.get(goReaderType) != null) {
            return cGoConnectorMap.get(goReaderType);
        } else {
            return null;
        }
    }

    public void showProgressDialog(String title, String message, boolean isCancelable) {
        dismissDialogs();
        progressDialog = DialogHelper.showProgressDialog(this, title, message, isCancelable, "Cancel", (dialog, which) -> getCloverConnector().cancel());
        progressDialog.show();
    }

    public void showAlertDialog(String title, String message, DialogInterface.OnClickListener... listener) {
        dismissDialogs();
        alertDialog = DialogHelper.createAlertDialog(this, title, message, "OK", listener.length > 0 ? listener[0] : (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    public void showConfirmDialog(String title, String message, String positiveTxt,
                                  String negativeTxt, DialogInterface.OnClickListener positiveBtnListener,
                                  DialogInterface.OnClickListener negativeBtnListener) {
        dismissDialogs();
        confirmDialog = DialogHelper.createConfirmDialog(this, title, message,
                positiveTxt, negativeTxt, positiveBtnListener, negativeBtnListener);
        confirmDialog.show();
    }

    private void dismissDialogs() {
        hideKeyboard();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        toast.show();
    }

    public boolean isValidAmount(TextView view) {
        if (TextUtils.isEmpty(view.getText().toString())) {
            showToast("Please enter amount in cents");
            return false;
        }
        return true;
    }

    public void hideKeyboard() {
        DeviceUtil.hideKeyboard(this.getCurrentFocus());
    }

    private boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter == null || mBluetoothAdapter.isEnabled();
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public int getDefaultCloverGoCardEntryMethods() {
        return Constants.CARD_ENTRY_METHOD_MANUAL |
                Constants.CARD_ENTRY_METHOD_ICC_CONTACT |
                Constants.CARD_ENTRY_METHOD_NFC_CONTACTLESS;
    }

    public int getCloverGoCardEntryMethodState() {
        return cardEntryMethodState;
    }

    public void setCloverGoCardEntryMethodState(int state) {
        cardEntryMethodState = state;
    }

    private void addNewProcessedOrder(GoOrder order) {
        processedOrders.add(order);

        GoPayment payment = (GoPayment) order.getPayments().get(0);
        processedOrdersMap.put(payment.getPayment().getId(), order);
        processedPaymentsMap.put(payment.getPayment().getId(), payment);
    }

    private void showFragment(BaseFragment fragment) {
        hideKeyboard();
        disableOrEnableBottomBar(fragment.getFragmentTag());
        getFragmentManager().beginTransaction().replace(R.id.mainContainer, fragment, fragment.getFragmentTag()).commit();
    }

    private void disableOrEnableBottomBar(String fragmentTag) {
        if (fragmentTag.equals(SignatureFragment.TAG) || fragmentTag.equals(SendReceiptFragment.TAG)) {

            navigationView.setOnNavigationItemSelectedListener(null);

        } else if (isReadCardDemo) {

            navigationView.setOnNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.action_misc) {
                    showMiscReadCard(true);
                }
                return true;
            });

        } else {
            navigationView.setOnNavigationItemSelectedListener(getNavigationListener());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener getNavigationListener() {
        if (navigationListener == null) {
            navigationListener = item -> {
                switch (item.getItemId()) {
                    case R.id.action_register:
                        showRegister();
                        break;
                    case R.id.action_orders:
                        showOrders();
                        break;
                    case R.id.action_misc:
                        showMisc();
                        break;
                    case R.id.action_manual_refund:
                        showManualRefunds();
                        break;
                    case R.id.action_vault_card:
                        showVaultCards();
                        break;
                }
                return true;
            };
        }
        return navigationListener;
    }

    private boolean isFragmentVisible(String tag) {
        return getFragmentManager().findFragmentByTag(tag) != null && getFragmentManager().findFragmentByTag(tag).isVisible();
    }
}
