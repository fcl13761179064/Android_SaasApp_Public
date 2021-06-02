package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.DeviceCategoryHandler;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.ActivityDeviceReplaceBinding;
import com.ayla.hotelsaas.mvp.present.DeviceReplacePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceReplaceView;
import com.ayla.hotelsaas.utils.TempUtils;

import java.util.List;

/**
 * 设备替换页面
 * 进入需要带上 deviceId  scopeId
 */
public class DeviceReplaceActivity extends BaseMvpActivity<DeviceReplaceView, DeviceReplacePresenter> implements DeviceReplaceView {
    ActivityDeviceReplaceBinding mBinding;

    @Override
    protected DeviceReplacePresenter initPresenter() {
        return new DeviceReplacePresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivityDeviceReplaceBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    String deviceId;
    long scopeId;
    DeviceCategoryHandler deviceCategoryHandler;

    @Override
    protected void initView() {
        deviceId = getIntent().getStringExtra("deviceId");
        scopeId = getIntent().getLongExtra("scopeId", 0);
        deviceCategoryHandler = new DeviceCategoryHandler(this, scopeId);
    }

    @Override
    protected void initListener() {
        mBinding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getGatewayId(deviceId);
            }
        });
    }

    @Override
    public void canReplace(String gatewayId, List<DeviceCategoryBean> deviceCategoryBeans) {
        DeviceListBean.DevicesBean replaceDeviceBean = MyApplication.getInstance().getDevicesBean(deviceId);
        Intent intent = new Intent();
        Bundle replaceInfoBundle = new Bundle();
        replaceInfoBundle.putString("replaceDeviceId", deviceId);
        replaceInfoBundle.putString("targetGatewayDeviceId", gatewayId);
        replaceInfoBundle.putString("replaceDeviceNickname", replaceDeviceBean.getNickname());
        intent.putExtra("replaceInfo", replaceInfoBundle);

        deviceCategoryHandler.bindOrReplace(deviceCategoryBeans, intent, null);//替换设备
    }

    @Override
    public void cannotReplace(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        deviceCategoryHandler.onActivityResult(requestCode, resultCode, data);
    }
}
