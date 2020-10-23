package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 参数
 * String deviceId
 * long scopeId
 */
public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.my_account_button)
    TextView my_account_button;
    @BindView(R.id.rl_device_function_rename)
    View rl_function_rename;
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
        appBar.setCenterText("更多");
        if (mDevicesBean != null && !TextUtils.isEmpty(mDevicesBean.getNickname())) {
            tv_device_name.setText(mDevicesBean.getNickname());
        }
        if (TempUtils.isDeviceGateway(mDevicesBean)) {
            rl_function_rename.setVisibility(View.GONE);
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
                        .setContent(getResources().getString(R.string.remove_device_content))
                        .show(getSupportFragmentManager(), "");
            }
        });

        rl_device_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String newName) {
                                if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "修改设备名称不能为空", R.drawable.ic_toast_warming).show();
                                    return;
                                } else {
                                    tv_device_name.setText(newName);
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
    public void renameFailed(String msg) {
        CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void renameSuccess(String newNickName) {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success).show();
        setResult(RESULT_OK);
        MyApplication.getInstance().getDevicesBean(deviceId).setNickname(newNickName);
        EventBus.getDefault().post(new DeviceChangedEvent(deviceId));
        finish();
    }

    @Override
    public void removeSuccess(Boolean is_rename) {
        CustomToast.makeText(this, "移除成功", R.drawable.ic_success).show();
        setResult(RESULT_OK);
        EventBus.getDefault().post(new DeviceRemovedEvent(deviceId));
        finish();
    }

    @Override
    public void removeFailed(String code, String msg) {
        CustomToast.makeText(this, "移除失败", R.drawable.ic_toast_warming).show();
    }

    @OnClick(R.id.rl_device_function_rename)
    public void handleFunctionRenameJump() {
        Intent intent = new Intent(this, FunctionRenameActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
    }
}
