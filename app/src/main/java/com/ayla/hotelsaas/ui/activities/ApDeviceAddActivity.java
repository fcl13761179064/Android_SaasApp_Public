package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.ApNetworkPresenter;
import com.ayla.hotelsaas.mvp.view.ApDeviceAddView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.utils.WifiUtil;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设备添加处理页面
 * 进入时必须带入:
 * {@link Bundle addInfo} include:
 * must {@link int networkType} 1、鸿雁-插网线网关配网2、顺舟-插网线网关配网3、艾拉节点 4、鸿雁节点 5、艾拉wifi设备
 * must {@link int cuId}
 * must {@link long scopeId}
 * must {@link String pid}
 * must {@link String deviceCategory}
 * must {@link String productName}
 * {@link String waitBindDeviceId} 待补全设备的deviceId
 * {@link String nickname} 待补全设备的nickname
 * {@link String replaceDeviceId} 需要替换设备的ID
 * <p>
 * addInfo的额外参数：
 * networkType = 1 时，必须传入
 * HYproductKey、HYdeviceName
 * <p>
 * networkType = 2 时，必须传入
 * deviceId 网关deviceId
 * <p>
 * networkType = 3 时，必须传入
 * deviceId
 * <p>
 * networkType = 4 时，必须传入
 * deviceId
 * <p>
 * networkType = 5 时，必须传入
 * wifiName、wifiPassword
 * <p>
 */
public class ApDeviceAddActivity extends BaseMvpActivity<ApDeviceAddView, ApNetworkPresenter> implements ApDeviceAddView {
    private static final String TAG = "DeviceAddActivity";

