package com.example.cloverexamplego.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Validator {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean validateEmailInput(String email) {
        boolean isValidEmail = Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
        return isValidEmail;
    }

    public static boolean validatePhoneNumberInput(String phoneNumber) {
        // Strip out non-numerics
        String numberString = phoneNumber.replaceAll("[^\\d]", "");
        boolean isValidPhoneNumber = numberString.length() >= 10 && Patterns.PHONE.matcher(numberString).matches();
        return isValidPhoneNumber;
    }


    public static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}