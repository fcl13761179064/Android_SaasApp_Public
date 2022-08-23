package com.ayla.hotelsaas.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.databinding.FragmentSceneFunctionDatumSetBigValueSelectBinding;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.widget.common_dialog.ScrollerPickerDialog;
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog;
import com.blankj.utilcode.util.NumberUtils;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 场景创建，选择执行功能点的页面，范围选择情况，支持操作符的选择
 */
public class  SceneSettingFunctionDatumSetBigValueSelectFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {

    private String[] optionsName = new String[]{"小于", "等于", "大于"};
    private String[] optionsValue = new String[]{"<", "==", ">"};

    FragmentSceneFunctionDatumSetBigValueSelectBinding binding;

    public static SceneSettingFunctionDatumSetBigValueSelectFragment newInstance(boolean isCondition, DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putBoolean("isCondition", isCondition);
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingFunctionDatumSetBigValueSelectFragment fragment = new SceneSettingFunctionDatumSetBigValueSelectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = FragmentSceneFunctionDatumSetBigValueSelectBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    private DeviceTemplateBean.AttributesBean attributesBean;

    private int fractionDigits;

    @Override
    protected void initView(View view) {
        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        Double max = attributesBean.getSetup().getMax();
        Double min = attributesBean.getSetup().getMin();
        Double step = attributesBean.getSetup().getStep();
        String unit = attributesBean.getSetup().getUnit();

        binding.tvUnit.setText(unit);
        String format;

        fractionDigits = (int) Math.log10(1 / step);

        format = String.format("%s值范围 %s ~ %s", unit, NumberUtils.format(min, fractionDigits, false), NumberUtils.format(max, fractionDigits, false));

        Log.d(TAG, "initView: " + format);
        binding.tvDesc.setText(format);

        int count = 0;
        for (double i = min; i <= max; i += step) {
            count++;
        }

        boolean editMode = getActivity().getIntent().getBooleanExtra("editMode", false);
        BaseSceneBean.DeviceCondition condition = (BaseSceneBean.DeviceCondition) getActivity().getIntent().getSerializableExtra("condition");
        String operator = optionsName[optionsName.length / 2];
        double target_value = min + step * (int) (count / 2);
        if (editMode && condition != null) {
            for (int i = 0; i < optionsValue.length; i++) {
                if (TextUtils.equals(condition.getOperator(), optionsValue[i])) {
                    operator = optionsName[i];
                    break;
                }
            }
            try {
                target_value = Double.parseDouble(condition.getRightValue());
            } catch (Exception ignored) {

            }
        }

        binding.tvDeviceName.setText(operator);
        binding.tvValue.setText(NumberUtils.format(target_value, fractionDigits, false));
    }

    @Override
    protected void initListener() {
        boolean isCondition = getArguments().getBoolean("isCondition");
        if (isCondition) {
            binding.ivOptionArrow.setVisibility(View.VISIBLE);
            binding.rlOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ScrollerPickerDialog.newInstance()
                            .setData(Arrays.asList(optionsName))
                            .setCallback(new ScrollerPickerDialog.Callback() {
                                @Override
                                public void onCallback(int index) {
                                    binding.tvDeviceName.setText(optionsName[index]);
                                }
                            })
                            .show(getChildFragmentManager(), "dialog");
                }
            });
        } else {//如果是动作 ，就默认是 =  ，不能更改
            binding.ivOptionArrow.setVisibility(View.GONE);
        }

        binding.rlDeviceRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt)) {
                                    CustomToast.makeText(getContext(), "输入不能为空", R.drawable.ic_toast_warning);
                                    return;
                                }
                                double value = Double.parseDouble(txt);
                                Double max = attributesBean.getSetup().getMax();
                                Double min = attributesBean.getSetup().getMin();
                                Double step = attributesBean.getSetup().getStep();
                                if (value < min || value > max) {
                                    CustomToast.makeText(getContext(), "输入不在范围之内", R.drawable.ic_toast_warning);
                                } else if (BigDecimal.valueOf(value).remainder(BigDecimal.valueOf(step)).compareTo(BigDecimal.ZERO) != 0) {
                                    CustomToast.makeText(getContext(), "输入不在范围之内", R.drawable.ic_toast_warning);
                                } else {
                                    dialog.dismissAllowingStateLoss();
                                    binding.tvValue.setText(NumberUtils.format(value, fractionDigits, false));
                                }
                            }
                        }).setTitle("输入目标值").setEditHint("输入目标值")
                        .setEditValue(binding.tvValue.getText().toString())
                        .setInputType(fractionDigits == 0 ? ValueChangeDialog.InputType.numberSigned : ValueChangeDialog.InputType.numberSignedDecimal)
                        .show(getChildFragmentManager(), "dialog");
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public CallBackBean getDatum() {
        String optionValue = "==";
        String s = binding.tvDeviceName.getText().toString();
        for (int i = 0; i < optionsName.length; i++) {
            if (TextUtils.equals(optionsName[i], s)) {
                optionValue = optionsValue[i];
            }
        }
        return new SetupCallBackBean(optionValue, binding.tvValue.getText().toString(), attributesBean.getSetup(), binding.tvValue.getText().toString());
    }
}
