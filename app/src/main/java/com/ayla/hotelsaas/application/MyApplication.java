package com.ayla.hotelsaas.application;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.aliyun.alink.linksdk.alcs.coap.AlcsCoAP;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.List;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

import static com.aliyun.iot.aep.sdk.IoTSmart.REGION_ALL;
import static com.aliyun.iot.aep.sdk.IoTSmart.REGION_CHINA_ONLY;


/**
 * Created by fcl13761179064 on 2020/6/3.
 */
public class MyApplication extends AApplication {

    private List<DeviceListBean.DevicesBean> mDevicesBean;
    private static MyApplication mInstance = null;

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    public static Resources getResource() {
        return mInstance.getResources();
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, android.R.color.darker_gray);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initAutoSize();
        //初始化飞燕sdk
        onInit(this);
    }

    /**
     * 带参数初始化
     *
     * @param app
     */
    private static void onInit(AApplication app) {
        // 默认的初始化参数
        IoTSmart.InitConfig initConfig = new IoTSmart.InitConfig()
                // REGION_ALL: 支持连接中国大陆和海外多个接入点，REGION_CHINA_ONLY:直连中国大陆接入点，只在中国大陆出货选这个
                .setRegionType(REGION_CHINA_ONLY)
                // 对应控制台上的测试版（PRODUCT_ENV_DEV）和正式版（PRODUCT_ENV_PROD）(默认)
                .setProductEnv(IoTSmart.PRODUCT_ENV_PROD)
                // 是否打开日志
                .setDebug(true);

        // 定制三方通道离线推送，目前支持华为、小米和FCM
        IoTSmart.PushConfig pushConfig = new IoTSmart.PushConfig();
        pushConfig.fcmApplicationId = "fcmid"; // 替换为从FCM平台申请的id
        pushConfig.fcmSendId = "fcmsendid"; // 替换为从FCM平台申请的sendid
        pushConfig.xiaomiAppId = "XiaoMiAppId"; // 替换为从小米平台申请的AppID
        pushConfig.xiaomiAppkey = "XiaoMiAppKey"; // 替换为从小米平台申请的AppKey
        // 华为推送通道需要在AndroidManifest.xml里面添加从华为评审申请的appId
        initConfig.setPushConfig(pushConfig);


        GlobalConfig.getInstance().setApiEnv(GlobalConfig.API_ENV_ONLINE);
        GlobalConfig.getInstance().setBoneEnv(GlobalConfig.BONE_ENV_RELEASE);

        IoTSmart.Country country = new IoTSmart.Country();
        country.areaName = "China";
        country.code = "86";
        country.domainAbbreviation = "CN";
        country.isoCode = "CEN";
        country.pinyin = "ZhongGuoDaLu";
        IoTSmart.setCountry(country, new IoTSmart.ICountrySetCallBack() {
            @Override
            public void onCountrySet(boolean b) {
                Log.d("is_china", b + "");

            }
        });
        // 初始化
        IoTSmart.init(app, initConfig);
        new AlcsCoAP().setLogLevel(ALog.LEVEL_ERROR);

    }

    public List<DeviceListBean.DevicesBean> getDevicesBean() {
        return mDevicesBean;
    }

    public void setDevicesBean(List<DeviceListBean.DevicesBean> devicesBean) {
        mDevicesBean = devicesBean;
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance()
                .setBaseOnWidth(true)
                .getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
    }
}
