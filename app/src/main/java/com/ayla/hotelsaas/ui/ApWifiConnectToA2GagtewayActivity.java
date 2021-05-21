package com.ayla.hotelsaas.ui;

import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.APWifiToGateWayPresenter;
import com.ayla.hotelsaas.mvp.view.APwifiToGateWayView;

public class ApWifiConnectToA2GagtewayActivity extends BaseMvpActivity<APwifiToGateWayView, APWifiToGateWayPresenter> implements APwifiToGateWayView {
    @Override
    protected int getLayoutId() {
        return  R.layout.activity_wifi_connectionto_gateway;
    }

    @Override
    protected APWifiToGateWayPresenter initPresenter() {
        return new APWifiToGateWayPresenter();
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {
        mPresenter.changeSceneStatus();

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onFailed(Throwable throwable) {

    }

    @Override
    public void onSuccess() {

    }
}
