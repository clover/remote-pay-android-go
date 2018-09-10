package com.example.cloverexamplego.model;

import com.clover.remote.client.messages.PaymentResponse;
import com.clover.sdk.v3.payments.Payment;

public class GoPayment extends GoExchange {

    private Payment payment;
    private Status status;
    private long tipAmount;

    public enum Status {
        PAID, VOIDED, PARTIALLYREFUNDED, REFUNDED, AUTHORIZED, PREAUTHORIZED, OFFLINE
    }

    public GoPayment(PaymentResponse paymentResponse) {
        this.payment = paymentResponse.getPayment();
        this.status = getStatus(paymentResponse);
    }

    public Payment getPayment() {
        return payment;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    //Can make this public if being used somewhere else.
    private static Status getStatus(PaymentResponse response) {
        Status status = Status.PAID;
        if (response.isSale()) {
            status = Status.PAID;
        } else if (response.isAuth()) {
            status = Status.AUTHORIZED;
        } else if (response.isPreAuth()) {
            status = Status.PREAUTHORIZED;
        }
        return status;
    }

    public long getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(long tipAmount) {
        this.tipAmount = tipAmount;
    }
}
