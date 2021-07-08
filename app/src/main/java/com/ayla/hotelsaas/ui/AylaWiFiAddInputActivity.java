package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.iot.aep.sdk.page.LocationUtil;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.FilterWifiDialog;
import com.ayla.hotelsaas.widget.WifiUtils;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加DSN输入页面
 * 返回 wifiName，wifiPassword
 */
public class AylaWiFiAddInputActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_START_ADD = 0x10;
    @BindView(R.id.et_wifi)
    public EditText mWiFiNameEditText;
    @BindView(R.id.et_pwd)
    public EditText mWiFiPasswordEditText;
    @BindView(R.id.iv_airkiss_wifi_change)
    public ImageView iv_airkiss_wifi_change;
    private int defIndex = -1;
    private String locationName="-10000";

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ayla_wifi_add_input;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        WifiUtils wifiUtil = WifiUtils.getInstance(this);
        List<ScanResult> scanResultList = wifiUtil.getWifiScanResult();
        for (int x = 0; x < scanResultList.size(); x++) {
            if (TextUtils.equals(scanResultList.get(x).SSID, locationName)) {
                defIndex = x;
                break;
            }
        }
        iv_airkiss_wifi_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterWifiDialog.newInstance()
                        .setSubTitle("Wi_Fi")
                        .setTitle("设备位置")
                        .setData(scanResultList)
                        .setDefaultIndex(defIndex)
                        .setCallback(new FilterWifiDialog.Callback<ScanResult>() {

                            @Override
                            public void onCallback(ScanResult newLocationName) {
                                if (newLocationName != null) {
                                    locationName = newLocationName.SSID;
                                    if (TextUtils.isEmpty(locationName) || locationName.trim().isEmpty()) {
                                        CustomToast.makeText(getBaseContext(), "WiFi名称不能为空", R.drawable.ic_toast_warming);
                                        return;
                                    }
                                }

                                for (int x = 0; x < scanResultList.size(); x++) {
                                    if (TextUtils.equals(scanResultList.get(x).SSID, locationName)) {
                                        defIndex = x;
                                        break;
                                    }
                                }
                                mWiFiNameEditText.setText(locationName);
                            }
                        })
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!NetworkUtils.isWifiConnected()) {
            CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
                @Override
                public void onDone(CustomAlarmDialog dialog) {
                    dialog.dismissAllowingStateLoss();
                    Intent intent = new Intent();
                    intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                    startActivity(intent);
                }

                @Override
                public void onCancel(CustomAlarmDialog dialog) {
                    dialog.dismissAllowingStateLoss();
                }
            }).setTitle("未连接WiFi").setContent("检查到当前手机未连接 Wi-Fi，请进行连接").show(getSupportFragmentManager(), "wifi dialog");
        }
    }

    private boolean permissionHasAsked;//标记是否已经提示授权位置信息

    @Override
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mWiFiNameEditText.getText().toString())) {
            String connectWifiSsid = WifiUtil.getConnectWifiSsid();
            mWiFiNameEditText.setText(connectWifiSsid);
            mWiFiPasswordEditText.setText(getWifiPwd(connectWifiSsid));

            if (NetworkUtils.isWifiConnected() && TextUtils.isEmpty(connectWifiSsid)) {
                if (PermissionUtils.isGranted(PermissionConstants.getPermissions(PermissionConstants.LOCATION))) {
                    if (!LocationUtil.isLocationEnabled(this)) {//位置获取 开关没有打开
                        CustomToast.makeText(this, "打开GPS/位置开关可以自动获取当前连接的WiFi名称", R.drawable.ic_warning);
                    }
                } else {//定位权限没有
                    if (!permissionHasAsked) {
                        CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                PermissionUtils.permission(PermissionConstants.LOCATION)
                                        .callback(new PermissionUtils.FullCallback() {
                                            @Override
                                            public void onGranted(@NonNull List<String> granted) {
                                                String connectWifiSsid = WifiUtil.getConnectWifiSsid();
                                                mWiFiNameEditText.setText(connectWifiSsid);
                                                mWiFiPasswordEditText.setText(getWifiPwd(connectWifiSsid));
                                            }

                                            @Override
                                            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                                if (deniedForever.size() > 0) {
                                                    Intent settingsIntent = IntentUtils.getLaunchAppDetailsSettingsIntent(AppUtils.getAppPackageName());
                                                    startActivity(settingsIntent);
//                                            CustomToast.makeText(getApplicationContext(), "你拒绝了访问位置信息的授权，无法自动填充WiFi名称", R.drawable.ic_warning);
                                                }
                                            }
                                        }).request();
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        }).setTitle("无法自动获取WiFi名称").setContent("授权程序访问位置信息后，将可以自动填入连接的WiFi名").show(getSupportFragmentManager(), "wifi dialog");
                    }
                    permissionHasAsked = true;
                }
            }
        }
    }

    @OnClick(R.id.bt_start_add)
    public void handleJump() {
        String name = mWiFiNameEditText.getText().toString();
        String pwd = mWiFiPasswordEditText.getText().toString();
        if (name.length() == 0) {
            CustomToast.makeText(this, "WiFi名输入不能为空", R.drawable.ic_toast_warming);
        } else {
            saveWifiPwd(name, pwd);

            setResult(RESULT_OK, new Intent().putExtra("wifiName", name).putExtra("wifiPassword", pwd));
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_START_ADD && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * 保存wifi信息
     *
     * @param ssid
     * @param pwd
     */
    private void saveWifiPwd(String ssid, String pwd) {
        SPUtils instance = SPUtils.getInstance("wifi_info");
        instance.put(ssid, pwd, true);//保存wifi密码
    }

    private String getWifiPwd(String ssid) {
        SPUtils instance = SPUtils.getInstance("wifi_info");
        String pwd = "";
        try {
            pwd = instance.getString(ssid);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return pwd;
    }
}
