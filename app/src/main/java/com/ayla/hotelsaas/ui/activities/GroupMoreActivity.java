package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.GroupDetail;
import com.ayla.hotelsaas.bean.GroupNewDeviceItem;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.constant.SwitchWorkMode;
import com.ayla.hotelsaas.events.GroupDeleteEvent;
import com.ayla.hotelsaas.events.GroupUpdateEvent;
import com.ayla.hotelsaas.mvp.present.GroupMorePresenter;
import com.ayla.hotelsaas.mvp.view.GroupMoreView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.common_dialog.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.common_dialog.ItemPickerDialog;
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog;
import com.blankj.utilcode.util.GsonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupMoreActivity extends BaseMvpActivity<GroupMoreView, GroupMorePresenter> implements GroupMoreView {
    @BindView(R.id.group_more_name)
    TextView name;
    @BindView(R.id.group_more_location)
    TextView location;
    @BindView(R.id.group_more_device_count)
    TextView deviceCount;
    private GroupDetail groupDetail;
    private String groupId;
    private String newGroupName;

    @Override
    protected GroupMorePresenter initPresenter() {
        return new GroupMorePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_more;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView() {
        //设置群组的数据
        groupId = getIntent().getStringExtra("groupId");
        mPresenter.getGroupDetail(groupId, true);
    }

    @OnClick({R.id.group_more_name, R.id.group_more_location, R.id.group_more_device_count, R.id.group_delete})
    public void onViewClick(View v) {
        if (v.getId() == R.id.group_more_name) {
            showChangeNameDialog();
        } else if (v.getId() == R.id.group_more_location) {
            if (groupDetail != null) {
//                Intent intent = new Intent(this, GroupRegionActivity.class);
//                intent.putExtra("group", groupDetail);
//                intent.putExtra("regionName", location.getText());
//                startActivity(intent);
                showSetLocationDialog();
            } else
                CustomToast.makeText(this, "未获取到编组详情", R.drawable.ic_toast_warning);

        } else if (v.getId() == R.id.group_more_device_count) {
            Intent intent = new Intent(this, DeviceMarshallEntryActivity.class);
            intent.putExtra("group", groupDetail);
            if (groupDetail.getGroupDeviceList().size() > 0) {
                intent.putExtra("deviceId", groupDetail.getGroupDeviceList().get(0).getDeviceId());
                intent.putExtra("scopeId", SharePreferenceUtils.getLong(this, ConstantValue.SP_ROOM_ID, 0));
            }
            intent.putExtra(KEYS.PRODUCTLABEL, getIntent().getStringExtra(KEYS.PRODUCTLABEL));
            startActivity(intent);
        } else if (v.getId() == R.id.group_delete) {
            showDeleteDialog();
        }
    }

    private void showDeleteDialog() {
        CustomAlarmDialog
                .newInstance(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        mPresenter.deleteGroup(getIntent().getStringExtra("groupId"));
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .setTitle("提示")
                .setContent("编组删除后，将无法进行群控，需要重新创建编组，确认是否删除")
                .show(getSupportFragmentManager(), "delete_group");
    }

    private void showChangeNameDialog() {
        ValueChangeDialog
                .newInstance((dialog, newName) -> {
                    if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                        CustomToast.makeText(getBaseContext(), "修改编组名称不能为空", R.drawable.ic_toast_warning);
                        return;
                    } else {
                        GroupDetail newGroupDetail = groupDetail.getNewGroupDetail();
                        newGroupDetail.setGroupName(newName);
                        newGroupName = newName;
                        mPresenter.updateGroup(GsonUtils.toJson(newGroupDetail));
                    }
                    dialog.dismissAllowingStateLoss();
                })
                .setEditValue(groupDetail.getGroupName())
                .setTitle("修改名称")
                .setEditHint("请输入名称")
                .setMaxLength(20)
                .show(getSupportFragmentManager(), "group_name");
    }

    private void showSetLocationDialog() {
        List<String> purposeCategoryBeans = new ArrayList<>();
        List<DeviceLocationBean> locationBean = MyApplication.getInstance().getDevicesLocationBean();
        int selectIndex = -1;
        long regionId = -1;
        if (groupDetail != null)
            regionId = groupDetail.getRegionId();
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
                                long newRID = locationBean.get(x).getRegionId();
                                GroupDetail newGroupDetail = groupDetail.getNewGroupDetail();
                                newGroupDetail.setRegionId(newRID);
                                mPresenter.updateGroup(GsonUtils.toJson(newGroupDetail));
                            }
                            break;
                        }
                    }

                })
                .show(getSupportFragmentManager(), "dialog");
    }

    @Override
    protected void initListener() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void groupUpdateOnEvent(GroupUpdateEvent event) {
        mPresenter.getGroupDetail(groupId, false);
    }

    @Override
    public void getDataResult(Throwable throwable) {
        if (throwable != null)
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
    }

    @Override
    public void updateGroupResult(Boolean result, Throwable throwable) {
        if (result) {
            mPresenter.getGroupDetail(groupId, true);
            //通知列表页更新数据
            GroupUpdateEvent event = new GroupUpdateEvent();
            event.groupId = groupId;
            event.groupName = newGroupName;
            EventBus.getDefault().post(event);
            newGroupName = null;

            CustomToast.makeText(this, "保存成功", R.drawable.ic_success);
        } else {
            if (throwable != null)
                CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
            else CustomToast.makeText(this, "操作失败", R.drawable.ic_toast_warning);
        }
    }

    @Override
    public void loadGroupDetailSuccess(GroupDetail groupDetail) {
        this.groupDetail = groupDetail;
        name.setText(groupDetail.getGroupName());
        List<DeviceLocationBean> locationBean = MyApplication.getInstance().getDevicesLocationBean();
        for (int i = 0; i < locationBean.size(); i++) {
            DeviceLocationBean deviceLocationBean = locationBean.get(i);
            if (deviceLocationBean.getRegionId() == groupDetail.getRegionId()) {
                location.setText(deviceLocationBean.getRegionName());
                break;
            }

        }
        if (groupDetail.getGroupDeviceList() != null) {
            deviceCount.setText(String.valueOf(groupDetail.getGroupDeviceList().size()));
        }
    }

    @Override
    public void deleteGroupResult(Boolean result) {
        if (groupDetail != null) {
            GroupNewDeviceItem groupNewDeviceItem = null;
            List<GroupNewDeviceItem> groupDeviceList = groupDetail.getGroupDeviceList();
            Out:
            for (GroupNewDeviceItem item : groupDeviceList) {
                for (String pid : ConstantValue.FOUR_SWITCH_PID) {
                    if (TextUtils.equals(pid, item.getPid())) {
                        groupNewDeviceItem = item;
                        break Out;
                    }
                }
            }
            if (groupNewDeviceItem != null) {
                mPresenter.updateSwitchMode(groupNewDeviceItem.getDeviceId(), groupNewDeviceItem.getSubDeviceKey(), SwitchWorkMode.POWER_MODE.getCode());
            } else {
                hideProgress();
                startActivity(new Intent(this, MainActivity.class));
                GroupDeleteEvent event = new GroupDeleteEvent();
                event.groupId = groupId;
                EventBus.getDefault().post(event);
                finish();
            }
        }
    }

    @Override
    public void updateSwitchModeResult(boolean result, Throwable throwable) {
        if (result) {
            startActivity(new Intent(this, MainActivity.class));
            GroupDeleteEvent event = new GroupDeleteEvent();
            event.groupId = groupId;
            EventBus.getDefault().post(event);
            finish();
        } else {
            if (throwable != null)
                CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }
}
