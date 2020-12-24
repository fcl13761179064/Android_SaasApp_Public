package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.NumberPicker;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面，范围选择情况，支持操作符的选择
 */
public class SceneSettingFunctionDatumSetRangeWithOptionFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {
    @BindView(R.id.np_1)
    NumberPicker mNumberPicker1;
    @BindView(R.id.np_2)
    NumberPicker mNumberPicker2;

    private String[] optionsName = new String[]{"小于", "等于", "大于"};
    private String[] optionsValue = new String[]{"<", "==", ">"};


    public static SceneSettingFunctionDatumSetRangeWithOptionFragment newInstance(DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingFunctionDatumSetRangeWithOptionFragment fragment = new SceneSettingFunctionDatumSetRangeWithOptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_function_datum_set__option_range;
    }

    private DeviceTemplateBean.AttributesBean attributesBean;

    @Override
    protected void initView(View view) {
        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        Double max = attributesBean.getSetup().getMax();
        Double min = attributesBean.getSetup().getMin();
        Double step = attributesBean.getSetup().getStep();
        String unit = attributesBean.getSetup().getUnit();


        mNumberPicker1.setDisplayedValues(optionsName);
        mNumberPicker1.setMinValue(0);
        mNumberPicker1.setMaxValue(optionsName.length - 1);

        int count = 0;
        for (double i = min; i <= max; i += step) {
            count++;
        }
        String[] values = new String[count];
        realValues = new String[count];
        for (int i = 0; i < count; i++) {
            Double value = min + i * step;
            String l_v;
            if (step % 1 == 0) {
                l_v = String.valueOf(value.intValue());
            } else {
                l_v = String.valueOf(value);
            }
            values[i] = String.format("%s%s", l_v, unit);
            realValues[i] = l_v;
        }
        mNumberPicker2.setDisplayedValues(values);
        mNumberPicker2.setMinValue(0);
        mNumberPicker2.setMaxValue(values.length - 1);

        boolean editMode = getActivity().getIntent().getBooleanExtra("editMode", false);
        BaseSceneBean.DeviceCondition condition = (BaseSceneBean.DeviceCondition) getActivity().getIntent().getSerializableExtra("condition");
        int defaultPicker1Index = (mNumberPicker1.getMinValue() + mNumberPicker1.getMaxValue()) / 2;
        int defaultPicker2Index = (mNumberPicker2.getMinValue() + mNumberPicker2.getMaxValue()) / 2;
        if (editMode && condition != null) {
            for (int i = 0; i < optionsValue.length; i++) {
                if (TextUtils.equals(condition.getOperator(), optionsValue[i])) {
                    defaultPicker1Index = i;
                    break;
                }
            }
            try {
                defaultPicker2Index = (int) ((Double.parseDouble(condition.getRightValue()) - min) / step);
            } catch (Exception ignored) {

            }
        }
        mNumberPicker1.setValue(defaultPicker1Index);
        mNumberPicker2.setValue(defaultPicker2Index);
    }

    String[] realValues;

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    public CallBackBean getDatum() {
        String option = optionsValue[mNumberPicker1.getValue()];
        String realValue = realValues[mNumberPicker2.getValue()];

        return new SetupCallBackBean(option, realValue, attributesBean.getSetup());
    }
}
