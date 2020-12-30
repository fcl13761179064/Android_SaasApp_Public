package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ItemPickerDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 参数
 * String deviceId
 * long scopeId
 */
public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {
    private final int REQUEST_CODE_SWITCH_USAGE_SET = 0X10;

    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.my_account_button)
    TextView my_account_button;
    @BindView(R.id.rl_device_function_rename)
    View rl_function_rename;
    @BindView(R.id.rl_switch_usage)
    View rl_switch_usage;
    @BindView(R.id.rl_purpose_change)
    View rl_purpose_change;
    @BindView(R.id.rl_device_detail)
    RelativeLayout rl_device_detail;

    private long mScopeId;
    private String deviceId;

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
        deviceId = getIntent().getStringExtra("deviceId");
        DeviceListBean.DevicesBean mDevicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        mScopeId = getIntent().getLongExtra("scopeId", 0);
        if (mDevicesBean != null) {
            if (mDevicesBean.getIsPurposeDevice() == 1 && mDevicesBean.getDeviceUseType() == 0) {//支持创建用途设备、并且现在不是用途设备的源设备，就可以进行用途设备配置
                rl_switch_usage.setVisibility(View.VISIBLE);
            } else {
                rl_switch_usage.setVisibility(View.GONE);
            }
            if (mDevicesBean.getDeviceUseType() == 1 && !TextUtils.isEmpty(mDevicesBean.getPurposeName())) {
                rl_purpose_change.setVisibility(View.VISIBLE);
            } else {
                rl_purpose_change.setVisibility(View.GONE);
            }
            tv_device_name.setText(mDevicesBean.getNickname());
            if (!TempUtils.isDeviceGateway(mDevicesBean)) {
                mPresenter.getRenameAbleFunctions(mDevicesBean.getCuId(), mDevicesBean.getDeviceCategory(), mDevicesBean.getDeviceId());
            }
        }
    }

    @Override
    protected void initListener() {

        rl_device_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceMoreActivity.this, DeviceDetailActivity.class);
                intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
                startActivity(intent);
            }
        });
        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceListBean.DevicesBean mDevicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
                if (mDevicesBean != null) {
                    String msg = getResources().getString(R.string.remove_device_content);
                    if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(mDevicesBean)) {
                        msg = "将移除此设备的所有用途设备，移除不可恢复是否继续？";
                    }
                    if (mDevicesBean.getDeviceUseType() == 3) {//用途源设备
                        msg = "移除此设备将同步移除相关用途设备，移除不可恢复是否继续？";
                    }
                    CustomAlarmDialog
                            .newInstance(new CustomAlarmDialog.Callback() {
                                @Override
                                public void onDone(CustomAlarmDialog dialog) {
                                    dialog.dismissAllowingStateLoss();
                                    mPresenter.deviceRemove(deviceId, mScopeId, "2");
                                }

                                @Override
                                public void onCancel(CustomAlarmDialog dialog) {
                                    dialog.dismissAllowingStateLoss();
                                }
                            })
                            .setTitle(getResources().getString(R.string.remove_device_title))
                            .setContent(msg)
                            .show(getSupportFragmentManager(), "");
                }
            }
        });

        rl_device_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String newName) {
                                if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "修改设备名称不能为空", R.drawable.ic_toast_warming);
                                    return;
                                } else {
                                    mPresenter.deviceRenameMethod(deviceId, newName);
                                }
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setEditValue(tv_device_name.getText().toString())
                        .setTitle("修改名称")
                        .setEditHint("请输入名称")
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "scene_name");
            }
        });
    }

    @Override
    public void renameFailed(String code, String msg) {
        if ("140001".equals(code)) {
            CustomToast.makeText(this, "该名称不能重复使用", R.drawable.ic_toast_warming);
        } else {
            CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_warming);
        }
    }

    @Override
    public void renameSuccess(String newNickName) {
        tv_device_name.setText(newNickName);
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        MyApplication.getInstance().getDevicesBean(deviceId).setNickname(newNickName);
        EventBus.getDefault().post(new DeviceChangedEvent(deviceId));
        finish();
    }

    @Override
    public void removeSuccess(Boolean is_rename) {
        CustomToast.makeText(this, "移除成功", R.drawable.ic_success);
        EventBus.getDefault().post(new DeviceRemovedEvent(deviceId));
        finish();
    }

    @Override
    public void removeFailed(Throwable throwable) {
    }

    @Override
    public void cannotRenameFunction() {
        rl_function_rename.setVisibility(View.GONE);
    }

    @Override
    public void canRenameFunction() {
        rl_function_rename.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPurposeCategory(List<PurposeCategoryBean> purposeCategoryBeans) {
        deviceId = getIntent().getStringExtra("deviceId");
        DeviceListBean.DevicesBean mDevicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        int defIndex = -1;
        String purposeName = mDevicesBean.getPurposeName();
        for (int i = 0; i < purposeCategoryBeans.size(); i++) {
            if (TextUtils.equals(purposeCategoryBeans.get(i).getPurposeName(), purposeName)) {
                defIndex = i;
                break;
            }
        }
        ItemPickerDialog.newInstance()
                .setSubTitle("请选择按键控制的设备")
                .setTitle("控制设备")
                .setIconRes(R.drawable.ic_purpose_select)
                .setData(purposeCategoryBeans)
                .setDefaultIndex(defIndex)
                .setCallback(new ItemPickerDialog.Callback<PurposeCategoryBean>() {
                    @Override
                    public void onCallback(PurposeCategoryBean object) {
                        mPresenter.updatePurpose(deviceId, object.getId());
                    }
                })
                .show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void updatePurposeSuccess() {
        CustomToast.makeText(this, "更新成功", R.drawable.ic_success);
        setResult(RESULT_OK);
        EventBus.getDefault().post(new DeviceRemovedEvent(deviceId));
        finish();
    }

    @Override
    public void updatePurposeFailed(Throwable throwable) {
        CustomToast.makeText(this, "更新失败", R.drawable.ic_toast_warming);
    }

    @OnClick(R.id.rl_device_function_rename)
    public void handleFunctionRenameJump() {
        Intent intent = new Intent(this, FunctionRenameActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }

    @OnClick(R.id.rl_switch_usage)
    public void handleSwitchUsage() {
        Intent intent = new Intent(this, SwitchUsageSettingActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("scopeId", mScopeId);
        startActivityForResult(intent, REQUEST_CODE_SWITCH_USAGE_SET);
    }

    /**
     * 控制设备切换
     */
    @OnClick(R.id.rl_purpose_change)
    public void handlePurposeChange() {
        mPresenter.getPurposeCategory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SWITCH_USAGE_SET && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            EventBus.getDefault().post(new DeviceAddEvent());
            finish();
        }
    }
}
