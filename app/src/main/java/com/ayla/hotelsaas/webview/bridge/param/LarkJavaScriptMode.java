/*
 * create by cairurui on 2/25/19 3:15 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview.bridge.param;

import com.google.gson.Gson;

/**
 * larkjs层传输到APP层的数据实体 (H5 -> APP)
 */
public class LarkJavaScriptMode extends LarkBridgeParam {
    private String name;
    private String jscallback;

    private LarkJavaScriptMode(boolean successed) {
        super(successed);
    }

    public static LarkJavaScriptMode getInstance(String json) {
        return new Gson().fromJson(json, LarkJavaScriptMode.class);
    }

    public String getName() {
        return name;
    }

    public String getJscallback() {
        return jscallback;
    }
}
