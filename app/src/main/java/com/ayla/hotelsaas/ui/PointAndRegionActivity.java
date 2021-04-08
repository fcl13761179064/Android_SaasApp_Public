package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.ActivityPointAndRegionBinding;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.mvp.present.PointAndRegionPresenter;
import com.ayla.hotelsaas.mvp.view.PointAndRegionView;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * 开关用途设置
 * 进入需要带上 deviceId  scopeId
 */
public class PointAndRegionActivity extends BaseMvpActivity<PointAndRegionView, PointAndRegionPresenter> implements PointAndRegionView {
    ActivityPointAndRegionBinding mBinding;

    @Override
    protected PointAndRegionPresenter initPresenter() {
        return new PointAndRegionPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivityPointAndRegionBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    DeviceListBean.DevicesBean devicesBean;

    @Override
    protected void initView() {
        String deviceId = getIntent().getStringExtra("deviceId");
        devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        if (devicesBean != null) {
            mBinding.tvPointnameRight.setText(devicesBean.getPointName());
            mBinding.tvRegionnameRight.setText(devicesBean.getRegionName());
        }
    }

    @Override
    protected void initListener() {
        mBinding.rlPointName.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {


            }
        });
        mBinding.rlRegionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (devicesBean != null) {
                    ValueChangeDialog
                            .newInstance(new ValueChangeDialog.DoneCallback() {
                                @Override
                                public void onDone(DialogFragment dialog, String newName) {
                                    if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                                        CustomToast.makeText(PointAndRegionActivity.this, "设备点位不能为空", R.drawable.ic_toast_warming);
                                        return;
                                    } else {
                                        mPresenter.modifyPointName(devicesBean.getDeviceId(), devicesBean.getNickname(), newName, devicesBean.getRegionId(), devicesBean.getRegionName());
                                    }
                                    dialog.dismissAllowingStateLoss();
                                }
                            })
                            .setEditValue(mBinding.tvPointnameRight.getText().toString())
                            .setTitle("设备点位")
                            .setEditHint("请输入设备点位")
                            .setMaxLength(12)
                            .show(getSupportFragmentManager(), "设备点位");
                }
            }
        });
    }

    @Override
    public void modifySuccess(String pointName) {
        mBinding.tvPointnameRight.setText(pointName);
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        devicesBean.setPointName(pointName);
        EventBus.getDefault().post(new DeviceChangedEvent(devicesBean.getDeviceId()));
    }
}
