package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.GlideApp;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddPresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设备添加处理页面
 * 进入时必须带入:
 * 参数 int networkType 1、鸿雁-插网线网关配网2、顺舟-插网线网关配网3、艾拉节点 4、鸿雁节点 5、艾拉wifi设备
 * <p>
 * cuId 、scopeId、deviceName、deviceCategory
 * <p>
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
 */
public class DeviceAddActivity extends BaseMvpActivity<ZigBeeAddView, ZigBeeAddPresenter> implements ZigBeeAddView {
    private static final String TAG = "DeviceAddActivity";
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

    @Override
    protected ZigBeeAddPresenter initPresenter() {
        return new ZigBeeAddPresenter();
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

    private int bindProgress;//记录进度

    private void startBind() {
        int networkType = getIntent().getIntExtra("networkType", 0);
        int cuId = getIntent().getIntExtra("cuId", 0);
        long scopeId = getIntent().getLongExtra("scopeId", 0);
        String deviceCategory = getIntent().getStringExtra("deviceCategory");
        String deviceName = getIntent().getStringExtra("deviceName");

        if (networkType == 1) {//鸿雁网关
            mPresenter.bindHongYanGateway((AApplication) getApplication(),
                    cuId,
                    scopeId,
                    deviceCategory,
                    deviceName,
                    getIntent().getStringExtra("HYproductKey"),
                    getIntent().getStringExtra("HYdeviceName"));
        } else if (networkType == 2) {//顺舟网关
            mPresenter.bindAylaGateway(
                    getIntent().getStringExtra("deviceId"),
                    cuId,
                    scopeId,
                    deviceCategory,
                    deviceName);
        } else if (networkType == 3) {//艾拉节点
            mPresenter.bindAylaNode(
                    getIntent().getStringExtra("deviceId"),
                    cuId,
                    scopeId,
                    deviceCategory,
                    deviceName);
        } else if (networkType == 4) {//鸿雁节点
            mPresenter.bindHongYanNode(
                    getIntent().getStringExtra("deviceId"),
                    cuId,
                    scopeId,
                    deviceCategory,
                    deviceName);
        } else if (networkType == 5) {//艾拉WiFi
            mPresenter.bindAylaWiFi(
                    getIntent().getStringExtra("wifiName"),
                    getIntent().getStringExtra("wifiPassword"),
                    cuId,
                    scopeId,
                    deviceCategory,
                    deviceName);
        }
    }

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindProgress == -1) {
            startBind();
        }
    }

    /**
     * 根据bindTag刷新UI
     */
    private void refreshBindShow() {
        switch (bindProgress) {
            case 0:
                GlideApp.with(mImageView).load(R.drawable.ic_device_bind_loading).into(mImageView);
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
    public void bindSuccess(String deviceId, String deviceName) {
        startActivity(new Intent(this, DeviceAddSuccessActivity.class).putExtra("deviceId", deviceId).putExtra("deviceName", deviceName));
        setResult(RESULT_OK);
        finish();
    }

    private String errorMsg;

    @Override
    public void bindFailed(String msg) {
        Log.d(TAG, "zigBeeDeviceBindFailed: " + msg);
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
        errorMsg = msg;
        bindProgress = -1;
        refreshBindShow();
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

}