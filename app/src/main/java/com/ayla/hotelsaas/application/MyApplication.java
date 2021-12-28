package com.ayla.hotelsaas.application;


import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.NonNull;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.aliyun.iot.aep.sdk.framework.config.GlobalConfig;
import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.common.Constance;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fcl13761179064 on 2020/6/3.
 */
public class MyApplication extends AApplication {

    private final String TAG = this.getClass().getSimpleName();
    private List<DeviceListBean.DevicesBean> mDevicesBean = new ArrayList<>();
    private List<BaseSceneBean> mOneKeyDate = new ArrayList();

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

    static {
        com.scwang.smart.refresh.layout.SmartRefreshLayout.setDefaultRefreshHeaderCreator(new com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public com.scwang.smart.refresh.layout.api.RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull com.scwang.smart.refresh.layout.api.RefreshLayout layout) {
                return new com.scwang.smart.refresh.header.ClassicsHeader(context);
            }
        });
        com.scwang.smart.refresh.layout.SmartRefreshLayout.setDefaultRefreshFooterCreator(new com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public com.scwang.smart.refresh.layout.api.RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull com.scwang.smart.refresh.layout.api.RefreshLayout layout) {
                return new com.scwang.smart.refresh.footer.ClassicsFooter(context);
            }
        });
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "version name: " + BuildConfig.VERSION_NAME + ", version code: " + BuildConfig.VERSION_CODE);
        mInstance = this;
        if (ProcessUtils.isMainProcess()) {
          //  initBugly();
            initX5();
            String title_type = SharePreferenceUtils.getString(this, Constance.SP_SAAS, "1");
            Log.d(TAG, "onResume::" +title_type);
            if (Constance.isNetworkDebug()) {//这个判断是dev，qa环境
                if ("1".equalsIgnoreCase(title_type)) {
                    IoTSmart.setAuthCode("dev_saas");
                } else {
                    IoTSmart.setAuthCode("dev_miya");
                }
            } else {//这个是prod环境
                if ("1".equalsIgnoreCase(title_type)) {
                    IoTSmart.setAuthCode("prod_saas");
                } else {
                    IoTSmart.setAuthCode("prod_miya");
                }
            }
            IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
            Log.d(TAG, "onResume: GlobalConfig.getInstance().getAuthCodesss():" + GlobalConfig.getInstance().getAuthCode());
            Log.d(TAG, "onResume: GlobalConfig.getInstance().getAuthCode():" + GlobalConfig.getInstance().getAuthCode());
            Log.d(TAG, "onResume: netDebug:" + Constance.isNetworkDebug());
        }
    }

   /* private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(
                    int crashType,
                    String errorType,
                    String errorMessage,
                    String errorStack) {

                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                String x5CrashInfo = com.tencent.smtt.sdk.WebView.getCrashExtraMessage(getApplicationContext());
                map.put("x5crashInfo", x5CrashInfo);
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(
                    int crashType,
                    String errorType,
                    String errorMessage,
                    String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }
        });

        CrashReport.initCrashReport(getApplicationContext(), "8863fabcca", Constance.isNetworkDebug());
    }*/

    private void initX5() {
        // 在调用TBS初始化、创建WebView之前进行如下配置
        QbSdk.setDownloadWithoutWifi(true);
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, "onCoreInitFinished: ");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d(TAG, "onViewInitFinished: " + b);
            }
        });
    }

    /**
     * 避免列表被破坏，需要重新组装一个list返回出去。
     *
     * @return
     */
    public List<DeviceListBean.DevicesBean> getDevicesBean() {
        return new ArrayList<>(mDevicesBean);
    }

    public DeviceListBean.DevicesBean getDevicesBean(String deviceId) {
        for (DeviceListBean.DevicesBean devicesBean : mDevicesBean) {
            if (deviceId != null && deviceId.contains("@")) {
                String pointName = deviceId.split("@")[0];
                if (!TextUtils.isEmpty(pointName)) {
                    DeviceListBean.DevicesBean bean = getDevicesBeanByPointName(pointName);
                    if (bean != null) {
                        return bean;
                    }
                }
            }
            if (devicesBean.getDeviceId().equals(deviceId)) {
                return devicesBean;
            }
        }
        return null;
    }

    private DeviceListBean.DevicesBean getDevicesBeanByPointName(String pointName) {
        for (DeviceListBean.DevicesBean devicesBean : mDevicesBean) {
            if (devicesBean.getPointName().equals(pointName)) {
                return devicesBean;
            }
        }
        return null;
    }

    public void setDevicesBean(List<DeviceListBean.DevicesBean> devicesBean) {
        mDevicesBean = devicesBean;
    }

    /**
     * 避免列表被破坏，需要重新组装一个list返回出去。
     *
     * @return
     * @param
     */
    public List<BaseSceneBean> getmOneKeyRelueBean() {
        return new ArrayList(mOneKeyDate);
    }
   /**
     * 避免列表被破坏，需要重新组装一个list返回出去。
     *
     * @return
    * @param
    * @param targetDeviceId
    */
    public String getmOneKeyRelueName(String targetDeviceId) {
        for (BaseSceneBean s :mOneKeyDate) {
            if (String.valueOf(s.getRuleId()).equals(targetDeviceId)){
                String ruleName = s.getRuleName();
                return ruleName;
            }

        }
        return null;
    }


    public void SaveOneKeyRuler(List<BaseSceneBean> data) {
        mOneKeyDate=data;
    }

}
