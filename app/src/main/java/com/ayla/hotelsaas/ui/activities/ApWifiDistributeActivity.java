package com.ayla.hotelsaas.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;

import com.aliyun.iot.aep.sdk.page.LocationUtil;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.common_dialog.CustomAlarmDialog;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.common_dialog.FilterWifiDialog;
import com.ayla.hotelsaas.utils.WifiUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;

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
    @BindView(R.id.select_wifi)
    public TextView select_wifi;
    private boolean isHidden = true;
    private int defIndex = -1;
    private String locationName = "-10000";
    private List<ScanResult> scanResultList;
    private Boolean is_wifi_more;
    private Boolean isChainRequestPermission;

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
        if (isChainRequestPermission)
            getPermiss();
    }


    @Override
    protected void initView() {
        isChainRequestPermission = true;
        is_wifi_more = false;
    }


    public void getPermiss() {
        try {
            if (!WifiUtils.getInstance(this).mIsopenWifi()) {//??????????????????wifi
                CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        isChainRequestPermission = true;
                        dialog.dismissAllowingStateLoss();
                        Intent intent = new Intent();
                        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }
                }).setTitle("?????????WiFi").setContent("?????????????????????????????? Wi-Fi??????????????????").show(getSupportFragmentManager(), "wifi dialog");
            } else {//?????????wifi????????????????????????????????????????????????????????????????????????
                checkLocationPermission();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            isChainRequestPermission = false;
            requestPermissions(new String[]{(Manifest.permission.ACCESS_FINE_LOCATION)}, 9990);
        } else {
            setWifiData();
        }
    }

    private void setWifiData() {
        WifiUtils wifiUtil = WifiUtils.getInstance(this);
        scanResultList = wifiUtil.getWifiScanResult();
        String connectWifiSsid = WifiUtil.getConnectWifiSsid();
        if (!LocationUtil.isLocationEnabled(this)) {//???????????? ??????????????????
            CustomToast.makeText(this, "??????GPS/?????????????????????????????????????????????WiFi??????", R.drawable.ic_toast_warning);
        } else {
            if (is_wifi_more) {
                try {
                    String name = mWiFiNameEditText.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        for (int x = 0; x < scanResultList.size(); x++) {
                            if (TextUtils.equals(scanResultList.get(x).SSID, "")) {
                                defIndex = x;
                                break;
                            }
                        }
                        FilterWifiDialog.newInstance()
                                .setSubTitle("Wi_Fi")
                                .setTitle("????????????")
                                .setData(scanResultList)
                                .setDefaultIndex(defIndex)
                                .setCallback(new FilterWifiDialog.Callback<ScanResult>() {

                                    @Override
                                    public void onCallback(ScanResult newLocationName) {
                                        if (newLocationName != null) {
                                            locationName = newLocationName.SSID;
                                            if (TextUtils.isEmpty(locationName) || locationName.trim().isEmpty()) {
                                                CustomToast.makeText(getBaseContext(), "WiFi??????????????????", R.drawable.ic_toast_warning);
                                                return;
                                            }
                                        }

                                        for (int x = 0; x < scanResultList.size(); x++) {
                                            if (TextUtils.equals(scanResultList.get(x).SSID, locationName)) {
                                                defIndex = x;
                                                break;
                                            }
                                        }
                                        select_wifi.setVisibility(View.GONE);
                                        mWiFiNameEditText.setText(locationName);
                                        mWiFiPasswordEditText.setText(getWifiPwd(locationName));
                                    }
                                })
                                .show(getSupportFragmentManager(), "dialog");
                    } else {
                        for (int x = 0; x < scanResultList.size(); x++) {
                            if (TextUtils.equals(scanResultList.get(x).SSID, name)) {
                                defIndex = x;
                                break;
                            }
                        }
                        FilterWifiDialog.newInstance()
                                .setSubTitle("Wi_Fi")
                                .setTitle("????????????")
                                .setData(scanResultList)
                                .setDefaultIndex(defIndex)
                                .setCallback(new FilterWifiDialog.Callback<ScanResult>() {

                                    @Override
                                    public void onCallback(ScanResult newLocationName) {
                                        if (newLocationName != null) {
                                            locationName = newLocationName.SSID;
                                            if (TextUtils.isEmpty(locationName) || locationName.trim().isEmpty()) {
                                                CustomToast.makeText(getBaseContext(), "WiFi??????????????????", R.drawable.ic_toast_warning);
                                                return;
                                            }
                                        }

                                        for (int x = 0; x < scanResultList.size(); x++) {
                                            if (TextUtils.equals(scanResultList.get(x).SSID, locationName)) {
                                                defIndex = x;
                                                break;
                                            }
                                        }
                                        select_wifi.setVisibility(View.GONE);
                                        mWiFiNameEditText.setText(locationName);
                                        mWiFiPasswordEditText.setText(getWifiPwd(locationName));
                                    }
                                })
                                .show(getSupportFragmentManager(), "dialog");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (NetworkUtils.isWifiConnected()) {//1.??????
                    select_wifi.setVisibility(View.GONE);
                    mWiFiNameEditText.setText(connectWifiSsid);
                    mWiFiPasswordEditText.setText(getWifiPwd(connectWifiSsid));
                } else {
                    select_wifi.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9990) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setWifiData();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    ) {//??????????????????????????????????????????
                        CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                isChainRequestPermission = true;
                                PermissionUtils.launchAppDetailsSettings();
//                                PermissionUtils.permission(PermissionConstants.LOCATION)
//                                        .callback(new PermissionUtils.FullCallback() {
//                                            @Override
//                                            public void onGranted(@NonNull List<String> granted) {
//                                                String connectWifiSsid = WifiUtil.getConnectWifiSsid();
//                                                mWiFiNameEditText.setText(connectWifiSsid);
//                                                mWiFiPasswordEditText.setText(getWifiPwd(connectWifiSsid));
//                                            }
//
//                                            @Override
//                                            public void onDenied(@NonNull List<String> deniedForever, @NonNull List<String> denied) {
//                                                if (deniedForever.size() > 0) {
//                                                    Intent settingsIntent = IntentUtils.getLaunchAppDetailsSettingsIntent(AppUtils.getAppPackageName());
//                                                    startActivity(settingsIntent);
////                                                  CustomToast.makeText(getApplicationContext(), "????????????????????????????????????????????????????????????WiFi??????", R.drawable.ic_warning);
//                                                }
//                                            }
//                                        }).request()
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        }).setTitle("??????????????????").setContent("???????????????????????????????????????????????????Wi-Fi??????").setEnsureText("??????").show(getSupportFragmentManager(), "wifi dialog");

                    }
                }
            }
        }
    }

    @OnClick(R.id.btn_next)
    public void handleJump() {
//        Boolean is_2g = false;
        String name = mWiFiNameEditText.getText().toString();
        String pwd = mWiFiPasswordEditText.getText().toString();
        if (name.length() == 0) {
            CustomToast.makeText(ApWifiDistributeActivity.this, "?????????WIFI??????", R.drawable.ic_toast_warning);
            return;
        }
        WifiUtils wifiUtil = WifiUtils.getInstance(this);
        List<ScanResult> scanResultList = wifiUtil.getWifiScanResult();
        if (scanResultList != null && scanResultList.size() > 0) {
//            for (ScanResult s : scanResultList) {
//                if (name.equals(s.SSID)) {
//                    int frequency = s.frequency;
//                    boolean is24G = wifiUtil.is24GHzWifi(frequency);
//                    if (is24G) {
//                        is_2g = true;
//                        break;
//                    }
//                }
//            }
//            if (is_2g) {
//                jumpTonextActivity(name, pwd);
//            } else {
//
//                CustomToast.makeText(ApWifiDistributeActivity.this, "??????WiFi?????????2.4GWiFi", R.drawable.ic_toast_warning);
//            }
            jumpTonextActivity(name, pwd);
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
                    .setContent("??????????????????")
                    .show(getSupportFragmentManager(), "");
            return;
        }
        KeyboardUtils.hideSoftInput(btn_next);
    }

    public void jumpTonextActivity(String name, String pwd) {
        is_wifi_more = false;
        saveWifiPwd(name, pwd);
        boolean is_coat_hanger = getIntent().getBooleanExtra("is_coat_hanger", false);
        Intent intent;
        if (is_coat_hanger) {
            intent = new Intent(ApWifiDistributeActivity.this, ApWifiConnectToA2GagtewayActivity.class);
        } else {
            intent = new Intent(ApWifiDistributeActivity.this, ApDistributeGuideActivity.class);
        }
        intent.putExtra("ssid", name);
        intent.putExtra("pwd", pwd);
        intent.putExtras(getIntent());
        startActivity(intent);
    }


    /**
     * ??????wifi??????
     *
     * @param ssid
     * @param pwd
     */
    private void saveWifiPwd(String ssid, String pwd) {
        SPUtils instance = SPUtils.getInstance("wifi_info");
        instance.put(ssid, pwd, true);//??????wifi??????
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
                        .setContent("1.???????????????????????????????????????????????????????????????????????????????????????????????????\n" +
                                "\n" +
                                "2.???????????????????????????????????????????????????????????????\n" +
                                "\n" +
                                "3.????????????????????????5GHz??????????????????????????????????????????????????????????????????????????????????????????\n" +
                                "\n" +
                                "4.?????????????????????????????????SSID??????????????????SSID???????????????????????????????????????????????????????????????????????????????????????")
                        .show(getSupportFragmentManager(), "");
            }
        });
        awi_tv_wifi_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_wifi_more = true;
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                getPermiss();
            }
        });
        awi_iv_pwd_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    //??????EditText??????????????????
                    awi_iv_pwd_toggle.setSelected(true);
                    mWiFiPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    awi_iv_pwd_toggle.setSelected(false);
                    //??????EditText??????????????????
                    mWiFiPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isHidden = !isHidden;
                mWiFiPasswordEditText.postInvalidate();
                //????????????EditText??????????????????
                CharSequence charSequence = mWiFiPasswordEditText.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }

            }
        });

    }
}
