package com.ayla.hotelsaas.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.blankj.utilcode.util.BarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import wendu.dsbridge.DWebView;

/**
 * 设备单孔页面
 */
public class DeviceDetailH5Activity extends BaseMvpActivity {

    private static final String TAG = DeviceDetailH5Activity.class.getSimpleName();
    @BindView(R.id.web_view)
    DWebView mWebView;

    @BindView(R.id.empty_layout)
    View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebView.loadUrl("http://222.212.97.76:9797/#/demo");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_detail;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {
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
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void back(Object msg) {
                Log.d(TAG, "back: " + msg);
                finish();
            }

            @JavascriptInterface
            public void navigationTo(Object msg) {
                Log.d(TAG, "navigationTo: " + msg);
            }

            @JavascriptInterface
            public void sysTextColor(Object msg) throws JSONException {
                Log.d(TAG, "sysTextColor: " + msg);
                String state = new JSONObject(msg.toString()).getString("state");
                switch (state) {
                    case "heightlight":
                        BarUtils.setStatusBarLightMode(DeviceDetailH5Activity.this, true);
                        break;
                    case "dark":
                        BarUtils.setStatusBarLightMode(DeviceDetailH5Activity.this, false);
                        break;
                }
            }
        }, "miya.h5.webview");
        mWebView.addJavascriptObject(new Object() {
            @JavascriptInterface
            public void ready(Object msg) {
                Log.d(TAG, "ready: " + msg);
                miya_native_dataShare_init();
            }

            @JavascriptInterface
            public void token(Object msg) {
                Log.d(TAG, "token: " + msg);
            }
        }, "miya.h5.dataShare");
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @OnClick(R.id.bt_refresh)
    void handleRefreshClick() {
        mWebView.reload();
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }

    private void miya_native_dataShare_init() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", "111");
            jsonObject.put("refreshToken", "2222");
            jsonObject.put("marginTop", "600");
            mWebView.callHandler("miya.native.dataShare.init", new Object[]{jsonObject.toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
