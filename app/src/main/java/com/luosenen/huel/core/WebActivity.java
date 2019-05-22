package com.luosenen.huel.core;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.luosenen.huel.R;

public class WebActivity extends Activity {
    private WebView webView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.webLay),
                "正在链接Google服务，请稍等！",
                Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.WHITE);
        View mView = snackbar.getView();
        mView.setBackgroundColor(Color.GRAY);
        snackbar.show();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl("https://ipv6.google-api.ac.cn");
    }
}
