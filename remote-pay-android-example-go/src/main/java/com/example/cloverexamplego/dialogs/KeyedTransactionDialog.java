package com.example.cloverexamplego.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.clover.remote.client.clovergo.CloverGoConstants.TransactionType;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.clover.remote.client.clovergo.messages.KeyedAuthRequest;
import com.clover.remote.client.clovergo.messages.KeyedManualRefundRequest;
import com.clover.remote.client.clovergo.messages.KeyedPreAuthRequest;
import com.clover.remote.client.clovergo.messages.KeyedSaleRequest;
import com.clover.remote.client.clovergo.messages.KeyedVaultCardRequest;
import com.clover.remote.client.messages.BaseRequest;
import com.clover.remote.client.messages.TransactionRequest;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.utils.IdUtils;
import com.firstdata.clovergo.domain.utils.CreditCardUtil;

public class KeyedTransactionDialog extends BaseDialog {
    public static final String TAG = "KeyedTransactionDialog";

    private final String CARD_NUM = "4111111111111111";
    private final String CARD_EXP = "1224";
    private final String CARD_CVV = "333";

    private ICloverGoConnectorListener.ManualCardEntry mManualCardEntryListnr;

    private TransactionType mTransactionType;

    private BaseRequest mBaseRequest;

    public static KeyedTransactionDialog newInstance(TransactionType transactionType,
                                                     BaseRequest baseRequest,
                                                     ICloverGoConnectorListener.ManualCardEntry manualCardEntry) {
        KeyedTransactionDialog keyedTransactionDialog = new KeyedTransactionDialog();
        keyedTransactionDialog.mTransactionType = transactionType;
        keyedTransactionDialog.mBaseRequest = baseRequest;
        keyedTransactionDialog.mManualCardEntryListnr = manualCardEntry;
        return keyedTransactionDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_keyed_transaction, container, false);

        EditText mCardNumber = view.findViewById(R.id.cardNumberEditText);
        EditText mExpiration = view.findViewById(R.id.expirationEditText);
        EditText mCvv = view.findViewById(R.id.cvvEditText);

        view.findViewById(R.id.quickFill).setOnClickListener(view1 -> {
            mCardNumber.setText(CARD_NUM);
            mExpiration.setText(CARD_EXP);
            mCvv.setText(CARD_CVV);
        });

        view.findViewById(R.id.startTransactionBtn).setOnClickListener(view12 -> {
            String cardNumber = mCardNumber.getText().toString();
            if (!CreditCardUtil.validateCard(cardNumber)) {
                getPOSActivity().showToast("Please enter a valid card number");
                return;
            }

            String expiration = (mExpiration.getText().toString().replace("/", ""));
            if (!CreditCardUtil.validateCardExpiry(expiration)) {
                getPOSActivity().showToast("Please enter a valid card expiration");
                return;
            }
            String cvv = mCvv.getText().toString();
            int defaultCvvLength = CreditCardUtil.getCardType(cardNumber) == CreditCardUtil.AMEX ? 4 : 3;

            if (cvv.length() != defaultCvvLength) {
                getPOSActivity().showToast("Please enter a valid cvv number");
                return;
            }

            getPOSActivity().showProgressDialog("Keyed Transaction", "Processing Transaction", false);
            getDialog().dismiss();


            if (mBaseRequest instanceof TransactionRequest) {
                TransactionRequest request = null;
                TransactionRequest transactionRequest = (TransactionRequest) mBaseRequest;

                if (mTransactionType == TransactionType.SALE) {
                    request = new KeyedSaleRequest(transactionRequest.getAmount(),
                            transactionRequest.getExternalId(),
                            cardNumber,
                            expiration,
                            cvv);
                } else if (mTransactionType == TransactionType.AUTH) {
                    request = new KeyedAuthRequest(transactionRequest.getAmount(),
                            transactionRequest.getExternalId(),
                            cardNumber,
                            expiration,
                            cvv);
                } else if (mTransactionType == TransactionType.PRE_AUTH) {
                    request = new KeyedPreAuthRequest(transactionRequest.getAmount(),
                            transactionRequest.getExternalId(),
                            cardNumber,
                            expiration,
                            cvv);
                } else if (mTransactionType == TransactionType.MANUAL_REFUND) {
                    request = new KeyedManualRefundRequest(transactionRequest.getAmount(),
                            IdUtils.getNextId(),
                            cardNumber,
                            expiration,
                            cvv);
                }
                if (request != null) {
                    request.setNote(transactionRequest.getNote());
                    doneKeyEntry(request, mTransactionType);
                }
            } else {
                if (mTransactionType == TransactionType.VAULT_CARD) {
                    KeyedVaultCardRequest keyedVaultCardRequest = new KeyedVaultCardRequest(cardNumber, expiration, cvv);
                    doneKeyEntry(keyedVaultCardRequest, mTransactionType);
                }
            }
        });

        return view;
    }

    private void doneKeyEntry(BaseRequest baseRequest, TransactionType transactionType) {
        mManualCardEntryListnr.cardDataEntered(baseRequest, transactionType);
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}