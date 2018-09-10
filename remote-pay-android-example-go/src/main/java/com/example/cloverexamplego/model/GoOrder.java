package com.example.cloverexamplego.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GoOrder {

    private String id;
    private List<GoExchange> payments;
    private Date date;
    private long amount;

    public GoOrder(GoPayment goPayment) {
        this.id = goPayment.getPayment().getOrder().getId();
        amount = goPayment.getPayment().getAmount();
        date = new Date();

        payments = new ArrayList<>();
        payments.add(goPayment);
    }

    public String getId() {
        return id;
    }

    public List<GoExchange> getPayments() {
        return payments;
    }

    public Date getDate() {
        return date;
    }

    public long getAmount() {
        return amount;
    }

    public void addRefund(GoRefund refund) {
        payments.add(refund);
    }
}