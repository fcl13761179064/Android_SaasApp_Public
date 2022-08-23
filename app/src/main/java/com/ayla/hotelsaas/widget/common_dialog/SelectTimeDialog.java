package com.ayla.hotelsaas.widget.common_dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ayla.hotelsaas.R;
import com.blankj.utilcode.util.SizeUtils;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 场景设备的延时动作设置
 */
public class SelectTimeDialog extends Dialog {
    private Boolean showHour = true;
    private Boolean showMin = true;
    private Boolean showSecond = false;
    private OnConfirmSelectTimeListener onConfirmSelectTimeListener;
    private int hourValue;
    private int minValue;
    private int secondValue;
    private String title;

    public void setOnConfirmSelectTimeListener(OnConfirmSelectTimeListener listener) {
        onConfirmSelectTimeListener = listener;
    }

    public SelectTimeDialog(@NonNull Context context) {
        super(context);
    }

    public SelectTimeDialog(Context context, Boolean showHour, Boolean showMin, Boolean showSecond) {
        super(context);
        this.showHour = showHour;
        this.showMin = showMin;
        this.showSecond = showSecond;
    }

    public SelectTimeDialog(Context context, String title, Boolean showHour, Boolean showMin, Boolean showSecond) {
        super(context);
        this.showHour = showHour;
        this.showMin = showMin;
        this.showSecond = showSecond;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_set_time, null);
        if (!TextUtils.isEmpty(title)) {
            ((TextView) view.findViewById(R.id.title)).setText(title);
        }
        WheelView<String> hourView = view.findViewById(R.id.np_hour);
        hourView.setVisibility(showHour ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.np_hour_divider).setVisibility((showHour && (showMin || showSecond)) ? View.VISIBLE : View.GONE);
        WheelView<String> minuteView = view.findViewById(R.id.np_minute);
        minuteView.setVisibility(showMin ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.np_minute_divider).setVisibility((showMin && showSecond) ? View.VISIBLE : View.GONE);
        WheelView<String> secondView = view.findViewById(R.id.np_second);
        secondView.setVisibility(showSecond ? View.VISIBLE : View.GONE);
        initPickerView(hourView, 0, 23);
        initPickerView(minuteView, 0, 59);
        initPickerView(secondView, 0, 59);
        view.findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.tv_confirm).setOnClickListener(v -> {
            if (onConfirmSelectTimeListener != null) {
                onConfirmSelectTimeListener.onSelectTime(hourValue, minValue, secondValue);
            }
            dismiss();
        });
        hourView.setOnItemSelectedListener((wheelView, data, position) -> {
            try {
                hourValue = Integer.parseInt(data);
            } catch (Exception e) {
                Log.e("SelectTimeDialog", "小时数据异常");
            }
        });
        minuteView.setOnItemSelectedListener((wheelView, data, position) -> {
            try {
                minValue = Integer.parseInt(data);
            } catch (Exception e) {
                Log.e("SelectTimeDialog", "分钟数据异常");
            }
        });
        secondView.setOnItemSelectedListener((wheelView, data, position) -> {
            try {
                secondValue = Integer.parseInt(data);
            } catch (Exception e) {
                Log.e("SelectTimeDialog", "秒数据异常");
            }
        });
        setContentView(view);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.gravity = Gravity.BOTTOM;
        getWindow().setBackgroundDrawable(null);
        getWindow().setAttributes(attributes);
        getWindow().setWindowAnimations(R.style.FromBottomShow);
    }

    /**
     * show 之后调用
     *
     * @param hour
     * @param min
     * @param second
     */
    public void setDefaultValue(int hour, int min, int second) {
        WheelView<String> hourView = findViewById(R.id.np_hour);
        WheelView<String> minuteView = findViewById(R.id.np_minute);
        WheelView<String> secondView = findViewById(R.id.np_second);
        hourValue = hour;
        minValue = min;
        secondValue = second;
        hourView.setSelectedItemPosition(hour);
        minuteView.setSelectedItemPosition(min);
        secondView.setSelectedItemPosition(second);
    }

    public void setMinSecondValue(int min, int second) {
        WheelView<String> minuteView = findViewById(R.id.np_minute);
        WheelView<String> secondView = findViewById(R.id.np_second);
        minValue = min;
        secondValue = second;
        minuteView.setSelectedItemPosition(min);
        secondView.setSelectedItemPosition(second);
    }

    public void setHourMinValue(int hour, int min) {
        WheelView<String> hourView = findViewById(R.id.np_hour);
        WheelView<String> minuteView = findViewById(R.id.np_minute);
        hourValue = hour;
        minValue = min;
        minuteView.setSelectedItemPosition(min);
        hourView.setSelectedItemPosition(hour);
    }


    protected void initPickerView(WheelView<String> picker, int minV, int maxV) {
        List<String> data = new ArrayList<>();
        for (int i = minV; i <= maxV; i++) {
            data.add(String.format(Locale.CHINA, "%02d", i));
        }
        picker.setCyclic(true);
        picker.setCurved(false);
        picker.setVisibleItems(3);
        picker.setData(data);
        picker.setNormalItemTextColor(Color.parseColor("#A9A9A9"));
        picker.setSelectedItemTextColor(Color.parseColor("#3C3C3E"));
        picker.setTextSize(SizeUtils.sp2px(20));
        picker.setLineSpacing(SizeUtils.dp2px(16.0f));
    }

    //    @Override
//    protected void initListener() {
//        tv_confire.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int minute = numberPickerMinute.getValue();
//                int second = numberPickerSecond.getValue();
//
//                int seconds = minute * 60 + second;
//                if (seconds == 0) {
//                    CustomToast.makeText(ActionDelaySettingActivity.this, "无效的延时设置", R.drawable.ic_toast_warming);
//                    return;
//                }
//                setResult(RESULT_OK, new Intent().putExtras(getIntent()).putExtra("seconds", seconds));
//                finish();
//            }
//        });
//
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//
//            /**
//             * Called when a view has been clicked.
//             *
//             * @param v The view that was clicked.
//             */
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
    public interface OnConfirmSelectTimeListener {
        void onSelectTime(int hour, int min, int second);
    }
}
