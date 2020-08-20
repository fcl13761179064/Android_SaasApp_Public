package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddPresenter;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ZigBee添加页面
 * 进入时必须带入网关deviceId 、cuId 、scopeId、deviceName、deviceCategory
 */
public class ZigBeeAddActivity extends BaseMvpActivity<ZigBeeAddView, ZigBeeAddPresenter> implements ZigBeeAddView {
    private static final String TAG = "ZigBeeAddActivity";
    @BindView(R.id.iv_01)
    public LottieAnimationView mImageView;
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
        return R.layout.activity_zigbee_add;
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
        Log.d(TAG, "startBind: " + Thread.currentThread().getName());
        mPresenter.bindZigBeeNodeWithGatewayDSN(
                getIntent().getStringExtra("deviceId"),
                getIntent().getIntExtra("cuId", 0),
                getIntent().getLongExtra("scopeId", 0),
                getIntent().getStringExtra("deviceCategory"),
                getIntent().getStringExtra("deviceName"));
    }

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindProgress == 6) {
            finish();
        } else {
            startBind();
        }
    }

    /**
     * 根据bindTag刷新UI
     */
    private void refreshBindShow() {
        switch (bindProgress) {
            case 0:
                mImageView.setAnimation(R.raw.ic_device_bind_loading);
                mImageView.playAnimation();
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
            case 6:
                mImageView.setImageResource(R.drawable.ic_device_bind_success);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mProgressView.setVisibility(View.INVISIBLE);
                mProgressTextView.setText("设备绑定成功");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("完成");
                break;
        }
    }

    @Override
    public void progressSuccess() {
        setResult(RESULT_OK);
        Log.d(TAG, "zigBeeDeviceBindFinished: ");
        bindProgress = 6;
        refreshBindShow();
    }

    @Override
    public void progressFailed(Throwable throwable) {
        Log.d(TAG, "zigBeeDeviceBindFailed: " + throwable);
        mImageView.setImageResource(R.drawable.ic_device_bind_failed);
        mLoadingTextView.setVisibility(View.INVISIBLE);
        mProgressView.setVisibility(View.VISIBLE);
        mProgressTextView.setText("设备绑定失败，请确认设备状态后重试");
        mFinishButton.setVisibility(View.VISIBLE);
        mFinishButton.setText("重试");

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
    }

    @Override
    public void bindZigBeeDeviceSuccess() {
        Log.d(TAG, "bindZigBeeDeviceSuccess: ");
        bindProgress = 5;
        refreshBindShow();
    }

    @Override
    public void bindZigBeeDeviceStart() {
        Log.d(TAG, "bindZigBeeDeviceStart: ");
        bindProgress = 4;
        refreshBindShow();
    }

    @Override
    public void fetchCandidatesSuccess() {
        Log.d(TAG, "fetchCandidatesSuccess: ");
        bindProgress = 3;
        refreshBindShow();
    }

    @Override
    public void fetchCandidatesStart() {
        Log.d(TAG, "fetchCandidatesStart: ");
        bindProgress = 2;
        refreshBindShow();
    }

    @Override
    public void gatewayConnectSuccess() {
        Log.d(TAG, "gatewayConnectSuccess: ");
        bindProgress = 1;
        refreshBindShow();
    }

    @Override
    public void gatewayConnectStart() {
        Log.d(TAG, "gatewayConnectStart: " + Thread.currentThread().getName());
        bindProgress = 0;
        refreshBindShow();
    }
}
