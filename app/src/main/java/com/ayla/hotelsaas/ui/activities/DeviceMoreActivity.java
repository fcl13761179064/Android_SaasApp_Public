package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.FirmwareUpdateData;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.constant.ShowWay;
import com.ayla.hotelsaas.constant.SwitchWorkMode;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.ui.activities.firmware.FirmwareUpdateActivity;
import com.ayla.hotelsaas.ui.activities.set_switch.SwitchKeyConfigActivity;
import com.ayla.hotelsaas.utils.CommonUtils;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.common_dialog.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.common_dialog.ItemPickerDialog;
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog;
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog;
import com.ayla.hotelsaas.widget.scene_dialog.RadioItemData;
import com.ayla.hotelsaas.widget.scene_dialog.RadioItemsDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 参数
 * String deviceId
 * long scopeId
 */
public class DeviceMoreActivity extends BaseMvpActivity<DeviceMoreView, DeviceMorePresenter> implements DeviceMoreView {
    private final int REQUEST_CODE_SWITCH_USAGE_SET = 0X10;
    private final int REQUEST_CODE_FOR_A6_relerive_transfer = 0X11;
    private final int REQUEST_CODE_POSITION_SHOW = 0X12;
    private final int REQUEST_CODE_UPDATE_VERSION = 0X13;
    @BindView(R.id.rl_device_rename)
    RelativeLayout rl_device_rename;
    @BindView(R.id.tv_device_name)
    TextView tv_device_name;
    @BindView(R.id.my_account_button)
    TextView my_account_button;
    @BindView(R.id.rl_device_function_rename)
    View rl_function_rename;
    @BindView(R.id.rl_switch_default)
    View rl_switch_default;
    @BindView(R.id.rl_switch_usage)
    View rl_switch_usage;
    @BindView(R.id.rl_purpose_change)
    View rl_purpose_change;
    @BindView(R.id.rl_device_detail)
    RelativeLayout rl_device_detail;
    @BindView(R.id.rl_replace)
    RelativeLayout rl_replace;
    @BindView(R.id.ll_function_group_2)
    LinearLayout ll_function_group_2;
    @BindView(R.id.rl_device_marshalling)
    RelativeLayout rl_device_marshalling;
    @BindView(R.id.ll_marsall_layout)
    LinearLayout ll_marsall_layout;
    @BindView(R.id.my_updata_devices)
    Button my_updata_devices;
    @BindView(R.id.rl_A6_relerive)
    RelativeLayout rl_A6_relerive;
    @BindView(R.id.rl_firmware_num)
    RelativeLayout rlFirmwareNum;
    @BindView(R.id.firmware_num)
    TextView firmwareNum;
    @BindView(R.id.have_version_tag)
    View haveVersionTag;
    @BindView(R.id.layout_set_function)
    LinearLayout layoutSetFunction;
    @BindView(R.id.show_way)
    TextView showWayTv;


