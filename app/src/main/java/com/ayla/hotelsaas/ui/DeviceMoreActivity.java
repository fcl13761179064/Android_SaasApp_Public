package com.ayla.hotelsaas.ui;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AllCustomeDialog;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CommonDialog;


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

    private AllCustomeDialog allCustomeDialog;
    private DeviceListBean.DevicesBean mDevicesBean;
    private Long mScopeId;

    @Override
    public void refreshUI() {
        Constance.Is_Auto_ReFresh = -100;
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

    }

    @Override
    protected void initListener() {
        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                allCustomeDialog = new AllCustomeDialog(DeviceMoreActivity.this, getResources().getString(R.string.remove_device_content), getResources().getString(R.string.remove_device_title));
                allCustomeDialog.setOnSureClick("移除", new AllCustomeDialog.OnSureClick() {
                    @Override
                    public void click(Dialog dialog) {
                        if (mDevicesBean != null) {
                            mPresenter.deviceRemove(mDevicesBean.getDeviceId(), mScopeId, "2");
                        }
                        SoftInputUtil.showSoftInput(rl_device_rename);

                    }
                });
                allCustomeDialog.setOnCancelClick("取消", new AllCustomeDialog.OnCancelClick() {
                    @Override
                    public void click(Dialog dialog) {
                        allCustomeDialog.dismiss();
                    }
                });
                allCustomeDialog.setCanceledOnTouchOutside(false);
                allCustomeDialog.show();

            }
        });

        rl_device_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                CommonDialog.newInstance(new CommonDialog.DoneCallback() {
                    @Override
                    public void onDone(DialogFragment dialog, String txt) {
                        if (TextUtils.isEmpty(txt)) {
                            ToastUtils.showShortToast("修改设备名称不能为空");
                        } else {
                            tv_device_name.setText(txt);
                            if (mDevicesBean != null) {
                                mPresenter.deviceRenameMethod(mDevicesBean.getDeviceId(), txt);
                            }
                        }
                        SoftInputUtil.showSoftInput(rl_device_rename);
                        dialog.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onCancle(DialogFragment dialog) {
                        SoftInputUtil.hideShow(rl_device_rename);
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
        Constance.Is_Auto_ReFresh = Activity.RESULT_OK;
    }

    @Override
    public void operateRemoveSuccess(Boolean is_rename) {
        ToastUtils.showShortToast("移除成功");
        Constance.Is_Auto_ReFresh = Activity.RESULT_OK;
    }

    @Override
    public void operateMoveFailSuccess(String code, String msg) {
        ToastUtils.showShortToast(msg);
    }

}
