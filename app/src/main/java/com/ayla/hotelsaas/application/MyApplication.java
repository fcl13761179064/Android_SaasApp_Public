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
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
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
    private static DeviceListBean mDevicesBean;
    private List<DeviceLocationBean> mDevicesLocationBean = new ArrayList<>();
    private List<BaseSceneBean> mOneKeyDate = new ArrayList();
    private List<GroupItem> mAllGroupData = new ArrayList<>();

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
            String title_type = SharePreferenceUtils.getString(this, ConstantValue.SP_SAAS, "1");
            Log.d(TAG, "onResume::" + title_type);
            if (ConstantValue.isNetworkDebug()) {//???????????????dev???qa??????
                if ("1".equalsIgnoreCase(title_type)) {
                    IoTSmart.setAuthCode("dev_saas");
                } else {
                    IoTSmart.setAuthCode("dev_miya");
                }
            } else {//?????????prod??????
                if ("1".equalsIgnoreCase(title_type)) {
                    IoTSmart.setAuthCode("prod_saas");
                } else {
                    IoTSmart.setAuthCode("prod_miya");
                }
            }
            IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(ConstantValue.isNetworkDebug()));
            Log.d(TAG, "onResume: GlobalConfig.getInstance().getAuthCodesss():" + GlobalConfig.getInstance().getAuthCode());
            Log.d(TAG, "onResume: GlobalConfig.getInstance().getAuthCode():" + GlobalConfig.getInstance().getAuthCode());
            Log.d(TAG, "onResume: netDebug:" + ConstantValue.isNetworkDebug());
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
        // ?????????TBS??????????????????WebView????????????????????????
        QbSdk.setDownloadWithoutWifi(true);
        HashMap<String, Object> map = new HashMap<>();
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
     * ????????????????????????????????????????????????list???????????????
     *
     * @return
     */
    public DeviceListBean getDevicesBean() {
        return mDevicesBean;
    }


    /**
     * ????????????????????????????????????????????????list???????????????
     *
     * @return
     */
    public List<DeviceLocationBean> getDevicesLocationBean() {
        return mDevicesLocationBean;
    }

    public DeviceListBean.DevicesBean getDevicesBean(String deviceId) {
        if (mDevicesBean != null) {
            for (DeviceListBean.DevicesBean devicesBean : mDevicesBean.getDevices()) {
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
        }
        return null;
    }

    public Boolean haveA6Gateway() {
        if (mDevicesBean != null) {
            for (DeviceListBean.DevicesBean devicesBean : mDevicesBean.getDevices()) {
                if (TempUtils.isDeviceGateway(devicesBean) && devicesBean.getPid().equalsIgnoreCase("ZBGW0-A000003")) {
                   return true;
                }
            }
        }
        return false;
    }

    public List<DeviceListBean.DevicesBean> a6GatewayData() {
        List<DeviceListBean.DevicesBean> data = new ArrayList<>();
        if (mDevicesBean != null) {
            for (DeviceListBean.DevicesBean devicesBean : mDevicesBean.getDevices()) {
                if (TempUtils.isDeviceGateway(devicesBean) && devicesBean.getPid().equalsIgnoreCase("ZBGW0-A000003")) {
                    data.add(devicesBean);
                }
            }
        }
        return data;
    }

    private DeviceListBean.DevicesBean getDevicesBeanByPointName(String pointName) {
        for (DeviceListBean.DevicesBean devicesBean : mDevicesBean.getDevices()) {
            if (devicesBean.getPointName().equals(pointName)) {
                return devicesBean;
            }
        }
        return null;
    }

    public void setDevicesBean(DeviceListBean devicesBean) {
        mDevicesBean = devicesBean;
    }


    //????????????????????????
    public void setDevicesLocation(List<DeviceLocationBean> devicesLocationBean) {
        mDevicesLocationBean = devicesLocationBean;
    }

    public List<GroupItem> getAllGroupData() {
        return mAllGroupData;
    }

    public void setAllGroupData(List<GroupItem> groupData) {
        mAllGroupData = groupData;
    }

    public GroupItem getGroupItem(String id) {
        for (int i = 0; i < mAllGroupData.size(); i++) {
            if (TextUtils.equals(mAllGroupData.get(i).getGroupId(), id)) {
                return mAllGroupData.get(i);
            }
        }
        return null;
    }

    /**
     * ????????????????????????????????????????????????list???????????????
     *
     * @param
     * @return
     */
    public List<BaseSceneBean> getmOneKeyRelueBean() {
        return new ArrayList(mOneKeyDate);
    }

    /**
     * ????????????????????????????????????????????????list???????????????
     *
     * @param
     * @param targetDeviceId
     * @return
     */
    public String getmOneKeyRelueName(String targetDeviceId) {
        for (BaseSceneBean s : mOneKeyDate) {
            if (String.valueOf(s.getRuleId()).equals(targetDeviceId)) {
                String ruleName = s.getRuleName();
                return ruleName;
            }

        }
        return null;
    }


    public void SaveOneKeyRuler(List<BaseSceneBean> data) {
        mOneKeyDate = data;
    }

}
