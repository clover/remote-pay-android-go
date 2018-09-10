package com.example.cloverexamplego.rest;

import retrofit2.Retrofit;

/**
 * Created by Avdhesh Akhani on 3/15/17.
 */

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://" + baseUrl)
                    .build();
        }
        return retrofit;
    }
}