    private final int REQUEST_CODE_ADD_SUCCESS = 0X10;

    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_progress)
    public TextView mProgressTextView;
    @BindView(R.id.ll_progress)
    public View mProgressView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;
    @BindView(R.id.iv_p1)
    public ImageView mP1View;
    @BindView(R.id.tv_p1)
    public TextView mP1TextView;

    @BindView(R.id.iv_p2)
    public ImageView mP2View;
    @BindView(R.id.tv_p2)
    public TextView mP2TextView;


    @BindView(R.id.iv_p3)
    public ImageView mP3View;
    @BindView(R.id.tv_p3)
    public TextView mP3TextView;
    private int cuId;
    private long scopeId;
    private String deviceCategory;
    private String pid;
    private String productName;
    private String nickname;
    private String waitBindDeviceId;
    private String replaceDeviceId;
    private Bundle addInfo;
    private String ssid;
    private String pwd;
    private String dsn;
    private String deviceSsid;
    private int bindProgress;//记录进度

    private int PHONE_SETTING_SSID = 0X13;

    @Override
    protected ApNetworkPresenter initPresenter() {
        return new ApNetworkPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_add;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_SUCCESS) {
            setResult(RESULT_OK);
            finish();
        } else if (requestCode == PHONE_SETTING_SSID) {
            deviceSsid = WifiUtil.getConnectWifiSsid();
            mPresenter.connectToApDevice(ApDeviceAddActivity.this, dsn, ssid, pwd, deviceSsid, addInfo.getString("deviceId"), cuId);
        }
    }


    private void startBind() {
        addInfo = getIntent().getBundleExtra("addInfo");
        int networkType = addInfo.getInt("networkType");
        cuId = addInfo.getInt("cuId");
        scopeId = addInfo.getLong("scopeId");
        deviceCategory = addInfo.getString("deviceCategory");
        pid = addInfo.getString("pid");
        productName = addInfo.getString("productName");
        nickname = addInfo.getString("nickname");
        waitBindDeviceId = addInfo.getString("waitBindDeviceId");
        replaceDeviceId = addInfo.getString("replaceDeviceId");
        ssid = getIntent().getStringExtra("ssid");
        pwd = getIntent().getStringExtra("pwd");
        dsn = getIntent().getStringExtra("deviceId");
        deviceSsid = getIntent().getStringExtra("deviceSsid");
        mPresenter.connectToApDevice(ApDeviceAddActivity.this, dsn, ssid, pwd, deviceSsid, addInfo.getString("deviceId"), cuId);
    }

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindProgress == -1) {
//            if (FastClickUtils.isDoubleClick()) {
//                return;
//            }
//            Intent intent = new Intent();
//            intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
//            startActivityForResult(intent, PHONE_SETTING_SSID);
            setResult(RESULT_OK);
            finish();
        }
    }


    /**
     * 根据bindTag刷新UI
     */
    private void refreshBindShow() {
        switch (bindProgress) {
            case 0:
                Glide.with(mImageView).load(R.drawable.ic_device_bind_loading).into(mImageView);
                mLoadingTextView.setVisibility(View.VISIBLE);
                mProgressTextView.setText("最长可能需要1分钟，请耐心等待");
                mProgressView.setVisibility(View.VISIBLE);
                mP1View.setImageResource(R.drawable.ic_progress_dot_loading);
                mP1TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                mP2View.setImageResource(R.drawable.ic_progress_dot_ready);
                mP2TextView.setTextColor(ContextCompat.getColor(this, R.color.color_999999));
                mP3View.setImageResource(R.drawable.ic_progress_dot_ready);
                mP3TextView.setTextColor(ContextCompat.getColor(this, R.color.color_999999));
                mFinishButton.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mP1View.setImageResource(R.drawable.ic_progress_dot_finish);
                mP1TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                break;
            case 2:
                mP2View.setImageResource(R.drawable.ic_progress_dot_loading);
                mP2TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                break;
            case 3:
                mP2View.setImageResource(R.drawable.ic_progress_dot_finish);
                mP2TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                break;
            case 4:
                mP3View.setImageResource(R.drawable.ic_progress_dot_loading);
                mP3TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                break;
            case 5:
                mP3View.setImageResource(R.drawable.ic_progress_dot_finish);
                mP3TextView.setTextColor(ContextCompat.getColor(this, R.color.color_333333));
                break;
            case -1:
                mImageView.setImageResource(R.drawable.ic_device_bind_failed);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mProgressTextView.setText(TextUtils.isEmpty(errorMsg) ? "设备绑定失败\n请再检查设备状态后重试" : errorMsg);
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;
        }
    }

    @Override
    public void confireApStatus(Boolean b, String randomNum, String dsn) {
        mPresenter.bindAylaNode(
                dsn,
                cuId,
                scopeId,
                deviceCategory,
                pid,
                productName,
                nickname,
                waitBindDeviceId,
                replaceDeviceId,
                randomNum);
    }


    private String errorMsg;

    @Override
    public void bindFailed(Throwable throwable) {
        CustomToast.makeText(this, throwable.getMessage(), R.drawable.ic_toast_warning);
        Log.d(TAG, "zigBeeDeviceBindFailed: " + throwable);
        switch (bindProgress) {
            case 0:
                mP1View.setImageResource(R.drawable.ic_progress_dot_error);
                mP1TextView.setTextColor(ContextCompat.getColor(this, R.color.color_bind_logding_tips_failed));
                break;
            case 1:
            case 2:
                mP2View.setImageResource(R.drawable.ic_progress_dot_error);
                mP2TextView.setTextColor(ContextCompat.getColor(this, R.color.color_bind_logding_tips_failed));
                break;
            default:
                mP3View.setImageResource(R.drawable.ic_progress_dot_error);
                mP3TextView.setTextColor(ContextCompat.getColor(this, R.color.color_bind_logding_tips_failed));
                break;
        }
        errorMsg = TempUtils.getLocalErrorMsg("设备绑定失败\n请再检查设备状态后重试", throwable);
        bindProgress = -1;
        refreshBindShow();
    }

    @Override
    public void bindSuccess(DeviceListBean.DevicesBean devicesBean) {
        String ap_choose = SharePreferenceUtils.getString(this, ConstantValue.AP_NET_SELECT, null);
        startActivityForResult(new Intent(this, DeviceAddSuccessActivity.class)
                        .putExtra("device", devicesBean).putExtra("is_ap_normal", ap_choose),
                REQUEST_CODE_ADD_SUCCESS);
    }


    @Override
    public void step3Finish() {
        Log.d(TAG, "bindZigBeeDeviceSuccess: ");
        bindProgress = 5;
        refreshBindShow();
    }

    @Override
    public void step3Start() {
        Log.d(TAG, "bindZigBeeDeviceStart: ");
        bindProgress = 4;
        refreshBindShow();
    }

    @Override
    public void step2Finish() {
        Log.d(TAG, "fetchCandidatesSuccess: ");
        bindProgress = 3;
        refreshBindShow();
    }

    @Override
    public void step2Start() {
        Log.d(TAG, "fetchCandidatesStart: ");
        bindProgress = 2;
        refreshBindShow();
    }

    @Override
    public void step1Finish() {
        Log.d(TAG, "gatewayConnectSuccess: ");
        bindProgress = 1;
        refreshBindShow();
    }

    @Override
    public void step1Start() {
        Log.d(TAG, "gatewayConnectStart: " + Thread.currentThread().getName());
        bindProgress = 0;
        refreshBindShow();
    }


    @Override
    public void onFailed(Throwable throwable) {
        CustomToast.makeText(this, throwable.getMessage(), R.drawable.ic_toast_warning);
    }

}
