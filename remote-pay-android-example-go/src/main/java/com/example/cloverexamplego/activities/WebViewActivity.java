package com.example.cloverexamplego.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.cloverexamplego.R;

public class WebViewActivity extends Activity {
    public static final String EXTRA_CLOVER_GO_CODE_URL = "EXTRA_CLOVER_GO_CODE_URL";
    public static final String EXTRA_CLOVER_GO_TOKEN_URL = "EXTRA_CLOVER_GO_TOKEN_URL";

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String baseUrl = getIntent().getStringExtra(EXTRA_CLOVER_GO_TOKEN_URL);
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = getIntent().getStringExtra(EXTRA_CLOVER_GO_CODE_URL);
        }

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String codeFragment = "&code=";
                String accessTokenFragment = "#access_token=";

                if (url.indexOf(codeFragment) > -1) {
                    Uri uri = Uri.parse(url);
                    String clientId = uri.getQueryParameter("client_id");
                    String code = uri.getQueryParameter("code");

                    Intent output = new Intent();
                    output.putExtra(GoStartupActivity.EXTRA_CLOVER_GO_CODE, code);
                    output.putExtra(GoStartupActivity.EXTRA_CLOVER_GO_CLIENT, clientId);
                    setResult(RESULT_OK, output);
                    finish();

                } else if (url.indexOf(accessTokenFragment) > -1) {
                    String accessToken = url.substring(url.indexOf(accessTokenFragment) + accessTokenFragment.length(), url.length());

                    Intent output = new Intent();
                    output.putExtra(GoStartupActivity.EXTRA_CLOVER_GO_ACCESS_TOKEN, accessToken);
                    setResult(RESULT_OK, output);
                    finish();
                }
            }
        });

        webView.loadUrl(baseUrl);
    }
}