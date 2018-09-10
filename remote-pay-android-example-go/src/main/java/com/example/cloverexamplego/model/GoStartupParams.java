package com.example.cloverexamplego.model;

import com.clover.remote.client.clovergo.CloverGoDeviceConfiguration;

import java.io.Serializable;

public class GoStartupParams implements Serializable {
    private String accessToken;
    private String appId;
    private String appVersion;
    private String apiKey;
    private String secret;
    private boolean quickChip;

    private CloverGoDeviceConfiguration.ENV environment;

    public GoStartupParams(String accessToken, String appId, String appVersion, String apiKey, String secret, boolean quickChip, CloverGoDeviceConfiguration.ENV environment) {
        this.accessToken = accessToken;
        this.appId = appId;
        this.appVersion = appVersion;
        this.apiKey = apiKey;
        this.secret = secret;
        this.quickChip = quickChip;
        this.environment = environment;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public boolean isQuickChip() {
        return quickChip;
    }

    public void setQuickChip(boolean quickChip) {
        this.quickChip = quickChip;
    }

    public CloverGoDeviceConfiguration.ENV getEnvironment() {
        return environment;
    }

    public void setEnvironment(CloverGoDeviceConfiguration.ENV environment) {
        this.environment = environment;
    }
}