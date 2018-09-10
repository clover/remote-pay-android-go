package com.example.cloverexamplego.model;

import com.clover.remote.client.MerchantInfo;
import com.clover.remote.client.clovergo.CloverGoConstants;
import com.clover.remote.client.clovergo.ICloverGoConnector;
import com.clover.remote.client.clovergo.ICloverGoConnectorListener;
import com.clover.remote.client.messages.AuthResponse;
import com.clover.remote.client.messages.BaseRequest;
import com.clover.remote.client.messages.CapturePreAuthResponse;
import com.clover.remote.client.messages.CardApplicationIdentifier;
import com.clover.remote.client.messages.CloseoutResponse;
import com.clover.remote.client.messages.CloverDeviceErrorEvent;
import com.clover.remote.client.messages.CloverDeviceEvent;
import com.clover.remote.client.messages.ConfirmPaymentRequest;
import com.clover.remote.client.messages.CustomActivityResponse;
import com.clover.remote.client.messages.ManualRefundResponse;
import com.clover.remote.client.messages.MessageFromActivity;
import com.clover.remote.client.messages.PreAuthResponse;
import com.clover.remote.client.messages.PrintJobStatusResponse;
import com.clover.remote.client.messages.PrintManualRefundDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintManualRefundReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentDeclineReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentMerchantCopyReceiptMessage;
import com.clover.remote.client.messages.PrintPaymentReceiptMessage;
import com.clover.remote.client.messages.PrintRefundPaymentReceiptMessage;
import com.clover.remote.client.messages.ReadCardDataResponse;
import com.clover.remote.client.messages.RefundPaymentResponse;
import com.clover.remote.client.messages.ResetDeviceResponse;
import com.clover.remote.client.messages.RetrieveDeviceStatusResponse;
import com.clover.remote.client.messages.RetrievePaymentResponse;
import com.clover.remote.client.messages.RetrievePendingPaymentsResponse;
import com.clover.remote.client.messages.RetrievePrintersResponse;
import com.clover.remote.client.messages.SaleResponse;
import com.clover.remote.client.messages.TipAdjustAuthResponse;
import com.clover.remote.client.messages.TransactionRequest;
import com.clover.remote.client.messages.VaultCardResponse;
import com.clover.remote.client.messages.VerifySignatureRequest;
import com.clover.remote.client.messages.VoidPaymentResponse;
import com.clover.remote.message.TipAddedMessage;

import com.firstdata.clovergo.domain.model.Order;
import com.firstdata.clovergo.domain.model.Payment;
import com.firstdata.clovergo.domain.model.ReaderInfo;

import java.util.List;

/**
 * This class acts as an adapter - which helps to implement only the required methods
 */

public class GoConnectorListener implements ICloverGoConnectorListener {

    @Override
    public void onDeviceActivityStart(CloverDeviceEvent deviceEvent) {

    }

    @Override
    public void onDeviceActivityEnd(CloverDeviceEvent deviceEvent) {

    }

    @Override
    public void onDeviceError(CloverDeviceErrorEvent deviceErrorEvent) {

    }

    @Override
    public void onPreAuthResponse(PreAuthResponse response) {

    }

    @Override
    public void onAuthResponse(AuthResponse response) {

    }

    @Override
    public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {

    }

    @Override
    public void onCapturePreAuthResponse(CapturePreAuthResponse response) {

    }

    @Override
    public void onVerifySignatureRequest(VerifySignatureRequest request) {

    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {

    }

    @Override
    public void onCloseoutResponse(CloseoutResponse response) {

    }

    @Override
    public void onSaleResponse(SaleResponse response) {

    }

    @Override
    public void onManualRefundResponse(ManualRefundResponse response) {

    }

    @Override
    public void onRefundPaymentResponse(RefundPaymentResponse response) {

    }

    @Override
    public void onTipAdded(TipAddedMessage message) {

    }

    @Override
    public void onVoidPaymentResponse(VoidPaymentResponse response) {

    }

    @Override
    public void onDeviceDisconnected() {

    }

    @Override
    public void onDeviceConnected() {

    }

    @Override
    public void onDeviceReady(MerchantInfo merchantInfo) {

    }

    @Override
    public void onVaultCardResponse(VaultCardResponse response) {

    }

    @Override
    public void onPrintJobStatusResponse(PrintJobStatusResponse response) {

    }

    @Override
    public void onRetrievePrintersResponse(RetrievePrintersResponse response) {

    }

    @Override
    public void onPrintManualRefundReceipt(PrintManualRefundReceiptMessage message) {

    }

    @Override
    public void onPrintManualRefundDeclineReceipt(PrintManualRefundDeclineReceiptMessage message) {

    }

    @Override
    public void onPrintPaymentReceipt(PrintPaymentReceiptMessage message) {

    }

    @Override
    public void onPrintPaymentDeclineReceipt(PrintPaymentDeclineReceiptMessage message) {

    }

    @Override
    public void onPrintPaymentMerchantCopyReceipt(PrintPaymentMerchantCopyReceiptMessage message) {

    }

    @Override
    public void onPrintRefundPaymentReceipt(PrintRefundPaymentReceiptMessage message) {

    }

    @Override
    public void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse response) {

    }

    @Override
    public void onReadCardDataResponse(ReadCardDataResponse response) {

    }

    @Override
    public void onMessageFromActivity(MessageFromActivity message) {

    }

    @Override
    public void onCustomActivityResponse(CustomActivityResponse response) {

    }

    @Override
    public void onRetrieveDeviceStatusResponse(RetrieveDeviceStatusResponse response) {

    }

    @Override
    public void onResetDeviceResponse(ResetDeviceResponse response) {

    }

    @Override
    public void onRetrievePaymentResponse(RetrievePaymentResponse response) {

    }

    @Override
    public void onDeviceDiscovered(ReaderInfo readerInfo) {

    }

    @Override
    public void onDeviceDisconnected(ReaderInfo readerInfo) {

    }

    @Override
    public void onAidMatch(List<CardApplicationIdentifier> applicationIdentifierList, AidSelection aidSelection) {

    }

    @Override
    public void onPaymentTypeRequired(int cardEntryMethods, List<ReaderInfo> connectedReaders, PaymentTypeSelection paymentTypeSelection) {

    }

    @Override
    public void onManualCardEntryRequired(CloverGoConstants.TransactionType transactionType, BaseRequest baseRequest, ICloverGoConnector.GoPaymentType goPaymentType, ReaderInfo.ReaderType readerType, boolean allowDuplicate, ManualCardEntry manualCardEntry) {

    }

    @Override
    public void notifyOnProgressDialog(String title, String message, boolean isCancelable) {

    }

    @Override
    public void onCloverGoDeviceActivity(CloverDeviceEvent deviceEvent) {

    }

    @Override
    public void onGetMerchantInfo() {

    }

    @Override
    public void onGetMerchantInfoResponse(MerchantInfo merchantInfo) {

    }

    @Override
    public void onSignatureRequired(Payment payment, SignatureCapture signatureCapture) {

    }

    @Override
    public void onSendReceipt(Order order, SendReceipt sendReceipt) {

    }

    @Override
    public void onDisplayMessage(String message) {

    }

    @Override
    public void onVoidPayment(Payment payment, String reason) {

    }
}
