package com.ayla.hotelsaas.ui;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CommonDialog;


import butterknife.BindView;

public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;

    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    public void refreshUI() {
        Constance.Is_Auto_ReFresh= -100;
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        appBar.setCenterText("更多");
        if (mDevicesBean != null && !TextUtils.isEmpty(mDevicesBean.getDeviceName())) {
            tv_device_name.setText(mDevicesBean.getNickname());
        }

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
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                CommonDialog.newInstance(new CommonDialog.DoneCallback() {
                    @Override
                    public void onDone(DialogFragment dialog, String txt) {
                        tv_device_name.setText(txt);
                        if (mDevicesBean != null) {
                            mPresenter.deviceRenameMethod(mDevicesBean.getDeviceId(), txt);
                        }
                        SoftInputUtil.showSoftInput(rl_device_rename);
                        dialog.dismissAllowingStateLoss();
                    }
                }, "修改名称").show(getSupportFragmentManager(), "scene_name");
            }
        });

    }

    @Override
    public void operateError(String msg) {
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void operateSuccess(Boolean is_rename) {
        ToastUtils.showShortToast("修改成功");
        Constance.Is_Auto_ReFresh= Activity.RESULT_OK;
    }
}
