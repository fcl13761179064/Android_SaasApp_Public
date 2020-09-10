/*
 * create by cairurui on 1/3/19 10:47 AM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.ayla.hotelsaas.webview.bridge.LarkBridgeConsts;
import com.ayla.hotelsaas.webview.bridge.param.LarkBridgeParam;
import com.ayla.hotelsaas.webview.bridge.param.LarkBridgeResponseOutputParam;
import com.ayla.hotelsaas.webview.bridge.param.LarkJavaScriptMode;
import com.ayla.hotelsaas.webview.notify.LarkWebViewNotificationManager;
import com.blankj.utilcode.util.AppUtils;


import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;


public class LarkWebView extends WebView {
    public static final String BROADCAST_PERMISSION_DISC = AppUtils.getAppPackageName() + ".larksdkcommon.permission.sdk_inner";
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
                LoadCallBack loadCallBack = loadCallBackWeakReference.get();
                if (null != loadCallBack) {
                    loadCallBack.onPageFinished(view, url);
                }
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (null != loadCallBackWeakReference) {
                LoadCallBack loadCallBack = loadCallBackWeakReference.get();
                if (null != loadCallBack) {
                    loadCallBack.onPageStarted(view, url, favicon);
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (null != loadCallBackWeakReference) {
                LoadCallBack loadCallBack = loadCallBackWeakReference.get();
                if (null != loadCallBack) {
                    loadCallBack.onPageError(view, errorCode,description,failingUrl);
                }
            }
        }
    };

    private void init() {
        this.setWebViewClient(mWebViewClient);
        this.addJavascriptInterface(this, "larkBridge");
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        WebSettings webSettings = this.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAppCacheEnabled(true);
        String appCacheDir = this.getContext().getDir("cache", Context.MODE_PRIVATE).getAbsolutePath();
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setWebContentsDebuggingEnabled(true);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            callJS(intent.getStringExtra("data"));
        }
    };

    private WeakReference<LoadCallBack> loadCallBackWeakReference;

    @Override
    protected void onAttachedToWindow() {
        Log.d(TAG, "onAttachedToWindow: ");
        super.onAttachedToWindow();
        getContext().registerReceiver(broadcastReceiver, new IntentFilter("LARKCOMMON_CALL_TO_LARKJS"), BROADCAST_PERMISSION_DISC, null);
        LarkWebViewNotificationManager.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG, "onDetachedFromWindow: ");
        getContext().unregisterReceiver(broadcastReceiver);
        LarkWebViewNotificationManager.unRegister(this);
        sendCommand(LarkJavaScriptMode.getInstance("{\"name\":\"local.notify.detachedFromWindow\"}"));
        super.onDetachedFromWindow();
    }

    public synchronized void registerLoadCallBack(LoadCallBack loadCallBack) {
        unregisterLoadCallBack();
        loadCallBackWeakReference = new WeakReference<>(loadCallBack);
    }

    public synchronized void unregisterLoadCallBack() {
        if (null != loadCallBackWeakReference) {
            loadCallBackWeakReference.clear();
        }
        loadCallBackWeakReference = null;
    }

    @JavascriptInterface
    public void callJava(final String json) {
        try {
            Log.d(TAG, this.hashCode() + "->callJava: " + json);
            LarkJavaScriptMode input = LarkJavaScriptMode.getInstance(json);
            switch (input.getName()) {
                case LarkBridgeConsts.REQUEST_NAV_PUSH:
                    handlePushController(input);
                    break;
                case LarkBridgeConsts.REQUEST_NAV_POP:
                    handlePopController(input);
                    break;
                case LarkBridgeConsts.REQUEST_QUERY_LANGUAGE:
                    getCurrentLanguage(input);
                    break;
                case LarkBridgeConsts.REQUEST_QUERY_LARK_CLOUD_TOKEN:
                    getLarkCloudToken(input);
                    break;
                case LarkBridgeConsts.REQUEST_STORAGE_SET:
                    storageSet(input);
                    break;
                case LarkBridgeConsts.REQUEST_STORAGE_GET:
                    storageGet(input);
                    break;
                case LarkBridgeConsts.REQUEST_STORAGE_DELETE:
                    storageDelete(input);
                    break;
                case LarkBridgeConsts.REQUEST_CUS_EVENT:
                    handleCustomEvent(input);
                    break;
                case LarkBridgeConsts.REQUEST_SET_DEVICE_PROPERTY:
                    notifyDevicePropertyChanged(input);
                    sendCommand(input);
                    break;
                case LarkBridgeConsts.REQUEST_SET_DEVICE_PROPERTIES:
                    notifyPropertiesChanged(input);
                    sendCommand(input);
                    break;
                case LarkBridgeConsts.REQUEST_SET_DEVICE_GROUP_PROPERTY:
                    sendCommand(input);
                    break;
                case LarkBridgeConsts.REQUEST_SET_DEVICE_GROUP_PROPERTIES:
                    sendCommand(input);
                    break;
                default:
                    sendCommand(input);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyDevicePropertyChanged(LarkJavaScriptMode larkJavaScriptMode) {
        final String dsn = larkJavaScriptMode.stringValueFromParams("dsn");
        String propertyName = larkJavaScriptMode.stringValueFromParams("propertyName");
        Object propertyValue = larkJavaScriptMode.objectValueFromParams("propertyValue");
        LarkJSController.js2NativeCallback.notifyDevicePropertyChanged(dsn, propertyName, propertyValue);
    }

    private void notifyPropertiesChanged(LarkJavaScriptMode larkJavaScriptMode) {
        ArrayList<Map<String, Object>> properties = (ArrayList<Map<String, Object>>) larkJavaScriptMode.objectValueFromParams("properties");
        for (Map<String, Object> objectMap : properties) {
            String dsn = (String) objectMap.get("dsn");
            String propertyName = (String) objectMap.get("propertyName");
            Object propertyValue = objectMap.get("propertyValue");
            LarkJSController.js2NativeCallback.notifyDevicePropertyChanged(dsn, propertyName, propertyValue);
        }
    }

    private void notifyDeviceGroupSwitchChanged(int groupId, boolean switchState) {
        LarkJSController.js2NativeCallback.notifyDeviceGroupSwitchChanged(groupId, switchState);
    }

    /**
     * 处理 提交请求自定义通知
     *
     * @param params
     */
    private void handleCustomEvent(LarkJavaScriptMode params) {
        String msg = params.stringValueFromParams("msg");
        int code = params.intValueFromParams("code");
        if (code == 0) {
            code = 1000;
        }
        if (code >= 1000 && code <= 1999) {//网页发送给网页
            LarkWebViewNotificationManager.pushCustomEvent2WebView(msg, code);
        } else if (code >= 2000 && code <= 2999) {//网页发送给原生。
            if (code == 2000) {//网页发送给原生，通知设备群组的开关状态。
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String groupId = jsonObject.getString("groupId");
                    int groupSwitch = jsonObject.getInt("groupSwitch");
                    notifyDeviceGroupSwitchChanged(Integer.parseInt(groupId), groupSwitch == 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handlePopController(LarkJavaScriptMode params) {
        String func = params.stringValueFromParams("func");
        switch (func) {
            case LarkBridgeConsts.REQUEST_NAV_POP_PAGE_DEVICE_DETAIL:
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                break;
            case LarkBridgeConsts.REQUEST_NAV_EXIT_PAGE_DEVICE_DETAIL:
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                break;
        }
    }

    private void handlePushController(final LarkJavaScriptMode params) {
        String func = params.stringValueFromParams("func");
        switch (func) {
            case LarkBridgeConsts.REQUEST_NAV_POP_PAGE_CUSTOM_WEBVIEW:
                LarkJSController.js2NativeCallback.pushCustomWebView(params.stringValueFromParams("h5PagePath"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_DEVICE_DETAIL:
                LarkJSController.js2NativeCallback.pushCustomWebView(params.stringValueFromParams("h5PagePath"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_DEVICE_MORE_INFO:
                LarkJSController.js2NativeCallback.pushDeviceMoreInfo(params.stringValueFromParams("dsn"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_DEVICE_GROUP_MORE_INFO:
                LarkJSController.js2NativeCallback.pushDeviceGroupMoreInfo(params.stringValueFromParams("groupId"),
                        params.stringValueFromParams("groupName"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_COMMON_TIMER:
                LarkJSController.js2NativeCallback.pushCommonTimer(params.stringValueFromParams("dsn"), params.stringValueFromParams("actionName"),
                        params.stringValueFromParams("schPredicate"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_CYCLE_TIMER:
                LarkJSController.js2NativeCallback.pushCycleTimer(params.stringValueFromParams("dsn"));
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_NEW_DEVICE:
                LarkJSController.js2NativeCallback.pushAddDevice();
                break;
            case LarkBridgeConsts.REQUEST_NAV_PUSH_PAGE_SMS_ALARM:
                LarkJSController.js2NativeCallback.pushSmsAlarm(params.stringValueFromParams("dsn"),
                        params.stringValueFromParams("displayName"), params.stringValueFromParams("subTitle"),
                        params.stringValueFromParams("propertyName"), params.stringValueFromParams("triggerType"),
                        params.stringValueFromParams("compareType"), params.stringValueFromParams("value"),
                        params.stringValueFromParams("content"));
                break;
        }
    }

    private void getCurrentLanguage(final LarkJavaScriptMode data) {
        String currentLanguage = LarkJSController.js2NativeCallback.getCurrentLanguage();
        LarkBridgeParam result = new LarkBridgeResponseOutputParam(true, data.getJscallback());
        result.appendParamItem("language", currentLanguage);
        callJS(result.toJson());
    }

    private void getLarkCloudToken(final LarkJavaScriptMode data) {
        String cloudToken = LarkJSController.js2NativeCallback.getLarkCloudToken();
        LarkBridgeParam result = new LarkBridgeResponseOutputParam(true, data.getJscallback());
        result.appendParamItem("token", cloudToken);
        callJS(result.toJson());
    }

    private void storageGet(LarkJavaScriptMode data) {
        String key = data.stringValueFromParams("key");
        String value = LarkJSController.js2NativeCallback.storageGet(key);
        LarkBridgeParam result = new LarkBridgeResponseOutputParam(true, data.getJscallback());
        result.appendParamItem("key", key);
        result.appendParamItem("value", value);
        callJS(result.toJson());
    }

    private void storageSet(LarkJavaScriptMode data) {
        LarkJSController.js2NativeCallback.storageSet(data.stringValueFromParams("key"),
                data.stringValueFromParams("value"));
        LarkBridgeParam result = new LarkBridgeResponseOutputParam(true, data.getJscallback());
        callJS(result.toJson());
    }

    private void storageDelete(LarkJavaScriptMode data) {
        LarkJSController.js2NativeCallback.storageDelete(data.stringValueFromParams("key"));
        LarkBridgeParam result = new LarkBridgeResponseOutputParam(true, data.getJscallback());
        callJS(result.toJson());
    }

    /**
     * 将来自larkjs的命令以广播方式发送给larkcommon。接收者为：LarkJavaScriptNotificationManager
     *
     * @param data
     */
    private void sendCommand(LarkJavaScriptMode data) {
        data.appendParamItem("webID", this.hashCode());
        Intent intent = new Intent("LARKJS_CALL_TO_LARKCOMMON");
        intent.putExtra("data", data.toJson());
        getContext().sendBroadcast(intent, BROADCAST_PERMISSION_DISC);
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
}