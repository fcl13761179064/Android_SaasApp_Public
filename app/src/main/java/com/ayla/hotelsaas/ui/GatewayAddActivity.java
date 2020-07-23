package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.GatewayAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.GatewayAddGuideView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 网关添加页面
 * 进入时必须带上dsn、cuId 、scopeId。
 */
public class GatewayAddActivity extends BaseMvpActivity<GatewayAddGuideView, GatewayAddGuidePresenter> implements GatewayAddGuideView {
    @BindView(R.id.iv_01)
    public LottieAnimationView mImageView;
    @BindView(R.id.tv_loading)
    public TextView mLoadingTextView;
    @BindView(R.id.ll_bind_failed)
    public View mBindFailedView;
    @BindView(R.id.tv_bind_loading)
    public View mBindLoadingView;
    @BindView(R.id.tv_bind_success)
    public View mBindSuccessView;
    @BindView(R.id.bt_bind)
    public Button mFinishButton;


    @Override
    protected GatewayAddGuidePresenter initPresenter() {
        return new GatewayAddGuidePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gateway_add;
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
        mPresenter.registerDeviceWithDSN(
                getIntent().getStringExtra("dsn"),
                getIntent().getIntExtra("cuId", 0),
                getIntent().getIntExtra("scopeId", 0));
    }

    @Override
    public void bindSuccess() {
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
            setResult(RESULT_OK);
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
                mBindLoadingView.setVisibility(View.INVISIBLE);
                mBindSuccessView.setVisibility(View.VISIBLE);
                mBindFailedView.setVisibility(View.INVISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("完成");
                break;
            case -1:
                mImageView.setImageResource(R.drawable.ic_device_bind_failed);
                mLoadingTextView.setVisibility(View.INVISIBLE);
                mBindLoadingView.setVisibility(View.INVISIBLE);
                mBindSuccessView.setVisibility(View.INVISIBLE);
                mBindFailedView.setVisibility(View.VISIBLE);
                mFinishButton.setVisibility(View.VISIBLE);
                mFinishButton.setText("重试");
                break;
            default:
                mImageView.setAnimation(R.raw.ic_device_bind_loading);
                mImageView.playAnimation();
                mLoadingTextView.setVisibility(View.VISIBLE);
                mBindLoadingView.setVisibility(View.VISIBLE);
                mBindSuccessView.setVisibility(View.INVISIBLE);
                mBindFailedView.setVisibility(View.INVISIBLE);
                mFinishButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

}
