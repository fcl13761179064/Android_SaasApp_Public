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

    /**
     * 测试环境地址
     */
    public static final String BASE_URL_BATA = "http://divider_bottom_bg_white.gewala.net/openapi2/router/";
    /**
     * 正式环境地址
     */
    public static String BASE_URL = "https://abp-prod.ayla.com.cn/";


    //用户是否登录
    public static boolean UserIsLogin = false;

    //登录保存key
    public static String SP_Login_Token = "login_token";

    //refresh token
    public static String SP_Refresh_Token = "refresh_token";

    //状态
    public static final String V_STATUS_online = "online";    //初始状态
    public static final String V_STATUS_offline = "offline";    //未批准

}
