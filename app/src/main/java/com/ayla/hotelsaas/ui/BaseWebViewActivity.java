package com.ayla.hotelsaas.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import wendu.dsbridge.DWebView;

public abstract class BaseWebViewActivity extends BaseMvpActivity {
    private final String TAG = this.getClass().getSimpleName();

    private DWebView mWebView;

    private boolean hasError, hasLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView = getWebView();
        View emptyView = getEmptyView();
        mWebView.getSettings().setCacheMode(Constance.isNetworkDebug() ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            // 加载主框架出错时会被回调的方法 API<23
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.d(TAG, "onReceivedError1: " + errorCode + description);
                hasError = true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                int errorCode = error.getErrorCode();
                Log.d(TAG, "onReceivedError2: " + errorCode + error.getDescription());
                hasError = true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted: ");
                hasError = false;
                showProgress();
                emptyView.setVisibility(View.INVISIBLE);
                mWebView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished: ");
                hideProgress();
                if (!hasLoaded) {
                    if (hasError) {
                        mWebView.setVisibility(View.INVISIBLE);
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        hasLoaded = true;
                        mWebView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        IX5WebViewExtension x5WebViewExtension = mWebView.getX5WebViewExtension();
        Log.d(TAG, "onCreate: "+x5WebViewExtension);
        WebView.setWebContentsDebuggingEnabled(true);
    }

    protected abstract View getEmptyView();

    protected abstract DWebView getWebView();

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
