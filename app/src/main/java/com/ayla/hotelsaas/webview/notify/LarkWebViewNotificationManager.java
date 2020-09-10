/*
 * create by cairurui on 1/19/19 12:52 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview.notify;

import com.ayla.hotelsaas.webview.LarkWebView;
import com.ayla.hotelsaas.webview.bridge.LarkBridgeConsts;
import com.ayla.hotelsaas.webview.bridge.param.LarkBridgePushOutputParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 负责APP层通知LarkJs层
 */
public class LarkWebViewNotificationManager {
    private final String TAG = this.getClass().getSimpleName();

    private static List<LarkWebView> larkWebViews = new ArrayList<>();

    private LarkWebViewNotificationManager() {

    }

    /**
     * 通知H5，语言变化了。
     *
     * @param currentLanguage
     */
    public static void handlePushLanguage(String currentLanguage) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("language", currentLanguage);
        sendPush2Client(LarkBridgeConsts.PUSH_APP_LANGUAGE_CHANGED, map);
    }

    /**
     * 推送自定义通知
     */
    public static void pushCustomEvent2WebView(String msg,int code) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg", msg);
        map.put("code",code);
        sendPush2Client(LarkBridgeConsts.PUSH_CUSTOM_EVENT, map);
    }

    /**
     * 发送推送到H5网页客户端
     *
     * @param jsFunc    js方法名称
     * @param bizParams 业务参数
     */
    protected static void sendPush2Client(String jsFunc, HashMap<String, Object> bizParams) {
        LarkBridgePushOutputParam data = new LarkBridgePushOutputParam(true, jsFunc);
        data.setParams(bizParams);
        for (LarkWebView larkWebView : larkWebViews) {
            larkWebView.callJS(data.toJson());
        }
    }

    public static void register(LarkWebView larkWebView) {
        larkWebViews.add(larkWebView);
    }

    public static void unRegister(LarkWebView larkWebView) {
        larkWebViews.remove(larkWebView);
    }
}
