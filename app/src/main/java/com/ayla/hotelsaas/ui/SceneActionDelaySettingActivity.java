package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.widget.NumberPicker;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import butterknife.BindView;

/**
 * 场景设备的延时动作设置
 */
public class SceneActionDelaySettingActivity extends BaseMvpActivity {
    @BindView(R.id.np_minute)
    NumberPicker numberPickerMinute;
    @BindView(R.id.np_second)
    NumberPicker numberPickerSecond;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_action_delay_setting;
    }

    @Override
    protected void initView() {
        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                String result;
                if (value < 10) {
                    result = "0" + value;
                } else {
                    result = String.valueOf(value);
                }
                return result;
            }
        };
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(59);
        numberPickerMinute.setFormatter(formatter);
        numberPickerMinute.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPickerSecond.setMinValue(0);
        numberPickerSecond.setMaxValue(59);
        numberPickerSecond.setFormatter(formatter);
        numberPickerSecond.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void appBarRightTvClicked() {
        int minute = numberPickerMinute.getValue();
        int second = numberPickerSecond.getValue();
        setResult(RESULT_OK, new Intent().putExtra("seconds", minute * 60 + second));
        finish();
    }
}
