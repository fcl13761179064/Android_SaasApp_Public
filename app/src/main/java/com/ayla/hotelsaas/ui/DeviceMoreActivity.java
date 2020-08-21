package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.airbnb.lottie.L;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import java.util.List;

import butterknife.BindView;

public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.my_account_button)
    TextView my_account_button;

    private DeviceListBean.DevicesBean mDevicesBean;
    private Long mScopeId;

    @Override
    public void refreshUI() {
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        mScopeId = getIntent().getLongExtra("scopeId", 0);
        appBar.setCenterText("更多");
        if (mDevicesBean != null && !TextUtils.isEmpty(mDevicesBean.getNickname())) {
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
       // mPresenter.loadFunction(deviceId);

    }

    @Override
    protected void initListener() {
        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlarmDialog
                        .newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                if (mDevicesBean != null) {
                                    mPresenter.deviceRemove(mDevicesBean.getDeviceId(), mScopeId, "2");
                                }
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
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt)) {
                                    CustomToast.makeText(getBaseContext(), "修改设备名称不能为空", R.drawable.ic_toast_warming).show();
                                } else {
                                    tv_device_name.setText(txt);
                                    if (mDevicesBean != null) {
                                        mPresenter.deviceRenameMethod(mDevicesBean.getDeviceId(), txt);
                                    }
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
    public void operateError(String msg) {
        CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void operateSuccess(Boolean is_rename) {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void operateRemoveSuccess(Boolean is_rename) {
        CustomToast.makeText(this, "移除成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void operateMoveFailSuccess(String code, String msg) {
        CustomToast.makeText(this, "移除失败", R.drawable.ic_toast_warming).show();
    }
}
