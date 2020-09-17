/*
 * create by cairurui on 2/25/19 3:15 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview.bridge.param;

/**
 * Lark桥接推送输出数据（App->H5网页）
 */
public class LarkBridgePushOutputParam extends LarkBridgeParam {
    /**
     * 动作名称
     */
    private String name;

    public LarkBridgePushOutputParam(boolean successed, String name) {
        super(successed);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}