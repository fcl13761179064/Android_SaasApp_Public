package com.ayla.hotelsaas.application;

import com.ayla.hotelsaas.BuildConfig;

/**
 * @描述 常量类
 * @作者 fanchunlei
 * @时间 2020/7/8
 */
public class Constance {
    /**
     * 是否处于开发状态。
     */
    private static boolean networkDebug;

    static {
        switch (BuildConfig.server_domain) {
            case "qa":
            case "dev":
                networkDebug = true;
                break;
            default:
                networkDebug = false;
        }
        networkDebug = networkDebug || BuildConfig.DEBUG;
    }

    public static String sProdUrl = "https://abp-prod.ayla.com.cn";//正式环境
    public static String sQaUrl = "https://abp-test.ayla.com.cn";//测试环境
    public static String sDevUrl = "http://106.15.231.103";//DEV环境

    public static String getBaseUrl() {
        String url;
        switch (BuildConfig.server_domain) {
            case "qa":
                url = sQaUrl;
                break;
            case "dev":
                url = sDevUrl;
                break;
            default:
                url = sProdUrl;
        }
        return url;
    }

    /**
     * H5配网帮助页面URL
     *
     * @return
     */
    public static String getAssistantBaseUrl() {
        String url;
        switch (BuildConfig.server_domain) {
            case "qa":
            case "dev":
                url = "https://smarthotel-h5-test.ayla.com.cn";
                break;
            default:
                url = "https://smarthotel-h5.ayla.com.cn";
        }
        return url;
    }

    /**
     * 设备单控页面URL
     *
     * @return
     */
    public static String getDeviceControlBaseUrl() {
        String url;
        switch (BuildConfig.server_domain) {
            case "qa":
            case "dev":
                url = "https://miya-h5-test.ayla.com.cn";
                break;
            default:
                url = "https://miya-h5.ayla.com.cn";
        }
        return url;
    }

    public static boolean isNetworkDebug() {
        return networkDebug;
    }

    //登录保存key
    public static String SP_Login_Token = "login_token";

    //refresh token
    public static String SP_Refresh_Token = "refresh_token";

}
