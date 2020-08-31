package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ayla.hotelsaas.BuildConfig;

import java.lang.ref.WeakReference;


public class LarkWebView extends WebView {
    private final String TAG = this.getClass().getSimpleName();

    public LarkWebView(Context context) {
        super(context);
        init();
    }

    public LarkWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LarkWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (null != loadCallBackWeakReference) {
                LoadCallBack loadCallBack = loadCallBackWeakReference;
                if (null != loadCallBack) {
                    loadCallBack.onPageFinished(view, url);
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (null != loadCallBackWeakReference) {
                LoadCallBack loadCallBack = loadCallBackWeakReference;
                if (null != loadCallBack) {
                    loadCallBack.onPageStarted(view, url, favicon);
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (null != loadCallBackWeakReference) {
                LoadCallBack loadCallBack = loadCallBackWeakReference;
                if (null != loadCallBack) {
                    loadCallBack.onPageError(view, errorCode, description, failingUrl);
                }
            }
        }
    };

    private void init() {
        this.setWebViewClient(mWebViewClient);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void callJava(final String json) {
                Log.d(TAG, this.hashCode() + "->callJava: " + json);
                if (null != jsBridgeCallBackWeakReference) {
                    JsBridgeCallBack jsBridgeCallBack = jsBridgeCallBackWeakReference;
                    if (null != jsBridgeCallBack) {
                        jsBridgeCallBack.onJsCall(json);
                    }
                }
            }
        }, "larkBridge");
        WebSettings webSettings = this.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAppCacheEnabled(true);
        String appCacheDir = this.getContext().getDir("cache", Context.MODE_PRIVATE).getAbsolutePath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAllowFileAccess(true);
        webSettings.setCacheMode(BuildConfig.DEBUG ? WebSettings.LOAD_NO_CACHE : WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setWebContentsDebuggingEnabled(true);
    }


    private LoadCallBack loadCallBackWeakReference;
    private JsBridgeCallBack jsBridgeCallBackWeakReference;

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: ");
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        super.onDetachedFromWindow();
    }

    public void registerJsBridgeCallBack(JsBridgeCallBack jsBridgeCallBack) {
        unregisterJsBridgeCallBack();
        jsBridgeCallBackWeakReference = jsBridgeCallBack;
    }

    public void unregisterJsBridgeCallBack() {
        jsBridgeCallBackWeakReference = null;
    }

    public synchronized void registerLoadCallBack(LoadCallBack loadCallBack) {
        unregisterLoadCallBack();
        loadCallBackWeakReference = loadCallBack;
    }

    public synchronized void unregisterLoadCallBack() {
        loadCallBackWeakReference = null;
    }

    public void callJS(final String jsonValue) {
        Log.d(TAG, this.hashCode() + "->callJS: " + jsonValue);
        post(new Runnable() {
            @Override
            public void run() {
                evaluateJavascript(String.format("javascript:larkjs.bridgeManager.callJS(\'%s\')", jsonValue.replace("\\", "\\\\")), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {

                    }
                });
            }
        });
    }

    public interface LoadCallBack {
        /**
         * Notify the host application that a page has started loading. This method
         * is called once for each main frame load so a page with iframes or
         * framesets will call onPageStarted one time for the main frame. This also
         * means that onPageStarted will not be called when the contents of an
         * embedded frame changes, i.e. clicking a link whose target is an iframe,
         * it will also not be called for fragment navigations (navigations to
         * #fragment_id).
         *
         * @param view    The WebView that is initiating the callback.
         * @param url     The url to be loaded.
         * @param favicon The favicon for this page if it already exists in the
         *                database.
         */
        void onPageStarted(WebView view, String url, Bitmap favicon);

        /**
         * Notify the host application that a page has finished loading. This method
         * is called only for main frame. When onPageFinished() is called, the
         * rendering picture may not be updated yet. To get the notification for the
         * new Picture, use {@link PictureListener#onNewPicture}.
         *
         * @param view The WebView that is initiating the callback.
         * @param url  The url of the page.
         */
        void onPageFinished(WebView view, String url);

        /**
         * This interface needs to be called back when a page sends an error, such as when the network is disconnected
         */

        void onPageError(WebView view, int errorCode, String description, String failingUrl);

    }

    public interface JsBridgeCallBack {
        void onJsCall(String msg);
    }
}