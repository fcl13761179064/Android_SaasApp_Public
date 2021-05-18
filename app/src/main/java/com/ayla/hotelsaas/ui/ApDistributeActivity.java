package com.ayla.hotelsaas.ui;

import android.content.Intent;
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
import com.ayla.hotelsaas.databinding.ActivityApWifiPasswordInputBinding;
import com.ayla.hotelsaas.databinding.ActivitySwitchDefaultSettingBinding;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ApDistributeActivity extends BasicActivity {

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
    private boolean isHidden=true;
    private boolean permissionHasAsked;//标记是否已经提示授权位置信息

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
//                                                  CustomToast.makeText(getApplicationContext(), "你拒绝了访问位置信息的授权，无法自动填充WiFi名称", R.drawable.ic_warning);
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

    @Override
    protected void initView() {
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
       /* awi_tv_help.setOnClickListener {
            CommonDialog(requireContext()).apply {
                setTitleText("找不到要连接的路由器？")
                setMessageText("1.请确保当前路由器已设置密码。部分设备不支持连接到到未加密的路由器。\n\n2.请确保当前路由器不需要二次身份与密码验证。\n\n3.部分设备暂不支持5GHz网络连接。请查看设备说明书，确定当前设备支持的网络连接类型。\n\n4.请确保路由器未设置隐藏ssid。已设置隐藏ssid的路由器无法在列表中显示，请修改路由器设置后重新尝试连接。")
                setMessageTextSize(12f)
                setMessageGravity(Gravity.START)
                hiddenCancelView()
                setConfirmText("关闭")
                setOnConfirmClickListeners(View.OnClickListener { dismiss() })
            }.show()*/

    }

    @OnClick(R.id.btn_next)
    public void handleJump() {
        String name = mWiFiNameEditText.getText().toString();
        String pwd = mWiFiPasswordEditText.getText().toString();
        if (name.length() == 0) {
            CustomToast.makeText(this, "请输入WIFI名称", R.drawable.ic_toast_warming);
        } else if (pwd.length() == 0) {
            CustomToast.makeText(this, "请输入WiFi密码", R.drawable.ic_toast_warming);
        } else if (pwd.length() < 8) {
            CustomToast.makeText(this, "WIFI密码不小于8位", R.drawable.ic_toast_warming);
        } else {
            saveWifiPwd(name, pwd);
            setResult(RESULT_OK, new Intent().putExtra("wifiName", name).putExtra("wifiPassword", pwd));
            finish();
        }
        KeyboardUtils.hideSoftInput(btn_next);
    }


    private void configWiFi() {
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
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
                    mWiFiPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
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
