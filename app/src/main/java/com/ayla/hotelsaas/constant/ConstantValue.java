package com.ayla.hotelsaas.constant;

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
public class ConstantValue {
    public static String TTS_LEFT_VALUE = "user_tts";
    public static String SP_SAAS = "saas";
    public static String AP_NET_SELECT = "net";
    public static String HotelId;
    public static String Group_H5_Url = "/#/group/lights/lightingEquipment";
    public static String sProdUrl = "https://abp-prod.ayla.com.cn";//正式环境
    public static String sQaUrl = "https://abp-test.ayla.com.cn";//测试环境
    public static String sDevUrl = "http://106.14.31.32";//DEV环境
    //登录保存key
    public static String SP_Login_Token = "login_token";

    //refresh token
    public static String SP_Refresh_Token = "refresh_token";

    public static String SP_Login_account = "save_account";

    //登录保存key
    public static String SP_ROOM_ID = "room_id";

    //场景物模板属性code
    public static String SCENE_TEMPLATE_CODE = "KeyValueNotification.KeyValue";
    //艾拉wifi SSId正则
    public static String DEFAULT_SSID_REGEX = "Ayla-([0-9a-zA-Z]+|DevKit)";
    //A2网关PID
    public static String A2_GATEWAY_PID = "ZBGW0-A000002";
    public static String A6_GATEWAY_PID = "ZBGW0-A000003";
    public static final String[] FOUR_SWITCH_PID = {"ZBSW0-A000023", "ZBSW0-A000024", "ZBSW0-A000025", "ZBSW0-A000026"};

    public static final String COMMON_ID = "0";
    public static final String INTELLIGENT_ID = "1";

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
    }


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
                //url = "http://192.168.1.10:8080";
                break;
            case "stage":
                url = "https://miya-h5-canary.ayla.com.cn";//canary环境;
                break;
            default:
                url = "https://miya-h5.ayla.com.cn";
        }
        return url;
    }

    public static String getGroupControlBaseUrl() {
        String url;
        switch (BuildConfig.server_domain) {
            case "qa":
            case "dev":
                url = "https://miyang-h5-test.ayla.com.cn";
                break;
            case "stage":
                url = "https://miyang-h5-canary.ayla.com.cn";//canary环境;
                break;
            default:
                url = "https://miyang-h5.ayla.com.cn";
        }
        return url;
    }

    public static boolean isNetworkDebug() {
        return networkDebug;
    }


}
