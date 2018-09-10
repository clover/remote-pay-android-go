package com.example.cloverexamplego.interfaces;

import com.clover.remote.Challenge;

public interface PaymentConfirmationListener {
    void onRejectClicked(Challenge challenge);

    void onAcceptClicked(int challengeIndex);
}