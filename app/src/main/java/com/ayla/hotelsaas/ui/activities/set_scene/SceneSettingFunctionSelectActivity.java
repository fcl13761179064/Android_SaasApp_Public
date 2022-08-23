package com.ayla.hotelsaas.ui.activities.set_scene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionSelectAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.events.SceneItemEvent;
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener;
import com.ayla.hotelsaas.mvp.present.SceneSettingFunctionSelectPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingFunctionSelectView;
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet;
import com.ayla.hotelsaas.ui.activities.SceneSettingFunctionDatumSetActivity;
import com.ayla.hotelsaas.utils.CommonUtils;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.NPItemDecoration;
import com.ayla.hotelsaas.widget.scene_dialog.GroupActionChoosePropertyValueDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 场景联动 选择设备 设备属性列表
 */
public class SceneSettingFunctionSelectActivity extends BaseMvpActivity<SceneSettingFunctionSelectView, SceneSettingFunctionSelectPresenter> implements SceneSettingFunctionSelectView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBar appBar;
    private SceneSettingFunctionSelectAdapter mAdapter;
    private List<SceneItemEvent> allSceneItem;
    private int sceneType = 0;
    private int currentPos = -1;

    @Override
    protected SceneSettingFunctionSelectPresenter initPresenter() {
        return new SceneSettingFunctionSelectPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_function_select;
    }

    private DeviceListBean.DevicesBean deviceBean;
    private ArrayList<String> groupAbilities;
    private String groupId;
    private ChooseDevicePropertyValueUtil chooseDevicePropertyValueUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupAbilities = getIntent().getStringArrayListExtra(KEYS.GROUPACTION);
        groupId = getIntent().getStringExtra(KEYS.GROUPID);
        if (!TextUtils.isEmpty(groupId)) {
            mPresenter.getGroupAbility(groupId);
        } else {
            if (deviceBean != null && deviceBean.getDeviceId() != null) {
                String deviceId = deviceBean.getDeviceId();
                mPresenter.loadAllProperty(sceneType == 0, deviceId, deviceBean.getPid());
            } else {
                String deviceId = "0";
                String pid = "0";
                mPresenter.loadAllProperty(sceneType == 0, deviceId, pid);
            }
        }
    }

    @Override
    protected void initView() {
        sceneType = getIntent().getIntExtra(KEYS.SCENETYPE, 0);
        allSceneItem = new ArrayList<>();
        deviceBean = MyApplication.getInstance().getDevicesBean(getIntent().getStringExtra("deviceId"));
        String deviceName = getIntent().getStringExtra("deviceName");
        appBar.setCenterText(deviceName);
        mAdapter = new SceneSettingFunctionSelectAdapter(R.layout.item_scene_setting_function_select);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new NPItemDecoration(44, 0));
        mRecyclerView.setAdapter(mAdapter);
        chooseDevicePropertyValueUtil = new ChooseDevicePropertyValueUtil(sceneType, getSupportFragmentManager(), new ChoosePropertyValueListener() {
            @Override
            public void onUpdate(int currentPos, ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
                updateItemValue(currentPos, callBackBean);
            }

            @Override
            public void onToastContent(String content) {
                CustomToast.makeText(SceneSettingFunctionSelectActivity.this, content, R.drawable.ic_toast_warning);
            }

            @Override
            public void onProgress() {
                showProgress("加载中...");
            }

            @Override
            public void onFinish() {
                hideProgress();
            }
        });
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        List<DeviceTemplateBean.AttributesBean> data = mAdapter.getData();
        for (int i = 0; i < data.size(); i++) {
            String deviceId = getIntent().getStringExtra("deviceId");
            DeviceTemplateBean.AttributesBean attributesBean = data.get(i);
            SceneItemEvent sceneItemEvent = new SceneItemEvent(sceneType == 0, TextUtils.isEmpty(groupId) ? deviceId : groupId, attributesBean, attributesBean.getCallBackBean());
            allSceneItem.add(sceneItemEvent);
        }
        EventBus.getDefault().post(allSceneItem);
        Intent intent = new Intent();
        intent.putExtra("every_data", (Serializable) allSceneItem);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            DeviceTemplateBean.AttributesBean attributesBean = mAdapter.getItem(position);
            if (TextUtils.isEmpty(groupId)) {
                if (deviceBean != null && attributesBean != null) {
                    chooseDevicePropertyValueUtil.setCurrentPos(position);
                    chooseDevicePropertyValueUtil.setCurrentAttributesBean(attributesBean);
                    chooseDevicePropertyValueUtil.loadSingleProperty(deviceBean.getDeviceId(), attributesBean.getCode(), false);
                }
            } else {
                if (attributesBean != null) {
                    currentPos = position;
                    chooseDevicePropertyValueUtil.setCurrentPos(position);
                    chooseDevicePropertyValueUtil.showChooseGroupPropertyValue(attributesBean, false);
                }
            }
        });
    }

    private void jumpToGroup(DeviceTemplateBean.AttributesBean attributesBean) {
        Intent mainActivity = new Intent(SceneSettingFunctionSelectActivity.this, SceneSettingFunctionDatumSetActivity.class);
        mainActivity.putExtra("deviceName", attributesBean.getDisplayName());
        mainActivity.putExtra(KEYS.GROUPID, groupId);
        mainActivity.putExtra("type", sceneType);
        mainActivity.putExtra(KEYS.GROUPACTION, attributesBean);
        startActivityForResult(mainActivity, 0);
    }

    private void jumpNext(boolean autoJump, DeviceTemplateBean.AttributesBean attributesBean) {
        Intent mainActivity = new Intent(SceneSettingFunctionSelectActivity.this, SceneSettingFunctionDatumSetActivity.class);
        mainActivity.putExtras(getIntent());
        mainActivity.putExtra("autoJump", autoJump);
        if (deviceBean != null && deviceBean.getDeviceId() != null) {
            mainActivity.putExtra("deviceId", deviceBean.getDeviceId());
        } else {
            mainActivity.putExtra("deviceId", "0");
        }
        mainActivity.putExtra("type", sceneType);
        mainActivity.putExtra("property", attributesBean.getCode());
        mainActivity.putExtra("deviceName", attributesBean.getDisplayName());
        startActivityForResult(mainActivity, 0);

    }


    @Override
    public void showAllProperty(List<DeviceTemplateBean.AttributesBean> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public void loadGroupAbilitySuccess(List<NewGroupAbility> data) {
        List<DeviceTemplateBean.AttributesBean> beans = new ArrayList<>();
        if (groupAbilities != null) {
            for (int i = 0; i < groupAbilities.size(); i++) {
                String vCode = groupAbilities.get(i);
                String[] vCodeArray = vCode.split(";");
                if (vCodeArray.length == 2) {
                    for (int j = 0; j < data.size(); j++) {
                        NewGroupAbility newGroupAbility = data.get(j);
                        if (TextUtils.equals(newGroupAbility.getVersion(), vCodeArray[0]) && TextUtils.equals(newGroupAbility.getAbilityCode(), vCodeArray[1])) {
                            DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                            attributesBean.setCode(newGroupAbility.getAbilityCode());
                            attributesBean.setDisplayName(newGroupAbility.getDisplayName());
                            List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                            for (int k = 0; k < newGroupAbility.getAbilityValues().size(); k++) {
                                NewGroupAbility.AbilityValues abilityValue = newGroupAbility.getAbilityValues().get(k);
                                DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                valueBean.setDataType(abilityValue.getDataType());
                                valueBean.setDisplayName(abilityValue.getDisplayName());
                                valueBean.setValue(abilityValue.getValue());
                                valueBean.setAbilitySubCode(abilityValue.getAbilitySubCode());
                                valueBean.setVersion(newGroupAbility.getVersion());
                                NewGroupAbility.Setup setup = abilityValue.getSetup();
                                if (setup != null) {
                                    DeviceTemplateBean.AttributesBean.SetupBean setupBean = new DeviceTemplateBean.AttributesBean.SetupBean();
                                    try {
                                        setupBean.setMax(Double.parseDouble(setup.getMax()));
                                        setupBean.setMin(Double.parseDouble(setup.getMin()));
                                        setupBean.setStep(Double.parseDouble(setup.getStep()));
                                        setupBean.setUnit(setup.getUnit());
                                        valueBean.setSetupBean(setupBean);
                                    } catch (Exception e) {
                                        Log.e(TAG, "setUp数据类型异常");
                                    }
                                }
                                valueBeans.add(valueBean);
                            }
                            attributesBean.setValue(valueBeans);

                            beans.add(attributesBean);
                            break;
                        }
                    }
                }
            }
        }
        mAdapter.setNewData(beans);

    }

    @Override
    public void loadGroupAbilityFail(Throwable throwable) {
        if (throwable != null) {
            CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }

    private void updateItemValue(int currentSetPos, ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
        if (currentSetPos >= 0 && mAdapter.getData().size() > currentSetPos) {
            DeviceTemplateBean.AttributesBean attributesBean = mAdapter.getData().get(currentSetPos);
            if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                ISceneSettingFunctionDatumSet.SetupCallBackBean setupCallBackBean = ((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean);
                if (sceneType == 0) {
                    //条件时显示数值条件
                    String txtO = CommonUtils.INSTANCE.convertOperatorToTxt(setupCallBackBean.getOperator());
                    attributesBean.setDeviceAttr(String.format("%s %s %s", txtO, setupCallBackBean.getTargetValue(), setupCallBackBean.getSetupBean().getUnit()));
                } else
                    attributesBean.setDeviceAttr(String.format("%s %s", setupCallBackBean.getTargetValue(), setupCallBackBean.getSetupBean().getUnit()));
                attributesBean.setCallBackBean(callBackBean);
            } else if (callBackBean instanceof ISceneSettingFunctionDatumSet.EventCallBackBean) {
                String displayName = ((ISceneSettingFunctionDatumSet.EventCallBackBean) callBackBean).getEvnetBean().getDisplayName();
                attributesBean.setDeviceAttr(displayName);
                attributesBean.setCallBackBean(callBackBean);
            } else if (callBackBean instanceof ISceneSettingFunctionDatumSet.ValueCallBackBean) {
                String targetValue = ((ISceneSettingFunctionDatumSet.ValueCallBackBean) callBackBean).getValueBean().getDisplayName();
                attributesBean.setDeviceAttr(targetValue);
                attributesBean.setCallBackBean(callBackBean);
            } else if (callBackBean instanceof ISceneSettingFunctionDatumSet.BitValueCallBackBean) {
                String targetValue = ((ISceneSettingFunctionDatumSet.BitValueCallBackBean) callBackBean).getBitValueBean().getDisplayName();
                attributesBean.setDeviceAttr(targetValue);
                attributesBean.setCallBackBean(callBackBean);
            }
            if (callBackBean == null) {
                attributesBean.setDeviceAttr("");
                attributesBean.setCallBackBean(null);
            }
            mAdapter.notifyItemChanged(currentSetPos);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chooseDevicePropertyValueUtil != null) {
            chooseDevicePropertyValueUtil.clear();
        }
    }
}
