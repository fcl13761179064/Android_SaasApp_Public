package com.ayla.hotelsaas.ui;

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
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.widget.ScrollerPickerDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import java.util.Arrays;

/**
 * 场景创建，选择执行功能点的页面，范围选择情况，支持操作符的选择
 */
public class SceneSettingFunctionDatumSetBigValueSelectFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {

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

    @Override
    protected void initView(View view) {
        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        Double max = attributesBean.getSetup().getMax();
        Double min = attributesBean.getSetup().getMin();
        Double step = attributesBean.getSetup().getStep();
        String unit = attributesBean.getSetup().getUnit();

        binding.tvUnit.setText(unit);
        String format;
        if (step == 1) {
            format = String.format("%s值范围 %s ~ %s", unit, min.intValue(), max.intValue());
        } else {
            format = String.format("%s值范围 %s ~ %s", unit, min, max);
        }
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
        binding.tvValue.setText(step == 1 ? String.valueOf((int) target_value) : String.valueOf(target_value));
    }

    @Override
    protected void initListener() {
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
        binding.rlDeviceRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt)) {
                                    CustomToast.makeText(getContext(), "输入不能为空", R.drawable.ic_toast_warming);
                                    return;
                                }
                                int value = Integer.parseInt(txt);
                                Double max = attributesBean.getSetup().getMax();
                                Double min = attributesBean.getSetup().getMin();
                                if (value < min || value > max) {
                                    CustomToast.makeText(getContext(), "输入不在范围之内", R.drawable.ic_toast_warming);
                                } else {
                                    dialog.dismissAllowingStateLoss();
                                    binding.tvValue.setText(String.valueOf(value));
                                }
                            }
                        }).setTitle("输入目标值").setEditHint("输入目标值")
                        .setEditValue(binding.tvValue.getText().toString())
                        .setInputType(ValueChangeDialog.InputType.numberSigned)
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
        return new SetupCallBackBean(optionValue, binding.tvValue.getText().toString(), attributesBean.getSetup());
    }
}
