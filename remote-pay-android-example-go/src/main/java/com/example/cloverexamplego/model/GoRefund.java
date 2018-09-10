package com.example.cloverexamplego.model;

import com.clover.sdk.v3.payments.Refund;

public class GoRefund extends GoExchange{

    private Refund refund;

    public GoRefund(Refund refund) {
        this.refund = refund;
    }

    public Refund getRefund() {
        return refund;
    }
}
