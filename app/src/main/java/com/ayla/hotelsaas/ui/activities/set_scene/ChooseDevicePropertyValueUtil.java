package com.ayla.hotelsaas.ui.activities.set_scene;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.interfaces.ChoosePropertyValueListener;
import com.ayla.hotelsaas.interfaces.HandleItemGroupPropertyListener;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.ui.activities.ISceneSettingFunctionDatumSet;
import com.ayla.hotelsaas.utils.CommonUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.scene_dialog.AylaBaseDialog;
import com.ayla.hotelsaas.widget.scene_dialog.ChooseGroupItemValueDialog;
import com.ayla.hotelsaas.widget.scene_dialog.GradientSeekbarDialog;
import com.ayla.hotelsaas.widget.scene_dialog.GroupActionChoosePropertyValueDialog;
import com.ayla.hotelsaas.widget.scene_dialog.InputValueDialog;
import com.ayla.hotelsaas.widget.scene_dialog.MenuItemsData;
import com.ayla.hotelsaas.widget.scene_dialog.MenuItemsDialog;
import com.ayla.hotelsaas.widget.scene_dialog.RadioItemData;
import com.ayla.hotelsaas.widget.scene_dialog.RadioItemsDialog;
import com.ayla.hotelsaas.widget.scene_dialog.SeekbarDialog;
import com.ayla.hotelsaas.widget.scene_dialog.SeekbarWithClickDialog;
import com.ayla.hotelsaas.widget.scene_dialog.WheelViewDialog;
import com.ayla.hotelsaas.widget.scene_dialog.WheelViewItemData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 选择设备属性值 工具类，编辑和新增时通用
 */
public class ChooseDevicePropertyValueUtil {
    private MenuItemsDialog menuItemsDialog;
    private int currentPos = -1;
    private int sceneType = 0;
    private ChoosePropertyValueListener choosePropertyValueListener;
    //当前设置的属性项 新增动作条件属性时更新当前属性的值以及编辑时设置选择的value
    private DeviceTemplateBean.AttributesBean currentAttributesBean;
    private BaseSceneBean.Action action;
    private BaseSceneBean.Condition condition;
    private BaseSceneBean.GroupAction groupAction;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public void setGroupAction(BaseSceneBean.GroupAction groupAction) {
        this.groupAction = groupAction;
    }

    public void setAction(BaseSceneBean.Action action) {
        this.action = action;
    }

    public void setCondition(BaseSceneBean.Condition condition) {
        this.condition = condition;
    }

    public DeviceTemplateBean.AttributesBean getCurrentAttributesBean() {
        return currentAttributesBean;
    }

    public int getSceneType() {
        return sceneType;
    }

    private FragmentManager fragmentManager;

    private ChooseDevicePropertyValueUtil() {
        //禁止使用空构造方法实例化
    }

