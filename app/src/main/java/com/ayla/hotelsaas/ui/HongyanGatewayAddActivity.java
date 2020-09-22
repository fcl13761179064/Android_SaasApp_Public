package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.iot.aep.sdk.framework.AApplication;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.GlideApp;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.HongyanGatewayAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.HongyanGatewayAddGuideView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 鸿雁网关添加页面
 * 进入时必须带上dsn、cuId 、scopeId、deviceCategory。
 * 樊春雷
 */
public class HongyanGatewayAddActivity extends BaseMvpActivity<HongyanGatewayAddGuideView, HongyanGatewayAddGuidePresenter> implements HongyanGatewayAddGuideView {
    @BindView(R.id.iv_01)
    public ImageView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.tv_bind_progress)
    public TextView mBindProgressTextView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;


    @Override
    protected HongyanGatewayAddGuidePresenter initPresenter() {
        return new HongyanGatewayAddGuidePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hongyan_gateway_add;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }


    private int bindTag = 0;//0:绑定中 1:绑定成功 -1:绑定失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBind();
    }

    private void startBind() {
        bindTag = 0;
        refreshBindShow();
        mPresenter.startBind((AApplication) getApplication(),
                getIntent().getIntExtra("cuId", 0),
                getIntent().getLongExtra("scopeId", 0),
                getIntent().getStringExtra("deviceCategory"),
                getIntent().getStringExtra("deviceName"),
                getIntent().getStringExtra("HYproductKey"),
                getIntent().getStringExtra("HYdeviceName"));
    }

    @Override
    public void bindSuccess() {
        setResult(RESULT_OK);
        bindTag = 1;
        refreshBindShow();
    }

    @Override
    public void bindFailed() {
        bindTag = -1;
        refreshBindShow();
    }

    @OnClick(R.id.bt_bind)
    public void handleButton() {
        if (bindTag == 1) {
            finish();
        } else if (bindTag == -1) {
            startBind();
        }
    }

    /**
     * 根据bindTag刷新UI
     */
    private void refreshBindShow() {
        switch (bindTag) {
            case 1:
                mImageView.setImageResource(R.drawable.ic_device_bind_success);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setText("设备绑定成功");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("完成");
                break;
            case -1:
                mImageView.setImageResource(R.drawable.ic_device_bind_failed);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindProgressTextView.setText("设备绑定失败\n请再检查设备状态与设备ID号后重试");
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;
            default:
                GlideApp.with(mImageView).load(R.drawable.ic_device_bind_loading).into(mImageView);
                mLoadingTextView.setVisibility(View.VISIBLE);
                mBindProgressTextView.setText("最长可能需要1分钟，请耐心等待");
                mFinishButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
