package com.ayla.hotelsaas.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;

public abstract class BaseWebViewActivity extends BaseMvpActivity {
    private final String TAG = this.getClass().getSimpleName();

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = getWebView();
        View emptyView = getEmptyView();
        mWebView.getSettings().setCacheMode(Constance.isNetworkDebug ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            // 加载主框架出错时会被回调的方法 API<23
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError: " + description);
                if (mWebView.getVisibility() != View.VISIBLE) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError: ");
                if (mWebView.getVisibility() != View.VISIBLE) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: ");
                showProgress();
                emptyView.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: ");
                hideProgress();
                mWebView.setVisibility(View.VISIBLE);
            }
        });
        WebView.setWebContentsDebuggingEnabled(Constance.isNetworkDebug);
    }

    protected abstract View getEmptyView();

    protected abstract WebView getWebView();

    protected void handleRefreshClick() {
        mWebView.reload();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}
