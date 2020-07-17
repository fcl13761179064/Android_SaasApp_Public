package com.ayla.hotelsaas.application;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public static String BASE_URL = "http://192.168.1.41:8080/";

    //用户是否登录
    public static boolean UserIsLogin = false;
}
