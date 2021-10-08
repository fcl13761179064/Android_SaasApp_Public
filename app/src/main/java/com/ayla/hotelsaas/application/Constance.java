package com.ayla.hotelsaas.application;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.Random;


/**
 * @描述 常量类
 * @作者 fanchunlei
 * @时间 2020/7/8
 */
public class Constance {
    public static String SP_SAAS = "saas";
    public static String AP_NET_SELECT = "net";
    public static String HotelId;
    /**
     * 是否处于开发状态。
     */
    private static boolean networkDebug;

    static {
        switch (BuildConfig.server_domain) {
            case "qa":
            case "dev":
            case "canary":
                networkDebug = true;
                break;
            default:
                networkDebug = false;
        }
//        networkDebug = networkDebug || BuildConfig.DEBUG;
    }

    public static String sProdUrl = "https://abp-prod.ayla.com.cn";//正式环境
    public static String sQaUrl = "https://abp-test.ayla.com.cn";//测试环境
    public static String sDevUrl = "http://106.14.31.32";//DEV环境

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
            case "canary":
                url = "https://miya-h5-canary.ayla.com.cn";//canary环境;
                break;
            default:
                url = "https://miya-h5.ayla.com.cn";
        }
        return url;
    }

    public static boolean isNetworkDebug() {
        return networkDebug;
    }


    public static boolean isOpenLog() {
        if ("debug".equalsIgnoreCase(BuildConfig.BUILD_TYPE)) {
            return true;
        } else {
            return false;
        }
    }

    //登录保存key
    public static String SP_Login_Token = "login_token";

    //refresh token
    public static String SP_Refresh_Token = "refresh_token";

    //sava password account
    public static String SP_Login_password = "save_password";
    public static String SP_Login_account = "save_account";

    //登录保存key
    public static String SP_ROOM_ID = "room_id";

    public static void saveVersionUpgradeInfo(VersionUpgradeBean versionUpgradeBean) {
        if (versionUpgradeBean != null) {
            SPUtils.getInstance().put("versionUpgradeBean", GsonUtils.toJson(versionUpgradeBean), true);
        } else {
            SPUtils.getInstance().remove("versionUpgradeBean", true);
        }
    }

    public static VersionUpgradeBean getVersionUpgradeInfo() {
        String s = SPUtils.getInstance().getString("versionUpgradeBean");
        try {
            VersionUpgradeBean upgradeBean = GsonUtils.fromJson(s, VersionUpgradeBean.class);
            return upgradeBean;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //场景物模板属性code
    public static String SCENE_TEMPLATE_CODE = "KeyValueNotification.KeyValue";
    //艾拉wifi SSId正则
    public static String DEFAULT_SSID_REGEX = "Ayla-([0-9a-zA-Z]+|DevKit)";

    /**
     * 获取随机字符串
     */
    //length用户要求产生字符串的长度
    public static String getRandomString(int length) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static boolean is_double_four_curtain(String deviceCategory) {
        if ("ZB-NODE-WC4-001".equals(deviceCategory) || "ZB-NODE-WC2-001".equals(deviceCategory)) {
            return true;
        } else {
            return false;
        }
    }
}
