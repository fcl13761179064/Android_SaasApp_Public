/*
 * create by cairurui on 2/25/19 3:15 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview.bridge.param;

/**
 * Lark桥接请求响应输出数据（App->H5网页）
 */
public class LarkBridgeResponseOutputParam extends LarkBridgeParam {

    /**
     * js回调ID
     */
    private String jscallback;

    public LarkBridgeResponseOutputParam(boolean successed, String jscallback) {
        super(successed);
        this.jscallback = jscallback;
    }

    public String getJscallback() {
        return jscallback;
    }
}