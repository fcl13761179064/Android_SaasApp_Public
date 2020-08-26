package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class DeviceDetailActivity extends BasicActivity {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_device_id)
    TextView tv_device_id;
    @BindView(R.id.tv_device_type)
    TextView tv_device_type;

    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    public void refreshUI() {
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        appBar.setCenterText("设备详情");

        super.refreshUI();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.device_detail_activity;
    }

    @Override
    protected void initView() {
        if (mDevicesBean!=null)
        tv_device_id.setText(mDevicesBean.getDeviceId());
        tv_device_type.setText(mDevicesBean.getDeviceName());

    }

    @Override
    protected void initListener() {

    }

}
