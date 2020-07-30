package com.ayla.hotelsaas.ui;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> {

    @BindView(R.id.appBar)
    AppBar appBar;

    @Override
    public void refreshUI() {
        appBar.setCenterText("更多");
        super.refreshUI();
    }



    @Override
    protected DeviceMorePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.device_more_activity;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }
}
