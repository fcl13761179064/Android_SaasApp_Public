package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.databinding.ActivityDeviceAddSuccessBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.mvp.present.DeviceAddSuccessPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddSuccessView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.common_dialog.ItemPickerDialog;
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class DeviceAddSuccessActivity extends BaseMvpActivity<DeviceAddSuccessView, DeviceAddSuccessPresenter> implements DeviceAddSuccessView {
    private ActivityDeviceAddSuccessBinding binding;
    private List<DeviceLocationBean> deviceListBean;
    private int defIndex = -1;
    private String LocationName = "";
    private long regionId=-1l;
    private long roomId;

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
        String is_ap_normals = getIntent().getStringExtra("is_ap_normal");
        roomId = SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0);
        binding.etInput.setText(device.getNickname());
        binding.tvLocationPoint.setText(device.getRegionName());
        binding.tvLocationName.setText(device.getPointName());
        if (!TextUtils.isEmpty(is_ap_normals)){
            binding.appBar.setLeftImageView(0);
        }
    }

    @Override
    protected void initListener() {
        mPresenter.getAllDeviceLocation(roomId);
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
                        .setSubTitle("???????????????????????????")
                        .setTitle("????????????")
                        .setLocationIconRes(R.mipmap.choose_location_icon, 1000)
                        .setData(purposeCategoryBeans)
                        .setDefaultIndex(defIndex)
                        .setCallback(new ItemPickerDialog.Callback<String>() {

                            @Override
                            public void onCallback(String newLocationName) {
                                if (TextUtils.isEmpty(newLocationName) || newLocationName.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "????????????????????????", R.drawable.ic_toast_warning);
                                    return;
                                }
                                LocationName = newLocationName;
                                for (int x = 0; x < deviceListBean.size(); x++) {
                                    if (TextUtils.equals(deviceListBean.get(x).getRegionName(), newLocationName)) {
                                        defIndex = x;
                                        break;
                                    }
                                }
                                regionId = deviceListBean.get(defIndex).getRegionId();
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
                                    CustomToast.makeText(getBaseContext(), "????????????????????????", R.drawable.ic_toast_warning);
                                    return;
                                }
                                binding.etInput.setText(txt);
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setTitle("????????????")
                        .setEditHint("?????????????????????")
                        .setEditValue(name)
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "????????????");
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
                                    CustomToast.makeText(getBaseContext(), "????????????????????????", R.drawable.ic_toast_warning);
                                    return;
                                }
                                binding.tvLocationName.setText(txt);
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setTitle("????????????")
                        .setEditHint("?????????????????????")
                        .setEditValue(name)
                        .setMaxLength(12)
                        .show(getSupportFragmentManager(), "????????????");
            }
        });

        binding.btBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = device.getNickname();
                String newNickname = binding.etInput.getText().toString();
                if (TextUtils.isEmpty(newNickname) || newNickname.trim().isEmpty()) {
                    CustomToast.makeText(MyApplication.getContext(), "????????????????????????", R.drawable.ic_toast_warning);
                    return;
                }
                String pointName = device.getPointName();
                String tv_location_name = binding.tvLocationPoint.getText().toString();
                String newLocationName = binding.tvLocationName.getText().toString();
                if (TextUtils.isEmpty(newLocationName) || newLocationName.trim().isEmpty()) {
                    CustomToast.makeText(MyApplication.getContext(), "????????????????????????", R.drawable.ic_toast_warning);
                    return;
                }
                if (TextUtils.equals(newNickname, nickname) && TextUtils.equals(newLocationName, pointName) && TextUtils.equals(tv_location_name, device.getRegionName())) {
                    Intent mainActivity = new Intent(DeviceAddSuccessActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                } else {
                    if (regionId==-1l){
                        mPresenter.deviceRenameMethod(device.getDeviceId(), newNickname, newLocationName, device.getRegionId(), device.getRegionName());
                    }else {
                        mPresenter.deviceRenameMethod(device.getDeviceId(), newNickname, newLocationName,regionId, tv_location_name);
                    }
                }
            }
        });
    }

    @Override
    public void renameSuccess(String nickName) {
        Intent mainActivity = new Intent(DeviceAddSuccessActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    public void renameFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg("????????????", throwable), R.drawable.ic_toast_warning);
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
