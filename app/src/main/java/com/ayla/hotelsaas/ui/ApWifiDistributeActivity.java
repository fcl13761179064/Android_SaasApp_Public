package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aliyun.iot.aep.sdk.page.LocationUtil;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.FilterWifiDialog;
import com.ayla.hotelsaas.widget.WifiUtils;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class ApWifiDistributeActivity extends BasicActivity {

    @BindView(R.id.et_wifi_name)
    public EditText mWiFiNameEditText;
    @BindView(R.id.et_password)
    public EditText mWiFiPasswordEditText;
    @BindView(R.id.btn_next)
    public Button btn_next;
    @BindView(R.id.awi_tv_wifi_more)
    public TextView awi_tv_wifi_more;
    @BindView(R.id.awi_iv_pwd_toggle)
    public ImageView awi_iv_pwd_toggle;
    @BindView(R.id.wifi_help)
    public TextView wifi_help;
    private boolean isHidden = true;
    private boolean permissionHasAsked;//标记是否已经提示授权位置信息
    private int defIndex = -1;
    private String locationName = "-10000";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ap_wifi_password_input;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    private void enableWiFiNameInput(Boolean isEnable) {
        mWiFiNameEditText.setEnabled(isEnable);
    }


    @Override
    protected void initView() {
        try {
            String connectWifiSsid = WifiUtil.getConnectWifiSsid();
            mWiFiNameEditText.setText(connectWifiSsid);
            mWiFiPasswordEditText.setText(getWifiPwd(connectWifiSsid));
            if (NetworkUtils.isWifiConnected() && TextUtils.isEmpty(connectWifiSsid)) {
                if (PermissionUtils.isGranted(PermissionConstants.getPermissions(PermissionConstants.LOCATION))) {
                    if (!LocationUtil.isLocationEnabled(this)) {//位置获取 开关没有打开
                        CustomToast.makeText(this, "打开GPS/位置开关可以自动获取当前连接的WiFi名称", R.drawable.ic_warning);
                        enableWiFiNameInput(true);
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
                                                enableWiFiNameInput(false);
                                            }

                                            @Override
                                            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
                                                if (deniedForever.size() > 0) {
                                                    Intent settingsIntent = IntentUtils.getLaunchAppDetailsSettingsIntent(AppUtils.getAppPackageName());
                                                    startActivity(settingsIntent);
//                                                  CustomToast.makeText(getApplicationContext(), "你拒绝了访问位置信息的授权，无法自动填充WiFi名称", R.drawable.ic_warning);
                                                }
                                            }
                                        }).request();
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        }).setTitle("获取位置权限").setContent("添加网关需要使用位置权限，用以扫描Wi-Fi热点").setEnsureText("设置").show(getSupportFragmentManager(), "wifi dialog");
                    }
                    permissionHasAsked = true;
                }
            } else {
                enableWiFiNameInput(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btn_next)
    public void handleJump() {
        Boolean is_2g = false;
        String name = mWiFiNameEditText.getText().toString();
        String pwd = mWiFiPasswordEditText.getText().toString();
        if (name.length() == 0) {
            CustomToast.makeText(ApWifiDistributeActivity.this, "请输入WIFI名称", R.drawable.ic_toast_warming);
            return;
        }
        WifiUtils wifiUtil = WifiUtils.getInstance(this);
        List<ScanResult> scanResultList = wifiUtil.getWifiScanResult();
        if (scanResultList != null && scanResultList.size() > 0) {
            for (ScanResult s : scanResultList) {
                if (name.equals(s.SSID)) {
                    int frequency = s.frequency;
                    boolean is24G = wifiUtil.is24GHzWifi(frequency);
                    if (is24G) {
                        is_2g = true;
                        break;
                    }
                }
            }
            if (is_2g) {
                jumpTonextActivity(name, pwd);
            } else {

                CustomToast.makeText(ApWifiDistributeActivity.this, "当前WiFi只支持2.4GWiFi", R.drawable.ic_toast_warming);
            }
        } else {
            CustomAlarmDialog
                    .newInstance(new CustomAlarmDialog.Callback() {
                        @Override
                        public void onDone(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();

                        }

                        @Override
                        public void onCancel(CustomAlarmDialog dialog) {
                            dialog.dismissAllowingStateLoss();
                        }
                    })
                    .setTitle(getResources().getString(R.string.wifi_2_4g_notice))
                    .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                    .setContent("没有发现网络")
                    .show(getSupportFragmentManager(), "");
            return;
        }
        KeyboardUtils.hideSoftInput(btn_next);
    }

    public void jumpTonextActivity(String name, String pwd) {
        saveWifiPwd(name, pwd);
        Intent intent = new Intent(ApWifiDistributeActivity.this, ApDistributeGuideActivity.class);
        intent.putExtra("ssid", name);
        intent.putExtra("pwd", pwd);
        intent.putExtras(getIntent());
        startActivity(intent);
    }

    private void configWiFi() {
        WifiUtils wifiUtil = WifiUtils.getInstance(this);
        List<ScanResult> scanResultList = wifiUtil.getWifiScanResult();
        for (int x = 0; x < scanResultList.size(); x++) {
            if (TextUtils.equals(scanResultList.get(x).SSID, locationName)) {
                defIndex = x;
                break;
            }
        }
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

    @Override
    protected void initListener() {
        wifi_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlarmDialog
                        .newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setTitle(getResources().getString(R.string.helper))
                        .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                        .setFontLocation(CustomAlarmDialog.Location.LEFT)
                        .setContent("1.请确保当前路由器已设置密码。部分设备不支持连接到到未加密的路由器。\n" +
                                "\n" +
                                "2.请确保当前路由器不需要二次身份与密码验证。\n" +
                                "\n" +
                                "3.部分设备暂不支持5GHz网络连接。请查看设备说明书，确定当前设备支持的网络连接类型。\n" +
                                "\n" +
                                "4.请确保路由器未设置隐藏SSID。已设置隐藏SSID的路由器无法在列表中显示，请修改路由器设置后重新尝试连接。")
                        .show(getSupportFragmentManager(), "");
            }
        });
        awi_tv_wifi_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configWiFi();
            }
        });
        awi_iv_pwd_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    //设置EditText文本为可见的
                    awi_iv_pwd_toggle.setSelected(true);
                    mWiFiPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    awi_iv_pwd_toggle.setSelected(false);
                    //设置EditText文本为隐藏的
                    mWiFiPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isHidden = !isHidden;
                mWiFiPasswordEditText.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = mWiFiPasswordEditText.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }

            }
        });

    }
}
