package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.GlideApp;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.AylaWifiAddPresenter;
import com.ayla.hotelsaas.mvp.present.ZigBeeAddPresenter;
import com.ayla.hotelsaas.mvp.view.AylaWifiAddView;
import com.ayla.hotelsaas.mvp.view.ZigBeeAddView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ZigBee添加页面
 * 进入时必须带入网关deviceId 、cuId 、scopeId、deviceName、deviceCategory
 */
public class AylaWifiAddActivity extends BaseMvpActivity<AylaWifiAddView, AylaWifiAddPresenter> implements AylaWifiAddView {
    private static final String TAG = "AylaWifiAddActivity";
    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_progress)
    public TextView mProgressTextView;
    @BindView(R.id.ll_progress)
    public View mProgressView;
    @BindView(R.id.ll_success_name_input)
    public View ll_success_name_input;
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
    protected AylaWifiAddPresenter initPresenter() {
        return new AylaWifiAddPresenter();
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
        progressStart();
    }

    private int bindProgress;//记录进度

    private void progressStart() {
        mPresenter.bindZigBeeNodeWithGatewayDSN(
                getIntent().getStringExtra("wifiName"),
                getIntent().getStringExtra("wifiPassword"),
                getIntent().getIntExtra("cuId", 0),
                getIntent().getLongExtra("scopeId", 0),
                getIntent().getStringExtra("deviceCategory"),
                getIntent().getStringExtra("deviceName"));
    }

    @Override
    public void startAirkiss() {
        bindProgress = 0;
        refreshBindShow();
    }

    @Override
    public void airkissSuccess() {
        bindProgress = 1;
        refreshBindShow();
    }

    @Override
    public void startBind() {
        bindProgress = 2;
        refreshBindShow();
    }

    @Override
    public void bindSuccess() {
        bindProgress = 3;
        refreshBindShow();
    }

    @Override
    public void progressSuccess() {
        setResult(RESULT_OK);
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

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindProgress == 6) {
            finish();
        } else {
            progressStart();
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
                ll_success_name_input.setVisibility(View.INVISIBLE);
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
                ll_success_name_input.setVisibility(View.VISIBLE);
                break;
        }
    }
}
