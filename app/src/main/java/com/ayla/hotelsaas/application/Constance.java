package com.ayla.hotelsaas.application;

/**
 * @描述 常量类
 * @作者 fanchunlei
 * @时间 2020/7/8
 */
public class Constance {
    /**
     * 是否是网络测试环境
     */
    public static boolean isNetworkDebug = false;

    public static String sProdUrl = "https://abp-prod.ayla.com.cn/";//正式环境
    public static String sQaUrl = "https://abp.ayla.com.cn/";//测试换行

    public static String getBaseUrl() {
        return isNetworkDebug ? sQaUrl : sProdUrl;
    }

    //登录保存key
    public static String SP_Login_Token = "login_token";

    //refresh token
    public static String SP_Refresh_Token = "refresh_token";

    //状态
    public static final String V_STATUS_online = "online";    //初始状态
    public static final String V_STATUS_offline = "offline";    //未批准

}
