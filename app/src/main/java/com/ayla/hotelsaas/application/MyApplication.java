package com.ayla.hotelsaas.application;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.blankj.utilcode.util.ProcessUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

/**
 * Created by fcl13761179064 on 2020/6/3.
 */
public class MyApplication extends AApplication {

    private final String TAG = this.getClass().getSimpleName();
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
        Log.d(TAG, "version name: " + BuildConfig.VERSION_NAME + ", version code: " + BuildConfig.VERSION_CODE);
        mInstance = this;
        if (ProcessUtils.isMainProcess()) {
            CrashReport.initCrashReport(getApplicationContext(), "8863fabcca", Constance.isNetworkDebug());
            IoTSmart.init(this, new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
        }
    }

    public List<DeviceListBean.DevicesBean> getDevicesBean() {
        return mDevicesBean;
    }

    public DeviceListBean.DevicesBean getDevicesBean(String deviceId) {
        for (DeviceListBean.DevicesBean devicesBean : mDevicesBean) {
            if (devicesBean.getDeviceId().equals(deviceId)) {
                return devicesBean;
            }
        }
        return null;
    }

    ;

    public void setDevicesBean(List<DeviceListBean.DevicesBean> devicesBean) {
        mDevicesBean = devicesBean;
    }

}
