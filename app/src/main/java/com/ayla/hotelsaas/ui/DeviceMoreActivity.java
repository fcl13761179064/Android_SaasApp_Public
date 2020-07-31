package com.ayla.hotelsaas.ui;

import android.view.View;
import android.widget.RelativeLayout;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.ToastUtil;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;

public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    private String mDeviceId;

    @Override
    public void refreshUI() {
        mDeviceId = getIntent().getStringExtra("deviceId");
        appBar.setCenterText("更多");
        super.refreshUI();
    }


    @Override
    protected DeviceMorePresenter initPresenter() {
        return new DeviceMorePresenter();
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
        rl_device_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deviceRenameMethod(mDeviceId, );
            }
        });

    }

    @Override
    public void operateError(String msg) {
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void operateSuccess(Boolean is_rename) {
        ToastUtils.showShortToast("操作成功");
    }
}
