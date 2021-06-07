package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.databinding.ActivityPointAndRegionBinding;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.RegionChangeEvent;
import com.ayla.hotelsaas.events.SceneChangedEvent;
import com.ayla.hotelsaas.mvp.present.PointAndRegionPresenter;
import com.ayla.hotelsaas.mvp.view.PointAndRegionView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.widget.ItemPickerDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备位置页面
 * 进入需要带上 deviceId  scopeId
 */
public class PointAndRegionActivity extends BaseMvpActivity<PointAndRegionView, PointAndRegionPresenter> implements PointAndRegionView {
    ActivityPointAndRegionBinding mBinding;
    private List<DeviceLocationBean> deviceListBean;
    private int defIndex = -1;
    private String locationName;
    private long regionId = -1l;

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
      Long roomId = SharePreferenceUtils.getLong(this, Constance.SP_ROOM_ID, 0);
        mPresenter.getAllDeviceLocation(roomId);
        String deviceId = getIntent().getStringExtra("deviceId");
        devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        if (devicesBean != null) {
            mBinding.tvPointnameRight.setText(devicesBean.getPointName());
            mBinding.tvRegionnameRight.setText(devicesBean.getRegionName());
        }
    }

    @Override
    protected void initListener() {
        mBinding.rlPointName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> purposeCategoryBeans = new ArrayList<>();
                for (int x = 0; x < deviceListBean.size(); x++) {
                    purposeCategoryBeans.add(deviceListBean.get(x).getRegionName());

                }

                for (int x = 0; x < deviceListBean.size(); x++) {
                    if (TextUtils.equals(deviceListBean.get(x).getRegionName(), locationName)) {
                        defIndex = x;
                        break;
                    }
                }

                ItemPickerDialog.newInstance()
                        .setSubTitle("请选择设备所属位置")
                        .setTitle("设备位置")
                        .setLocationIconRes(R.mipmap.choose_location_icon, 1000)
                        .setData(purposeCategoryBeans)
                        .setDefaultIndex(defIndex)
                        .setCallback(new ItemPickerDialog.Callback<String>() {

                            @Override
                            public void onCallback(String newLocationName) {
                                if (TextUtils.isEmpty(newLocationName) || newLocationName.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "设备名称不能为空", R.drawable.ic_toast_warming);
                                    return;
                                }
                                locationName = newLocationName;
                                for (int x = 0; x < deviceListBean.size(); x++) {
                                    if (TextUtils.equals(deviceListBean.get(x).getRegionName(), newLocationName)) {
                                        defIndex = x;
                                        break;
                                    }
                                }
                                EventBus.getDefault().post(new RegionChangeEvent());
                                regionId = deviceListBean.get(defIndex).getRegionId();
                                mPresenter.modifyRegionName(devicesBean.getDeviceId(), devicesBean.getNickname(), "", regionId, newLocationName);
                            }
                        })
                        .show(getSupportFragmentManager(), "dialog");
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

    @Override
    public void modifyRegionSuccess(String pointName) {
        mBinding.tvRegionnameRight.setText(pointName);
        devicesBean.setRegionName(pointName);
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        EventBus.getDefault().post(new DeviceChangedEvent(devicesBean.getDeviceId()));
    }


    @Override
    public void loadDeviceLocationSuccess(List<DeviceLocationBean> deviceListBean) {
        this.deviceListBean = deviceListBean;
    }
}