    public ChooseDevicePropertyValueUtil(int sceneType, @NonNull FragmentManager fragmentManager, @NonNull ChoosePropertyValueListener choosePropertyValueListener) {
        this.sceneType = sceneType;
        this.choosePropertyValueListener = choosePropertyValueListener;
        this.fragmentManager = fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    public void setCurrentAttributesBean(DeviceTemplateBean.AttributesBean currentAttributesBean) {
        this.currentAttributesBean = currentAttributesBean;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    //编组编辑
    public void loadGroupSingleProperty(String groupId, String code) {
        Disposable subscribe = RequestModel.getInstance().getGroupAbility(groupId).subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable ->
                        choosePropertyValueListener.onProgress())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupAbilities -> {
                    for (int j = 0; j < groupAbilities.size(); j++) {
                        NewGroupAbility newGroupAbility = groupAbilities.get(j);
                        if (TextUtils.equals(newGroupAbility.getAbilityCode(), code)) {
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
                                        Log.e("S", "setUp数据类型异常");
                                    }
                                }
                                valueBeans.add(valueBean);
                            }
                            attributesBean.setValue(valueBeans);
                            currentAttributesBean = attributesBean;
                            showChooseGroupPropertyValue(attributesBean, true);
                            break;
                        }
                    }
                    if (choosePropertyValueListener != null) {
                        choosePropertyValueListener.onFinish();
                    }
                }, throwable -> {
                    if (choosePropertyValueListener != null) {
                        choosePropertyValueListener.onFinish();
                    }
                });
        mCompositeDisposable.add(subscribe);
    }


    //编组新增
    public void showChooseGroupPropertyValue(DeviceTemplateBean.AttributesBean attributesBean, boolean edit) {
        GroupActionChoosePropertyValueDialog groupActionChoosePropertyValueDialog = GroupActionChoosePropertyValueDialog.Companion.newInstance(attributesBean, groupAction, currentPos, edit);
        groupActionChoosePropertyValueDialog.setHandleItemGroupPropertyListener(new HandleItemGroupPropertyListener() {
            @Override
            public void onShowChooseGroupItemValueDialog(@NonNull ISceneSettingFunctionDatumSet.SetupCallBackBean callBackBean) {
                ChooseGroupItemValueDialog.Companion.newInstance(currentPos, callBackBean, choosePropertyValueListener, groupActionChoosePropertyValueDialog)
                        .show(fragmentManager, "");
            }

            @Override
            public void onShowInputValueDialog(@NonNull DeviceTemplateBean.AttributesBean.ValueBean valueBean, @NonNull String targetValue) {
                if (choosePropertyValueListener != null) {
                    GroupInputValueUtil.INSTANCE.showSetNumberValueDialog(targetValue, valueBean, currentPos, fragmentManager, choosePropertyValueListener, groupActionChoosePropertyValueDialog);
                }
            }

            @Override
            public void onUpdateValue(int pos, @NonNull ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
                if (choosePropertyValueListener != null) {
                    choosePropertyValueListener.onUpdate(pos, callBackBean);
                }
            }
        });
        groupActionChoosePropertyValueDialog.show(fragmentManager, "");
    }


    //设备
    public void loadSingleProperty(String deviceId, String property, boolean edit) {
        final DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        Disposable subscribe = RequestModel.getInstance()
                .fetchDeviceTemplate(devicesBean.getPid())
                .map(deviceTemplateBeanBaseResult -> deviceTemplateBeanBaseResult.data)
                .compose(RequestModel.getInstance().modifyTemplateDisplayName(deviceId))
                .map(deviceTemplateBean -> {
                    for (DeviceTemplateBean.AttributesBean attribute : deviceTemplateBean.getAttributes()) {
                        if (TextUtils.equals(attribute.getCode(), property)) {
                            return attribute;
                        }
                    }

                    for (DeviceTemplateBean.EventbutesBean attribute : deviceTemplateBean.getEvents()) {
                        if (property.endsWith(".")) {
                            String[] split = property.split("\\.");
                            if (TextUtils.equals(attribute.getCode(), split[0])) {
                                attribute.setCode(property);
                                return attribute;
                            }
                        } else {
                            DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                            String[] spiltCode = property.split("\\.");
                            if (attribute.getCode().equals(spiltCode[0])) {
                                String parentName = attribute.getDisplayName();
                                for (DeviceTemplateBean.AttributesBean outparam : attribute.getOutParams()) {
                                    if (TextUtils.equals(outparam.getCode(), spiltCode[1])) {
                                        outparam.setDisplayName(parentName + "-" + outparam.getDisplayName());
                                        outparam.setCode(property);
                                        return outparam;
                                    }
                                }
                            }
                        }
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onProgress();
                })
                .doFinally(() -> {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onFinish();

                })
                .subscribe(attributesBean -> {
                    if (edit) {
                        //编辑时赋值
                        currentAttributesBean = attributesBean;
                        String rightValue = "";
                        String operator = "";
                        if (sceneType == 0 && condition instanceof BaseSceneBean.DeviceCondition) {
                            rightValue = ((BaseSceneBean.DeviceCondition) condition).getRightValue();
                            operator = ((BaseSceneBean.DeviceCondition) condition).getOperator();
                        } else if (sceneType == 1 && action instanceof BaseSceneBean.DeviceAction) {
                            rightValue = action.getRightValue();
                            operator = action.getOperator();
                        }
                        if (currentAttributesBean.getValue() != null) {
                            //ValueCallbackBean
                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                            valueBean.setValue(rightValue);
                            ISceneSettingFunctionDatumSet.ValueCallBackBean valueCallBackBean = new ISceneSettingFunctionDatumSet.ValueCallBackBean(valueBean);
                            currentAttributesBean.setCallBackBean(valueCallBackBean);
                        } else if (currentAttributesBean.getBitValue() != null) {
                            //ValueCallbackBean
                            DeviceTemplateBean.AttributesBean.BitValueBean bitValueBean = new DeviceTemplateBean.AttributesBean.BitValueBean();
                            int rightValueInt = 0;
                            try {
                                rightValueInt = Integer.parseInt(rightValue);
                            } catch (Exception ignored) {
                            }
                            bitValueBean.setValue(rightValueInt);
                            ISceneSettingFunctionDatumSet.BitValueCallBackBean bitValueCallBackBean = new ISceneSettingFunctionDatumSet.BitValueCallBackBean(bitValueBean);
                            currentAttributesBean.setCallBackBean(bitValueCallBackBean);
                        } else if (currentAttributesBean.getSetup() != null) {
                            ISceneSettingFunctionDatumSet.SetupCallBackBean setupCallBackBean = new ISceneSettingFunctionDatumSet.SetupCallBackBean(
                                    operator,
                                    rightValue,
                                    currentAttributesBean.getSetup(),
                                    ""
                            );
                            currentAttributesBean.setCallBackBean(setupCallBackBean);
                        } else {
                            DeviceTemplateBean.EventbutesBean eventbutesBean = new DeviceTemplateBean.EventbutesBean();
                            currentAttributesBean.setCallBackBean(new ISceneSettingFunctionDatumSet.EventCallBackBean(eventbutesBean));
                        }
                    }
                    chooseDevicePropertyValue(attributesBean, edit);
                }, throwable -> {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onToastContent(TempUtils.getLocalErrorMsg(throwable));
                });
        mCompositeDisposable.add(subscribe);
    }


    private void chooseDevicePropertyValue(DeviceTemplateBean.AttributesBean attributesBean, boolean edit) {
        if (attributesBean.getValue() != null) {
            showRadioDialog(attributesBean, false);
        } else if (attributesBean.getBitValue() != null) {
            showRadioDialog(attributesBean, false);
        } else if (attributesBean.getSetup() != null) {
            String unit = attributesBean.getSetup().getUnit();
            Double max = attributesBean.getSetup().getMax();
            if ("lm".equalsIgnoreCase(unit)) {
                showSeekbarWithClickDialog(attributesBean, null);
            } else if ("SAT".equalsIgnoreCase(unit) || "k".equalsIgnoreCase(unit)) {
                showGradientSeekbarDialog(attributesBean);
            } else if ("°C".equalsIgnoreCase(unit)) {
                if (sceneType == 0)
                    showMenuItemsDialog(attributesBean);
                else
                    showSeekbarWithClickDialog(attributesBean, null);
            } else if (max > 1000) {
                //手动输入值
                if (sceneType == 0)
                    showMenuItemsDialog(attributesBean);
                else
                    showInputValueDialog(attributesBean, null);
            } else {
                if (sceneType == 0)
                    showMenuItemsDialog(attributesBean);
                else
                    showSeekbarWithClickDialog(attributesBean, null);
            }
        } else {
            //事件类型属性
            showRadioDialog(attributesBean, !edit);
        }
    }


    private void showMenuItemsDialog(DeviceTemplateBean.AttributesBean attributesBean) {
        String conditionValue = "等于";
        DeviceTemplateBean.AttributesBean.SetupBean setup = attributesBean.getSetup();
        String tips = String.format("取值范围 %s %s-%s %s", setup.getMin().intValue(), setup.getUnit(), setup.getMax().intValue(), setup.getUnit());
        String value = String.format("%s %s", setup.getMin().intValue(), setup.getUnit());
        if (currentAttributesBean != null) {
            if (currentAttributesBean.getCallBackBean() != null && currentAttributesBean.getCallBackBean() instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                ISceneSettingFunctionDatumSet.SetupCallBackBean callBackBean = (ISceneSettingFunctionDatumSet.SetupCallBackBean) currentAttributesBean.getCallBackBean();
                conditionValue = CommonUtils.INSTANCE.convertOperatorToTxt(callBackBean.getOperator());
                value = String.format("%s %s", callBackBean.getTargetValue(), setup.getUnit());
            }
        }
        ArrayList<MenuItemsData> datas = new ArrayList<>();
        datas.add(new MenuItemsData("0", "数值条件", conditionValue));
        datas.add(new MenuItemsData("1", attributesBean.getDisplayName(), value));
        MenuItemsDialog.MenuItemsBuilder menuItemsBuilder = new MenuItemsDialog.MenuItemsBuilder();
        menuItemsDialog = menuItemsBuilder.setTitle(attributesBean.getDisplayName()).setTips(tips).setData(datas).setOnDialogItemClickListener(item -> {
            if (TextUtils.equals(item.getId(), "0")) {
                ArrayList<WheelViewItemData> dataItems = new ArrayList<>();
                dataItems.add(new WheelViewItemData("0", "大于"));
                dataItems.add(new WheelViewItemData("1", "等于"));
                dataItems.add(new WheelViewItemData("2", "小于"));
                showWheelViewDialog("数值条件", dataItems, new WheelViewItemData("", item.getContent()), item.getId());
            } else {
                Double max = attributesBean.getSetup().getMax();
                if (max > 1000)
                    showInputValueDialog(attributesBean, item);
                else {
                    showSeekbarWithClickDialog(attributesBean, item);
                }
            }
        }).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (menuItemsDialog != null) {
                    List<MenuItemsData> itemData = menuItemsDialog.getItemData();
                    if (itemData.size() == 2) {
                        String operator = CommonUtils.INSTANCE.convertTxtToOperator(itemData.get(0).getContent());
                        MenuItemsData menuItemsData = itemData.get(1);
                        String[] valueArray = menuItemsData.getContent().split(" ");
                        if (valueArray.length == 2) {
                            ISceneSettingFunctionDatumSet.SetupCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.SetupCallBackBean(operator, valueArray[0], attributesBean.getSetup(), "");
                            if (choosePropertyValueListener != null)
                                choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                        }
                        menuItemsDialog = null;
                    }
                }

            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {
                menuItemsDialog = null;
            }
        }).show(fragmentManager, "");
    }

    private void showWheelViewDialog(String title, ArrayList<WheelViewItemData> data, WheelViewItemData value, final String id) {
        WheelViewDialog.WheelViewDialogBuilder wheelViewDialogBuilder = new WheelViewDialog.WheelViewDialogBuilder();
        wheelViewDialogBuilder.setTitle(title).setData(data).setValue(value).setDialogGravity(Gravity.BOTTOM).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof WheelViewDialog.WheelViewDialogParams) {
                    WheelViewItemData selectData = ((WheelViewDialog.WheelViewDialogParams) dialog.getParams()).getSelectData();
                    if (menuItemsDialog != null && selectData != null)
                        menuItemsDialog.updateItem(id, selectData.getName());
                }
            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(fragmentManager, "");
    }

    /**
     * @param attributesBean
     * @param menuItemsData  设置条件value时传入 其余传null
     */
    private void showInputValueDialog(DeviceTemplateBean.AttributesBean attributesBean, MenuItemsData menuItemsData) {
        int value = 0;
        final DeviceTemplateBean.AttributesBean.SetupBean setupBean = attributesBean.getSetup();
        String rangeTxt = String.format("%s %s - %s %s", setupBean.getMin().intValue(), setupBean.getUnit(), setupBean.getMax().intValue(), setupBean.getUnit());
        if (menuItemsData != null) {
            //条件时输入值 content=value+单位 以空格分隔
            String content = menuItemsData.getContent();
            String[] contentArray = content.split(" ");
            if (contentArray.length == 2)
                value = Integer.parseInt(contentArray[0]);
        } else {
            if (currentAttributesBean != null) {
                ISceneSettingFunctionDatumSet.CallBackBean callBackBean = currentAttributesBean.getCallBackBean();
                if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                    ISceneSettingFunctionDatumSet.SetupCallBackBean setupCallBackBean = (ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean;
                    value = Integer.parseInt(setupCallBackBean.getTargetValue());
                }

            }
        }

        InputValueDialog.newInstance(new InputValueDialog.ButtonClickListener() {
            @Override
            public void onDone(DialogFragment dialog, String txt) {
                if (TextUtils.isEmpty(txt)) {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onToastContent("输入内容不能为空");
                    return;
                }
                if (txt.startsWith("+"))
                    txt = txt.substring(1);
                int value;
                try {
                    value = Integer.parseInt(txt);
                } catch (Exception e) {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onToastContent("输入内容不合法");
                    return;
                }

                if (value < setupBean.getMin() || value > setupBean.getMax()) {
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onToastContent("输入值不在范围之内");
                    return;
                }
                dialog.dismissAllowingStateLoss();
                if (menuItemsDialog != null && menuItemsData != null) {
                    menuItemsDialog.updateItem(menuItemsData.getId(), String.format("%s %s", txt, setupBean.getUnit()));
                } else {
                    ISceneSettingFunctionDatumSet.SetupCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.SetupCallBackBean("==", txt, setupBean, "");
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                }
            }

            @Override
            public void cancel() {

            }
        }).setEditValue(String.valueOf(value))
                .setInputType(InputValueDialog.InputType.numberSigned)
                .setTitle(attributesBean.getDisplayName())
                .setValueRange(rangeTxt)
                .show(fragmentManager, "");
    }

    private void showSeekbarWithClickDialog(DeviceTemplateBean.AttributesBean attributesBean, MenuItemsData menuItemsData) {
        int max = attributesBean.getSetup().getMax().intValue();
        int min = attributesBean.getSetup().getMin().intValue();
        int step = attributesBean.getSetup().getStep().intValue();
        String unit = attributesBean.getSetup().getUnit();
        int value = 0;
        if (menuItemsData != null) {
            //条件时输入值 content=value+单位 以空格分隔
            String content = menuItemsData.getContent();
            String[] contentArray = content.split(" ");
            if (contentArray.length == 2)
                value = Integer.parseInt(contentArray[0]);
        } else {
            if (currentAttributesBean != null) {
                ISceneSettingFunctionDatumSet.CallBackBean callBackBean = currentAttributesBean.getCallBackBean();
                if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                    value = Integer.parseInt(((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getTargetValue());
                }

            }
        }

        SeekbarWithClickDialog.SeekBarWithClickBuilder seekBarWithClickBuilder = new SeekbarWithClickDialog.SeekBarWithClickBuilder();
        seekBarWithClickBuilder.setTitle(attributesBean.getDisplayName()).setValue(value).setRange(min, max, step).setUnit(unit).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof SeekbarWithClickDialog.SeekBarWithClickParams) {
                    SeekbarWithClickDialog.SeekBarWithClickParams params = (SeekbarWithClickDialog.SeekBarWithClickParams) dialog.getParams();
                    int selectData = params.getSelectValue();
                    if (menuItemsData != null && menuItemsDialog != null)
                        menuItemsDialog.updateItem(menuItemsData.getId(), String.format("%s %s", selectData, unit));
                    else {
                        ISceneSettingFunctionDatumSet.SetupCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.SetupCallBackBean("==", String.valueOf(selectData), attributesBean.getSetup(), "");
                        if (choosePropertyValueListener != null)
                            choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                    }

                }
            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(fragmentManager, "");

    }


    private void showGradientSeekbarDialog(DeviceTemplateBean.AttributesBean attributesBean) {
        int max = attributesBean.getSetup().getMax().intValue();
        int min = attributesBean.getSetup().getMin().intValue();
        String unit = attributesBean.getSetup().getUnit();
        String tips = "";
        String leftTxt = "";
        String rightTxt = "";
        if ("SAT".equalsIgnoreCase(unit)) {
            tips = "饱和度调节仅在彩光模式下有效";
            leftTxt = "灰白";
            rightTxt = "鲜艳";
        } else if ("k".equalsIgnoreCase(unit)) {
            tips = "色温调节仅在白光模式下有效";
            leftTxt = "高冷";
            rightTxt = "低暖";
        }
        int value = 0;
        if (currentAttributesBean != null) {
            ISceneSettingFunctionDatumSet.CallBackBean callBackBean = currentAttributesBean.getCallBackBean();
            if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                value = Integer.parseInt(((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getTargetValue());
            }

        }
        GradientSeekbarDialog.GradientSeekbarBuilder gradientSeekbarBuilder = new GradientSeekbarDialog.GradientSeekbarBuilder();
        gradientSeekbarBuilder.setTitle(attributesBean.getDisplayName()).setValue(value).setRange(min, max).setTips(tips).setRLTxt(leftTxt, rightTxt).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof GradientSeekbarDialog.GradientSeekBarParams) {
                    GradientSeekbarDialog.GradientSeekBarParams params = (GradientSeekbarDialog.GradientSeekBarParams) dialog.getParams();
                    int selectData = params.getSelectValue();

                    ISceneSettingFunctionDatumSet.SetupCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.SetupCallBackBean("==", String.valueOf(selectData), attributesBean.getSetup(), "");
                    if (choosePropertyValueListener != null)
                        choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                }
            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(fragmentManager, "");

    }


    private void showSeekbarDialog(DeviceTemplateBean.AttributesBean attributesBean, MenuItemsData menuItemsData) {
        int max = attributesBean.getSetup().getMax().intValue();
        int min = attributesBean.getSetup().getMin().intValue();
        String unit = attributesBean.getSetup().getUnit();
        int value = 0;
        if (menuItemsData != null) {
            //条件时输入值 content=value+单位 以空格分隔
            String content = menuItemsData.getContent();
            String[] contentArray = content.split(" ");
            if (contentArray.length == 2)
                value = Integer.parseInt(contentArray[0]);
        } else {
            if (currentAttributesBean != null) {
                ISceneSettingFunctionDatumSet.CallBackBean callBackBean = currentAttributesBean.getCallBackBean();
                if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
                    value = Integer.parseInt(((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getTargetValue());
                }
            }
        }
        int rightImgId = 0;
        if (attributesBean.getDisplayName().endsWith("亮度")) {
            rightImgId = R.drawable.icon_light;
        }
        SeekbarDialog.SeekbarDialogBuilder seekbarDialogBuilder = new SeekbarDialog.SeekbarDialogBuilder();
        seekbarDialogBuilder.setTitle(attributesBean.getDisplayName()).setRightImageRes(rightImgId).setValue(value).setRange(min, max).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof SeekbarDialog.SeekBarDialogParams) {
                    SeekbarDialog.SeekBarDialogParams params = (SeekbarDialog.SeekBarDialogParams) dialog.getParams();
                    int selectData = params.getSelectValue();
                    if (menuItemsDialog != null && menuItemsData != null) {
                        menuItemsDialog.updateItem(menuItemsData.getId(), String.format("%s %s", selectData, unit));
                    } else {
                        ISceneSettingFunctionDatumSet.SetupCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.SetupCallBackBean("==", String.valueOf(selectData), attributesBean.getSetup(), "");
                        if (choosePropertyValueListener != null)
                            choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                    }
                }

            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(fragmentManager, "");

    }

    /**
     * 单选弹窗
     */
    private void showRadioDialog(DeviceTemplateBean.AttributesBean attributesBean, boolean cancel) {
        ArrayList<RadioItemData> datas = new ArrayList<>();
        String rightValue = "";
        ISceneSettingFunctionDatumSet.CallBackBean callBackBean = null;
        if (currentAttributesBean != null)
            callBackBean = currentAttributesBean.getCallBackBean();

        if (attributesBean.getValue() != null) {
            if (callBackBean instanceof ISceneSettingFunctionDatumSet.ValueCallBackBean) {
                DeviceTemplateBean.AttributesBean.ValueBean valueBean = ((ISceneSettingFunctionDatumSet.ValueCallBackBean) callBackBean).getValueBean();
                rightValue = valueBean.getValue();
            }
            List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = attributesBean.getValue();
            if (valueBeans != null && valueBeans.size() > 0) {
                for (int i = 0; i < valueBeans.size(); i++) {
                    DeviceTemplateBean.AttributesBean.ValueBean bean = valueBeans.get(i);
                    RadioItemData radioItemData;
                    if (TextUtils.equals(rightValue, bean.getValue()))
                        radioItemData = new RadioItemData(String.valueOf(i), bean.getDisplayName(), true);
                    else
                        radioItemData = new RadioItemData(String.valueOf(i), bean.getDisplayName(), false);
                    datas.add(radioItemData);
                }
                if (TextUtils.isEmpty(rightValue))
                    datas.get(0).setCheck(true);
            }
        } else if (attributesBean.getBitValue() != null) {
            if (callBackBean instanceof ISceneSettingFunctionDatumSet.BitValueCallBackBean)
                rightValue = String.valueOf(((ISceneSettingFunctionDatumSet.BitValueCallBackBean) callBackBean).getBitValueBean().getValue());

            List<DeviceTemplateBean.AttributesBean.BitValueBean> bitValue = attributesBean.getBitValue();
            if (bitValue != null && bitValue.size() > 0) {
                for (int i = 0; i < bitValue.size(); i++) {
                    DeviceTemplateBean.AttributesBean.BitValueBean bean = bitValue.get(i);
                    RadioItemData radioItemData;
                    if (TextUtils.equals(rightValue, String.valueOf(bean.getValue())))
                        radioItemData = new RadioItemData(String.valueOf(i), bean.getDisplayName(), true);
                    else
                        radioItemData = new RadioItemData(String.valueOf(i), bean.getDisplayName(), false);
                    datas.add(radioItemData);
                }
                if (TextUtils.isEmpty(rightValue))
                    datas.get(0).setCheck(true);
            }
        } else {
            //event 类型
            RadioItemData radioItemData;
            if (callBackBean != null) {
                radioItemData = new RadioItemData("0", attributesBean.getDisplayName(), true);
            } else
                radioItemData = new RadioItemData("0", attributesBean.getDisplayName(), false);
            datas.add(radioItemData);
        }

        RadioItemsDialog.RadioItemsBuilder radioItemsBuilder = new RadioItemsDialog.RadioItemsBuilder();
        radioItemsBuilder.setTitle(attributesBean.getDisplayName()).setClickItemCancel(cancel).setData(datas).setOperateListener(new AylaBaseDialog.OnOperateListener() {
            @Override
            public void onClickRight(@NonNull AylaBaseDialog dialog) {
                if (dialog.getParams() instanceof RadioItemsDialog.RadioItemsParams) {
                    RadioItemsDialog.RadioItemsParams params = (RadioItemsDialog.RadioItemsParams) dialog.getParams();
                    RadioItemData selectData = params.getSelectData();
                    if (selectData != null) {
                        if (attributesBean.getValue() != null && attributesBean.getValue().size() > Integer.parseInt(selectData.getId())) {
                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = attributesBean.getValue().get(Integer.parseInt(selectData.getId()));
                            ISceneSettingFunctionDatumSet.ValueCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.ValueCallBackBean(valueBean);
                            if (choosePropertyValueListener != null)
                                choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                        } else if (attributesBean.getBitValue() != null && attributesBean.getBitValue().size() > Integer.parseInt(selectData.getId())) {
                            DeviceTemplateBean.AttributesBean.BitValueBean valueBean = attributesBean.getBitValue().get(Integer.parseInt(selectData.getId()));
                            ISceneSettingFunctionDatumSet.BitValueCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.BitValueCallBackBean(valueBean);
                            if (choosePropertyValueListener != null)
                                choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                        } else {
                            ISceneSettingFunctionDatumSet.EventCallBackBean resultCallback = new ISceneSettingFunctionDatumSet.EventCallBackBean(attributesBean);
                            if (choosePropertyValueListener != null)
                                choosePropertyValueListener.onUpdate(currentPos, resultCallback);
                        }
                    } else {
                        if (currentAttributesBean != null)
                            currentAttributesBean.setCallBackBean(null);
                        if (choosePropertyValueListener != null)
                            choosePropertyValueListener.onUpdate(currentPos, null);
                    }

                }
            }

            @Override
            public void onClickLeft(@NonNull AylaBaseDialog dialog) {

            }
        }).show(fragmentManager, "");
    }

    /**
     * 清空数据 ，防止rx引起内存泄漏
     */
    public void clear() {
        mCompositeDisposable.clear();
        fragmentManager = null;
        choosePropertyValueListener = null;
        condition = null;
        groupAction = null;
        action = null;
        menuItemsDialog = null;
        currentAttributesBean = null;
    }
}