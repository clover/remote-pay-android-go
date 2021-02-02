/*
 * Copyright (C) 2018 Clover Network, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cloverexamplego.activities;

import com.clover.remote.client.clovergo.CloverGoDeviceConfiguration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cloverexamplego.R;
import com.example.cloverexamplego.model.GoStartupParams;
import com.example.cloverexamplego.rest.ApiClient;
import com.example.cloverexamplego.rest.ApiInterface;
import com.example.cloverexamplego.utils.PreferenceUtil;
import com.example.cloverexamplego.utils.Validator;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;

import static com.clover.remote.client.clovergo.CloverGoDeviceConfiguration.ENV.LIVE;
import static com.clover.remote.client.clovergo.CloverGoDeviceConfiguration.ENV.SANDBOX;

public class GoStartupActivity extends Activity {
    public static final String EXAMPLE_APP_NAME = "EXAMPLE_APP";
    public static final String EXTRA_CLOVER_GO_CODE = "EXTRA_CLOVER_GO_CODE";

    public static final String EXTRA_CLOVER_GO_CLIENT = "EXTRA_CLOVER_GO_CLIENT";
    public static final String EXTRA_CLOVER_GO_ACCESS_TOKEN = "EXTRA_CLOVER_GO_ACCESS_TOKEN";
    public static final int OAUTH_REQUEST_CODE = 100;
    public static final int OAUTH_REQUEST_TOKEN = 101;

    private static final String PREF_QUICK_CHIP = "PREF_QUICK_CHIP";
    private static final String PREF_ENV = "PREF_ENV";

    private String baseUrl = "sandbox.dev.clover.com";
    private String goApiKey, goSecret, demoAccessToken;
    private String oAuthClientId, oAuthClientSecret, oAuthUrl, oAuthTokenUrl;
    private String appId, appVersion;
    private CloverGoDeviceConfiguration.ENV goEnv = SANDBOX;
    private boolean quickChip;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        TextView version = findViewById(R.id.version);
        try {
            String versionName = "Version : " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            showToast("Could not get package name");
        }

        RadioButton sandboxRBtn = findViewById(R.id.sandboxEnvRadioBtn);
        RadioButton liveRBtn = findViewById(R.id.liveEnvRadioBtn);

        sandboxRBtn.setOnClickListener(view -> setEnvironment(SANDBOX));
        liveRBtn.setOnClickListener(view -> setEnvironment(LIVE));

        String envPref = PreferenceUtil.getStringValue(this, PREF_ENV);
        if (LIVE.name().equalsIgnoreCase(envPref)) {
            goEnv = LIVE;
            liveRBtn.setChecked(true);
        } else if (CloverGoDeviceConfiguration.ENV.SANDBOX.name().equalsIgnoreCase(envPref)) {
            goEnv = SANDBOX;
            sandboxRBtn.setChecked(true);
        }

        Switch quickChipSwitch = findViewById(R.id.quickChipSwitch);
        quickChipSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> quickChip = isChecked);
        quickChipSwitch.setChecked(getSharedPreferences(EXAMPLE_APP_NAME, Context.MODE_PRIVATE).getBoolean(PREF_QUICK_CHIP, false));

        findViewById(R.id.launchButton).setOnClickListener(view -> connect());
        findViewById(R.id.oAuthCodeButton).setOnClickListener(view -> connectGoWithAuthMode());
        findViewById(R.id.oAuthTokenButton).setOnClickListener(view -> connectGoWithNewAuthMode());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == OAUTH_REQUEST_CODE) {
            String clientId = data.getStringExtra(EXTRA_CLOVER_GO_CLIENT);
            String code = data.getStringExtra(EXTRA_CLOVER_GO_CODE);
            getAccessToken(clientId, code);

        } else if (data != null && requestCode == OAUTH_REQUEST_TOKEN) {
            setGoParams();
            String token = data.getStringExtra(EXTRA_CLOVER_GO_ACCESS_TOKEN);

            Intent intent = new Intent();
            intent.setClass(this, GoPOSActivity.class);
            startActivityWithGo(token);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            String merchantId = "";
            String employeeId = "";
            String clientId = "";
            String code = "";
            String value;

            for (String param : uri.getQueryParameterNames()) {
                value = uri.getQueryParameter(param);

                if (!TextUtils.isEmpty(value)) {
                    if (param.equalsIgnoreCase("merchant_id")) {
                        merchantId = value;
                    } else if (param.equalsIgnoreCase("employee_id")) {
                        employeeId = value;
                    } else if (param.equalsIgnoreCase("client_id")) {
                        clientId = value;
                    } else if (param.equalsIgnoreCase("code")) {
                        code = value;
                    }
                }
            }
            getAccessToken(clientId, code);
        }
    }

    public void connect() {
        setGoParams();
        Intent intent = new Intent();
        intent.setClass(this, GoPOSActivity.class);

        startActivityWithGo(demoAccessToken);
    }

    public void connectGoWithNewAuthMode() {
        if (goEnv == LIVE) {
            showToast("Do not use this on LIVE environment");
            return;
        }

        setGoParams();

        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_CLOVER_GO_TOKEN_URL, oAuthTokenUrl);
        startActivityForResult(intent, OAUTH_REQUEST_TOKEN);
    }

    public void connectGoWithAuthMode() {
        setGoParams();

        Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.EXTRA_CLOVER_GO_CODE_URL, oAuthUrl);
        startActivityForResult(intent, OAUTH_REQUEST_CODE);
    }

    private void getAccessToken(String clientId, String code) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Merchant account loading....");
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient(baseUrl).create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.getAccessToken(clientId, oAuthClientSecret, code);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.has("message")) {
                            String err = jsonObject.getString("message");
                            showToast(err);
                        } else if (jsonObject.has("access_token")) {

                            if (Validator.isNetworkConnected(GoStartupActivity.this)) {
                                String accessToken = jsonObject.getString("access_token");

                                Intent intent = new Intent();
                                intent.setClass(GoStartupActivity.this, GoPOSActivity.class);
                                startActivityWithGo(accessToken);

                            } else {
                                showToast("Check Internet Connection");
                            }
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void setEnvironment(CloverGoDeviceConfiguration.ENV env) {
        goEnv = env;
        PreferenceUtil.saveString(this, PREF_ENV, goEnv.name());
    }

    private void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        }
        toast.setText(message);
        toast.show();
    }

    private void startActivityWithGo(String token) {
        PreferenceUtil.saveString(this, PREF_ENV, goEnv.name());
        getSharedPreferences(EXAMPLE_APP_NAME, Context.MODE_PRIVATE).edit().putBoolean(PREF_QUICK_CHIP, quickChip).commit();

        GoStartupParams params = new GoStartupParams(token, appId, appVersion, goApiKey, goSecret, quickChip, goEnv);

        Intent intent = new Intent();
        intent.setClass(this, GoPOSActivity.class);
        intent.putExtra(GoPOSActivity.EXTRA_GO_PARAMS, params);
        startActivity(intent);
    }

    /**
     * appId - your app's ID
     * appId = "<put your APP ID here>";
     * <p>
     * appVersion - your app's version
     * appVersion = "<put your APP VERSION here>";
     * <p>
     * These should be provided to you.
     * goApiKey = "<put your key here>";
     * goSecret = "<put your secret here>";
     * <p>
     * This is used for demo purposes. You can generate an access token and hardcode it here so
     * that you can use the same access token repeatedly.
     * demoAccessToken = "<put your token here>";
     * <p>
     * App ID found in developer portal app settings.
     * oAuthClientId = "<put your Client ID here>";
     * <p>
     * App secret found in developer portal app settings.
     * oAuthClientSecret = "<put your secret here>";
     */
    private void setGoParams() {
        appId = "<put your APP ID here>";
        appVersion = "<put your APP VERSION here>";

        if (goEnv == LIVE) {
            goApiKey = "<put your key here>";
            goSecret = "<put your secret here>";

            demoAccessToken = "<put your token here>";

            oAuthClientId = "<put your Client ID here>";
            oAuthClientSecret = "<put your secret here>";

            baseUrl = "clover.com";

        } else if (goEnv == CloverGoDeviceConfiguration.ENV.SANDBOX) {
            goApiKey = "<put your key here>";
            goSecret = "<put your secret here>";

            demoAccessToken = "<put your token here>";

            oAuthClientId = "<put your Client ID here>";
            oAuthClientSecret = "<put your secret here>";

            baseUrl = "sandbox.dev.clover.com";

        }

        oAuthUrl = "https://" + baseUrl + "/oauth/authorize?client_id=" + oAuthClientId + "&response_type=code";
        oAuthTokenUrl = "https://" + baseUrl + "/oauth/authorize?client_id=" + oAuthClientId + "&response_type=token";
    }
}