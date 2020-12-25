package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.databinding.ActivityDeviceAddSuccessBinding;
import com.ayla.hotelsaas.mvp.present.DeviceAddSuccessPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddSuccessView;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

public class DeviceAddSuccessActivity extends BaseMvpActivity<DeviceAddSuccessView, DeviceAddSuccessPresenter> implements DeviceAddSuccessView {
    private ActivityDeviceAddSuccessBinding binding;

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

    @Override
    protected void initView() {
        String deviceName = getIntent().getStringExtra("deviceName");
        binding.etInput.setText(deviceName);
    }

    @Override
    protected void initListener() {
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
        binding.btBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = getIntent().getStringExtra("deviceName");
                String deviceId = getIntent().getStringExtra("deviceId");
                String newName = binding.etInput.getText().toString();
                if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                    CustomToast.makeText(MyApplication.getContext(), "设备名称不能为空", R.drawable.ic_toast_warming);
                    return;
                }
                if (TextUtils.equals(newName, deviceName)) {
                    finish();
                } else {
                    mPresenter.deviceRenameMethod(deviceId, newName);
                }
            }
        });
    }

    @Override
    public void renameSuccess(String nickName) {
        finish();
    }

    @Override
    public void renameFailed(String code, String msg) {
        if ("140001".equals(code)) {
            CustomToast.makeText(this, "该名称不能重复使用", R.drawable.ic_toast_warming);
        } else {
            CustomToast.makeText(MyApplication.getContext(), "修改失败", R.drawable.ic_toast_warming);
        }
    }
}
