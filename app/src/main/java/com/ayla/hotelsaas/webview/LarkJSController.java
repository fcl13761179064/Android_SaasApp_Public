/*
 * create by cairurui on 1/7/19 3:49 PM.
 * Copyright (c) 2019 SunseaIoT. All rights reserved.
 */

package com.ayla.hotelsaas.webview;

public class LarkJSController {
    public static Js2NativeCallback js2NativeCallback;

    public static void init(Js2NativeCallback js2NativeCallback) {
        LarkJSController.js2NativeCallback = js2NativeCallback;
    }
}