    private long mScopeId;
    private long businessId;
    private String deviceId;
    private String mRoom_name;
    private String move_wall_type;
    private DeviceListBean.DevicesBean mDevicesBean;
    private boolean isSHowMarshall = false;
    //点击固件版本号时，再次核对是否有新的版本
    private boolean checkFirmwareVersion = false;
    private List<RadioItemData> showWayData = new ArrayList<>();
    private ShowWay showWay = ShowWay.ALL;

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
        showWayData.add(new RadioItemData("1", "各按键统一展示", false));
        showWayData.add(new RadioItemData("3", "全部展示", false));
        checkFirmwareVersion = false;
        rlFirmwareNum.setVisibility(View.GONE);
        mPresenter.getDeviceMarShallPidData();
        deviceId = getIntent().getStringExtra("deviceId");
        mPresenter.loadFirmwareVersion(deviceId);
        mDevicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        mScopeId = getIntent().getLongExtra("scopeId", 0);
        businessId = getIntent().getLongExtra("businessId", 0);
        mRoom_name = getIntent().getStringExtra("roomName");
        move_wall_type = getIntent().getStringExtra("move_wall_type");
        // tv_device_detail.setText(mDevicesBean.getRegionName());
        if (mDevicesBean != null) {
            //获取展示方式
            mPresenter.getDeviceDisplay(mScopeId, mDevicesBean.getDeviceId());
            //获取版本号
            mPresenter.getDeviceFirmwareVersion(mDevicesBean.getDeviceId(), mDevicesBean.getPid());
            my_updata_devices.setVisibility(View.GONE);
            rl_A6_relerive.setVisibility(View.GONE);
            if (mDevicesBean.getIsPurposeDevice() == 1) {//支持创建用途设备、并且现在不是用途设备的源设备，就可以进行用途设备配置
                if ("ZBGW0-A000003".equals(mDevicesBean.getPid()) && ("3".equals(move_wall_type))) {
                    ll_function_group_2.setVisibility(View.GONE);
                    rl_switch_usage.setVisibility(View.GONE);
                    my_updata_devices.setVisibility(View.VISIBLE);
                    rl_A6_relerive.setVisibility(View.VISIBLE);
                } else {
                    ll_function_group_2.setVisibility(View.VISIBLE);
                    rl_switch_usage.setVisibility(View.VISIBLE);
                    if ("ZBGW0-A000003".equals(mDevicesBean.getPid())) {
                        ll_function_group_2.setVisibility(View.GONE);
                    }
                    //四路开关
                    for (String pid : ConstantValue.FOUR_SWITCH_PID) {
                        if (TextUtils.equals(pid, mDevicesBean.getPid())) {
                            ll_function_group_2.setVisibility(View.GONE);
                            layoutSetFunction.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            } else {
                rl_switch_usage.setVisibility(View.GONE);
            }
            if (mDevicesBean.getDeviceUseType() == 1 && !TextUtils.isEmpty(mDevicesBean.getPurposeName())) {
                ll_function_group_2.setVisibility(View.VISIBLE);
                rl_purpose_change.setVisibility(View.VISIBLE);
            } else {
                rl_purpose_change.setVisibility(View.GONE);
            }
            tv_device_name.setText(mDevicesBean.getNickname());
            if (!TempUtils.isDeviceGateway(mDevicesBean)) {
                mPresenter.functionLoad(mDevicesBean.getDeviceId(), mDevicesBean.getPid());
            }
            if (TempUtils.isDeviceNode(mDevicesBean)) {
                rl_replace.setVisibility(View.VISIBLE);
            } else {
                rl_replace.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void initListener() {
        rl_A6_relerive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceMoreActivity.this, ScanA6Activity.class);
                intent.putExtra("scanRelative", true);
                intent.putExtras(getIntent());
                startActivityForResult(intent, REQUEST_CODE_FOR_A6_relerive_transfer);
            }
        });

        my_updata_devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.A6UpdateTransferToZj(mScopeId, businessId, mRoom_name);

            }
        });
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
                                    boolean isSwitch = false;
                                    for (String item : ConstantValue.FOUR_SWITCH_PID) {
                                        if (TextUtils.equals(item, mDevicesBean.getPid())) {
                                            isSwitch = true;
                                            break;
                                        }
                                    }
                                    if (isSwitch) {
                                        int switchKeyCount = CommonUtils.INSTANCE.getSwitchKeyCount(mDevicesBean);
                                        JsonObject jsonObject = new JsonObject();
                                        JsonArray array = new JsonArray();
                                        for (int i = 1; i <= switchKeyCount; i++) {
                                            JsonObject itemObject = new JsonObject();
                                            itemObject.addProperty("deviceId", mDevicesBean.getDeviceId());
                                            itemObject.addProperty("propertiesName", String.format(Locale.CHINA, "%d%s", i, ":0x0006:WorkMode"));
                                            itemObject.addProperty("value", SwitchWorkMode.POWER_MODE.getCode());
                                            array.add(itemObject);
                                        }
                                        jsonObject.add("propertyList", array);
                                        showProgress("请稍后...");
                                        mPresenter.initSwitchMode(mDevicesBean.getDeviceId(), jsonObject.toString());
                                    } else {
                                        showProgress("请稍后...");
                                        mPresenter.deviceRemove(deviceId, mScopeId, "2", mDevicesBean.getPid());
                                    }
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
                DeviceListBean.DevicesBean mDevicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
                if (mDevicesBean != null) {
                    ValueChangeDialog
                            .newInstance(new ValueChangeDialog.DoneCallback() {
                                @Override
                                public void onDone(DialogFragment dialog, String newName) {
                                    if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
                                        CustomToast.makeText(getBaseContext(), "修改设备名称不能为空", R.drawable.ic_toast_warning);
                                        return;
                                    } else {
                                        mPresenter.deviceRenameMethod(deviceId, newName, mDevicesBean.getPointName(), mDevicesBean.getRegionId(), mDevicesBean.getRegionName());
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
            }
        });

        rl_device_marshalling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceMoreActivity.this, DeviceMarshallEntryActivity.class);
                intent.putExtras(getIntent());
                intent.putExtra("regionId", mDevicesBean.getRegionId());
                startActivity(intent);
            }
        });
        rlFirmwareNum.setOnClickListener(v -> {
            if (mDevicesBean != null) {
                if (TempUtils.isDeviceOnline(mDevicesBean)) {
                    checkFirmwareVersion = true;
                    mPresenter.getDeviceFirmwareVersion(mDevicesBean.getDeviceId(), mDevicesBean.getPid());
                } else {
                    CustomToast.makeText(this, "请检查设备联网情况", R.drawable.ic_toast_warning);
                }
//                mPresenter.getDeviceDetail(mDevicesBean.getDeviceId());
            }
        });
    }

    @Override
    public void renameFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg("修改失败", throwable), R.drawable.ic_toast_warning);
    }

    @Override
    public void renameSuccess(String newNickName) {
        tv_device_name.setText(newNickName);
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        devicesBean.setNickname(newNickName);
        devicesBean.setDeviceName(newNickName);
        EventBus.getDefault().post(new DeviceChangedEvent(deviceId, newNickName));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void removeSuccess(Boolean is_rename) {
        hideProgress();
        CustomToast.makeText(this, "移除成功", R.drawable.ic_success);
        EventBus.getDefault().post(new DeviceRemovedEvent(deviceId));
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void removeFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
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
        CustomToast.makeText(this, "更新失败", R.drawable.ic_toast_warning);
    }

    @Override
    public void canSetSwitchDefault() {
        rl_switch_default.setVisibility(View.VISIBLE);
    }

    @Override
    public void cannotSetSwitchDefault() {
        rl_switch_default.setVisibility(View.GONE);
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

    @OnClick(R.id.rl_location)
    public void handlePointChange() {
        Intent intent = new Intent(this, PointAndRegionActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivityForResult(intent, REQUEST_CODE_POSITION_SHOW);
    }

    @OnClick(R.id.rl_replace)
    public void handleReplace() {
        Intent intent = new Intent(this, DeviceReplaceActivity.class);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("scopeId", mScopeId);
        startActivity(intent);
    }


    @OnClick(R.id.rl_switch_default)
    public void handleSwitchDefault() {
        Intent intent = new Intent(this, SwitchDefaultListActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);

    }

    @OnClick({R.id.config_button, R.id.layout_show_way})
    public void handleSwitchConfig(View v) {
        if (v.getId() == R.id.config_button) {
            //按键配置
            Intent intent = new Intent(this, SwitchKeyConfigActivity.class);
            intent.putExtra(KEYS.DEVICEID, mDevicesBean.getDeviceId());
            intent.putExtra(KEYS.SCOPE_ID, mScopeId);
            intent.putExtra(KEYS.REGION_ID, mDevicesBean.getRegionId());
            intent.putExtra(KEYS.PRODUCTLABEL, getIntent().getStringExtra(KEYS.PRODUCTLABEL));
            startActivity(intent);
        } else if (v.getId() == R.id.layout_show_way) {
            //展示方式
            showSetShowWayDialog();
        }
    }

    private void showSetShowWayDialog() {
        for (int i = 0; i < showWayData.size(); i++) {
            RadioItemData itemData = showWayData.get(i);
            if (TextUtils.equals(itemData.getId(), String.valueOf(showWay.getType())))
                itemData.setCheck(true);
            else itemData.setCheck(false);
        }
        RadioItemsDialog.RadioItemsBuilder builder = new RadioItemsDialog.RadioItemsBuilder();
        builder.setTitle("展示方式").setData(showWayData).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof RadioItemsDialog.RadioItemsParams) {
                    RadioItemData selectData = ((RadioItemsDialog.RadioItemsParams) dialog.getParams()).getSelectData();
                    if (selectData != null) {
                        mPresenter.saveDeviceDisplay(CommonUtils.INSTANCE.getShowWay(Integer.parseInt(selectData.getId())), mDevicesBean.getDeviceId(), mScopeId);
                    }
                }
            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SWITCH_USAGE_SET && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            EventBus.getDefault().post(new DeviceAddEvent());
            finish();
        } else if (requestCode == REQUEST_CODE_FOR_A6_relerive_transfer && resultCode == RESULT_OK) {//获取到了DSN
            if (data != null) {
                String deviceId = data.getStringExtra("result").trim();
                if (!TextUtils.isEmpty(deviceId)) {
                    Bundle bundle = new Bundle();
                    Uri uri = Uri.parse(deviceId);
                    Set<String> paramNames = uri.getQueryParameterNames();
                    for (String paramName : paramNames) {
                        bundle.putString(paramName, new String(Base64.decode(uri.getQueryParameter(paramName), Base64.NO_WRAP)));
                    }
                    String device_Id = (String) bundle.get("deviceId");
                    String regToken = (String) bundle.get("regToken");
                    String tempToken = (String) bundle.get("tempToken");
                    mPresenter.A6TransferToZj(device_Id, mScopeId, businessId, mRoom_name, regToken, tempToken);
                }
            }
        }/*else  if (requestCode ==REQUEST_CODE_POSITION_SHOW && resultCode == RESULT_OK){
            if (data != null) {
                tv_device_detail.setText(mDevicesBean.getRegionName());
            }
        }*/
        if (requestCode == REQUEST_CODE_UPDATE_VERSION && resultCode == RESULT_OK) {
            if (mDevicesBean != null) {
                checkFirmwareVersion = false;
                mPresenter.loadFirmwareVersion(deviceId);
                mPresenter.getDeviceFirmwareVersion(mDevicesBean.getDeviceId(), mDevicesBean.getPid());
            }
        }
    }


    @Override
    public void MarshallEntryPidSuccess(List<String> data) {
        for (String DevicePid : data) {
            if (!TextUtils.isEmpty(DevicePid) && DevicePid.equals(mDevicesBean.getPid())) {
                isSHowMarshall = true;
                break;
            }
        }
        if (isSHowMarshall) {
            ll_marsall_layout.setVisibility(View.VISIBLE);
        } else {
            ll_marsall_layout.setVisibility(View.GONE);
        }
        //特殊处理四路开关Pro，隐藏编组的入口
        for (String pid : ConstantValue.FOUR_SWITCH_PID) {
            if (TextUtils.equals(pid, mDevicesBean.getPid())) {
                ll_marsall_layout.setVisibility(View.GONE);
                //隐藏设备替换
                rl_replace.setVisibility(View.GONE);
                break;
            }
        }

    }

    @Override
    public void MarshallEntryPidFail(Throwable throwable) {

    }

    @Override
    public void transferSuccess(BaseResult baseResult) {
        if (baseResult.isSuccess()) {
            CustomToast.makeText(this, "关联成功", R.drawable.ic_success);
        } else {
            CustomToast.makeText(this, "关联失败", R.drawable.ic_toast_warning);
        }

    }

    @Override
    public void transferFailed(Throwable throwable) {
        CustomToast.makeText(this, "关联失败", R.drawable.ic_toast_warning);
    }

    @Override
    public void updatetransferSuccess(BaseResult result) {
        if (result.isSuccess()) {
            CustomToast.makeText(this, "更新成功", R.drawable.ic_success);
        } else {
            if ("194000".equals(result.code)) {
                CustomToast.makeText(this, "请扫码关联", R.drawable.ic_toast_warning);
            } else {
                CustomToast.makeText(this, "更新失败", R.drawable.ic_toast_warning);
            }
        }

    }

    @Override
    public void updatetransferFailed(Throwable throwable) {
        CustomToast.makeText(this, "更新失败", R.drawable.ic_toast_warning);
    }

    @Override
    public void getFirmwareNewVersion(BaseResult<FirmwareUpdateData> result) {
//        haveVersionTag.setVisibility(View.VISIBLE);
//            if (checkFirmwareVersion) {
//                //跳转到下一个页面
//                checkFirmwareVersion = false;
//                Intent intent = new Intent(this, FirmwareUpdateActivity.class);
////                result.data.setCurrentVersion(firmwareNum.getText().toString());
////                result.data.setDsn(deviceId);
////                if (mDevicesBean != null) {
////                    result.data.setDeviceName(mDevicesBean.getDeviceName());
////                    result.data.setIconUrl(mDevicesBean.getIconUrl());
////                }
////                intent.putExtra(KEYS.FIRMWAREVESIONDATA, result.data);
//                startActivityForResult(intent, 1000);
//            }
        haveVersionTag.setVisibility(View.GONE);
        if (result.data != null) {
            haveVersionTag.setVisibility(View.VISIBLE);
            if (checkFirmwareVersion) {
                //跳转到下一个页面
                checkFirmwareVersion = false;
                Intent intent = new Intent(this, FirmwareUpdateActivity.class);
                result.data.setCurrentVersion(firmwareNum.getText().toString());
                result.data.setDsn(deviceId);
                if (mDevicesBean != null) {
                    result.data.setDeviceName(mDevicesBean.getDeviceName());
                    result.data.setIconUrl(mDevicesBean.getIconUrl());
                }
                intent.putExtra(KEYS.FIRMWAREVESIONDATA, result.data);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_VERSION);
            }
        } else {
            if (checkFirmwareVersion) {
                CustomToast.makeText(this, "已是最新版本", R.drawable.ic_success);
            }
        }
    }

    @Override
    public void getFirmwareNewVersionFail(Throwable throwable) {
        checkFirmwareVersion = false;
        if (throwable != null) {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }

    @Override
    public void getCurrentFirmwareVersion(String version) {
        if (!TextUtils.isEmpty(version)) {
            rlFirmwareNum.setVisibility(View.VISIBLE);
            firmwareNum.setText(version);
        }
    }

    @Override
    public void getFirmwareVersionFail(Throwable throwable) {
        if (throwable != null) {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }

    @Override
    public void onDeviceStatus(Boolean online) {
        if (online) {
            checkFirmwareVersion = true;
            mPresenter.getDeviceFirmwareVersion(mDevicesBean.getDeviceId(), mDevicesBean.getPid());
        } else {
            hideProgress();
            CustomToast.makeText(this, "请检查设备联网情况", R.drawable.ic_toast_warning);
        }
    }

    @Override
    public void onSaveDisplaySuccess(ShowWay way) {
        showWay = way;
        showWayTv.setText(CommonUtils.INSTANCE.getShowWayName(showWay));
        EventBus.getDefault().post(new DeviceAddEvent());
    }

    @Override
    public void onDisplayFail(Throwable throwable) {
        if (throwable != null)
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);

    }

    @Override
    public void getDisplaySuccess(ShowWay way) {
        showWay = way;
        showWayTv.setText(CommonUtils.INSTANCE.getShowWayName(showWay));
    }

    @Override
    public void initSwitchModeResult(boolean result, Throwable throwable) {
        if (result) {
            mPresenter.deviceRemove(deviceId, mScopeId, "2", mDevicesBean.getPid());
        } else {
            if (throwable != null)
                CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }
}
