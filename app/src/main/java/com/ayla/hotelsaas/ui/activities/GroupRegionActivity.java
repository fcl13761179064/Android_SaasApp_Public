package com.ayla.hotelsaas.ui.activities;

import android.text.TextUtils;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.GroupDetail;
import com.ayla.hotelsaas.databinding.ActivityPointAndRegionBinding;
import com.ayla.hotelsaas.events.GroupUpdateEvent;
import com.ayla.hotelsaas.mvp.present.UpdateGroupLocationPresenter;
import com.ayla.hotelsaas.mvp.view.UpdateGroupLocationView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.common_dialog.ItemPickerDialog;
import com.blankj.utilcode.util.GsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备位置页面
 * 进入需要带上 deviceId  scopeId
 */
public class GroupRegionActivity extends BaseMvpActivity<UpdateGroupLocationView, UpdateGroupLocationPresenter> implements UpdateGroupLocationView {
    ActivityPointAndRegionBinding mBinding;
    private long regionId;
    private GroupDetail groupDetail;
    private String locationName;
    private long changeRegionId;

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @NotNull
    @Override
    protected UpdateGroupLocationPresenter initPresenter() {
        return new UpdateGroupLocationPresenter();
    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivityPointAndRegionBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initView() {
        groupDetail = (GroupDetail) getIntent().getSerializableExtra("group");
        String regionName = getIntent().getStringExtra("regionName");
        regionId = groupDetail.getRegionId();
        if (groupDetail != null) {
            mBinding.tvRegionnameRight.setText(regionName);
            locationName = regionName;
        }
    }

    @Override
    protected void initListener() {
        mBinding.rlPointName.setOnClickListener(v -> {
            List<String> purposeCategoryBeans = new ArrayList<>();
            List<DeviceLocationBean> locationBean = MyApplication.getInstance().getDevicesLocationBean();
            int selectIndex = -1;
            for (int i = 0; i < locationBean.size(); i++) {
                purposeCategoryBeans.add(locationBean.get(i).getRegionName());
                if (regionId == locationBean.get(i).getRegionId())
                    selectIndex = i;
            }
            ItemPickerDialog.newInstance()
                    .setSubTitle("请选择设备所属位置")
                    .setTitle("设备位置")
                    .setLocationIconRes(R.mipmap.choose_location_icon, 1000)
                    .setData(purposeCategoryBeans)
                    .setDefaultIndex(selectIndex)
                    .setCallback((ItemPickerDialog.Callback<String>) newLocationName -> {
                        if (TextUtils.isEmpty(newLocationName) || newLocationName.trim().isEmpty()) {
                            CustomToast.makeText(getBaseContext(), "设备名称不能为空", R.drawable.ic_toast_warning);
                            return;
                        }
                        for (int x = 0; x < locationBean.size(); x++) {
                            if (TextUtils.equals(locationBean.get(x).getRegionName(), newLocationName)) {
                                if (groupDetail != null) {
                                    GroupDetail newGroupDetail = groupDetail.getNewGroupDetail();
                                    long regionId = locationBean.get(x).getRegionId();
                                    newGroupDetail.setRegionId(regionId);
                                    locationName = locationBean.get(x).getRegionName();
                                    changeRegionId = regionId;
                                    mPresenter.updateGroup(GsonUtils.toJson(newGroupDetail));
                                }
                                break;
                            }
                        }

                    })
                    .show(getSupportFragmentManager(), "dialog");
        });
    }

    @Override
    public void updateLocationResult(Boolean result, Throwable throwable) {
        if (result) {
            CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
            mBinding.tvRegionnameRight.setText(locationName);
            regionId = changeRegionId;
            groupDetail.setGroupName(locationName);
            groupDetail.setRegionId(regionId);
            EventBus.getDefault().post(new GroupUpdateEvent());
        } else {
            if (throwable != null)
                CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
            else
                CustomToast.makeText(this, "操作失败", R.drawable.ic_toast_warning);

        }
    }
}
