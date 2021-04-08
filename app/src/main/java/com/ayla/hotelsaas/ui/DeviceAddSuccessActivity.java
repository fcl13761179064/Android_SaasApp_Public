package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.databinding.ActivityDeviceAddSuccessBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.mvp.present.DeviceAddSuccessPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddSuccessView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.ItemPickerDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DeviceAddSuccessActivity extends BaseMvpActivity<DeviceAddSuccessView, DeviceAddSuccessPresenter> implements DeviceAddSuccessView {
    private ActivityDeviceAddSuccessBinding binding;
    private List<DeviceLocationBean> deviceListBean;
    private int defIndex = -1;
    private String LocationName = "";

    @Override
    protected DeviceAddSuccessPresenter initPresenter() {
        return new DeviceAddSuccessPresenter();
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityDeviceAddSuccessBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    private DeviceListBean.DevicesBean device;

    @Override
    protected void initView() {
        device = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("device");
        binding.etInput.setText(device.getNickname());
        binding.tvLocationPoint.setText(device.getRegionName());
        binding.tvLocationName.setText(device.getPointName());
    }

    @Override
    protected void initListener() {
        mPresenter.getAllDeviceLocation();
        deviceListBean = new ArrayList<>();
        binding.tvLocationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> purposeCategoryBeans = new ArrayList<>();
                for (int x = 0; x < deviceListBean.size(); x++) {
                    purposeCategoryBeans.add(deviceListBean.get(x).getRegionName());

                }

                for (int x = 0; x < deviceListBean.size(); x++) {
                    if (TextUtils.equals(deviceListBean.get(x).getRegionName(), LocationName)) {
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
                                LocationName = newLocationName;
                                binding.tvLocationPoint.setText(newLocationName);
                            }
                        })
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
        binding.etInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etInput.getText().toString();
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "设备名称不能为空", R.drawable.ic_toast_warming);
                                    return;
                                }
                                binding.etInput.setText(txt);
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setTitle("设备名称")
                        .setEditHint("请输入设备名称")
                        .setEditValue(name)
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "设备名称");
            }
        });
        binding.tvLocationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.tvLocationName.getText().toString();
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "设备点位不能为空", R.drawable.ic_toast_warming);
                                    return;
                                }
                                binding.tvLocationName.setText(txt);
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setTitle("设备点位")
                        .setEditHint("请输入设备点位")
                        .setEditValue(name)
                        .setMaxLength(12)
                        .show(getSupportFragmentManager(), "设备点位");
            }
        });

        binding.btBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = device.getNickname();
                String newNickname = binding.etInput.getText().toString();
                if (TextUtils.isEmpty(newNickname) || newNickname.trim().isEmpty()) {
                    CustomToast.makeText(MyApplication.getContext(), "设备名称不能为空", R.drawable.ic_toast_warming);
                    return;
                }
                String pointName = device.getPointName();
                String tv_location_name = binding.tvLocationPoint.getText().toString();
                String newLocationName = binding.tvLocationName.getText().toString();
                if (TextUtils.isEmpty(newLocationName) || newLocationName.trim().isEmpty()) {
                    CustomToast.makeText(MyApplication.getContext(), "设备点位不能为空", R.drawable.ic_toast_warming);
                    return;
                }
                if (TextUtils.equals(newNickname, nickname) && TextUtils.equals(newLocationName, pointName) && TextUtils.equals(tv_location_name, device.getRegionName())) {
                    finish();
                } else {
                    mPresenter.deviceRenameMethod(device.getDeviceId(), newNickname, newLocationName, device.getRegionId(), tv_location_name);
                }
            }
        });
    }

    @Override
    public void renameSuccess(String nickName) {
        finish();
    }

    @Override
    public void renameFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg("修改失败", throwable), R.drawable.ic_toast_warming);
    }

    @Override
    public void loadDeviceLocationSuccess(List<DeviceLocationBean> deviceListBean) {
        this.deviceListBean = deviceListBean;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new DeviceAddEvent());
        startActivity(new Intent(this, MainActivity.class));
    }
}
