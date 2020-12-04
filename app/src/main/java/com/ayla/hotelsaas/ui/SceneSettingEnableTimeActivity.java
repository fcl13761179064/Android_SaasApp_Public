package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.widget.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 场景设置生效时间段
 * 进入时，带上enableTime {@link com.ayla.hotelsaas.localBean.BaseSceneBean.EnableTime}
 * 返回时，带上新的 enableTime {@link com.ayla.hotelsaas.localBean.BaseSceneBean.EnableTime}
 */
public class SceneSettingEnableTimeActivity extends BaseMvpActivity {
    @BindView(R.id.tv_start_time)
    TextView mStartTimeTextView;
    @BindView(R.id.tv_end_time)
    TextView mEndTimeTextView;
    @BindView(R.id.tv_repeat_day)
    TextView mRepeatDayTextView;
    @BindView(R.id.sc_enable)
    SwitchCompat mSwitchCompat;
    @BindView(R.id.ll_start_time)
    View startTimeView;
    @BindView(R.id.ll_end_time)
    View endTimeView;


    private final int REQUEST_CODE_REPEAT_DATA = 0X10;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting_enable_time;
    }

    BaseSceneBean.EnableTime enableTime;

    @Override
    protected void initView() {
        enableTime = (BaseSceneBean.EnableTime) getIntent().getSerializableExtra("enableTime");

        mSwitchCompat.setChecked(enableTime.isAllDay());
        mStartTimeTextView.setText(formatTime(enableTime.getStartHour(), enableTime.getStartMinute()));
        mEndTimeTextView.setText(formatTime(enableTime.getEndHour(), enableTime.getEndMinute()));
        mRepeatDayTextView.setText(formatRepeatDay(enableTime.getEnableWeekDay()));
        syncTimeClickAble(!enableTime.isAllDay());
    }

    @Override
    protected void initListener() {
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableTime.setAllDay(isChecked);
                syncTimeClickAble(!isChecked);
            }
        });
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        if (!enableTime.isAllDay()) {
            if (enableTime.getStartHour() > enableTime.getEndHour()) {
                CustomToast.makeText(this, "时间范围不能跨天", R.drawable.ic_toast_warming);
                return;
            }
            if (enableTime.getStartHour() == enableTime.getEndHour() && enableTime.getStartMinute() > enableTime.getEndMinute()) {
                CustomToast.makeText(this, "时间范围不能跨天", R.drawable.ic_toast_warming);
                return;
            }
            if (enableTime.getEndHour() == 0 && enableTime.getEndMinute() == 0) {
                CustomToast.makeText(this, "时间范围不能跨天", R.drawable.ic_toast_warming);
                return;
            }
        }
        setResult(RESULT_OK, new Intent().putExtra("enableTime", enableTime));
        finish();
    }

    @OnClick(R.id.ll_repeat_data)
    void handleJumpRepeatData() {
        Intent intent = new Intent(this, SceneSettingRepeatDataActivity.class);
        intent.putExtra("enable_position", enableTime.getEnableWeekDay());
        startActivityForResult(intent, REQUEST_CODE_REPEAT_DATA);
    }


    @OnClick(R.id.ll_start_time)
    void handleStartTime() {
        TimePickerDialog
                .newInstance(new TimePickerDialog.DoneCallback() {
                    @Override
                    public void onDone(TimePickerDialog dialog, int hour, int minute) {
                        enableTime.setStartHour(hour);
                        enableTime.setStartMinute(minute);
                        dialog.dismissAllowingStateLoss();
                        mStartTimeTextView.setText(formatTime(hour, minute));
                    }
                })
                .setHour(0)
                .setMinute(0)
                .show(getSupportFragmentManager(), "time");
    }

    @OnClick(R.id.ll_end_time)
    void handleEndTime() {
        TimePickerDialog
                .newInstance(new TimePickerDialog.DoneCallback() {
                    @Override
                    public void onDone(TimePickerDialog dialog, int hour, int minute) {
                        enableTime.setEndHour(hour);
                        enableTime.setEndMinute(minute);
                        dialog.dismissAllowingStateLoss();
                        mEndTimeTextView.setText(formatTime(hour, minute));
                    }
                })
                .setHour(0)
                .setMinute(0)
                .show(getSupportFragmentManager(), "time");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REPEAT_DATA && resultCode == RESULT_OK) {
            int[] enableWeekDay = data.getIntArrayExtra("enable_position");
            enableTime.setEnableWeekDay(enableWeekDay);
            mRepeatDayTextView.setText(formatRepeatDay(enableWeekDay));
        }
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    private String formatTime(int hour, int minute) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        String format = sf.format(calendar.getTime());
        return format;
    }

    private String formatRepeatDay(int[] days) {
        StringBuilder sb = new StringBuilder();
        if (days.length == 7) {
            sb.append("每天");
        } else {
            for (int i = 0; i < days.length; i++) {
                switch (days[i]) {
                    case 1:
                        sb.append("周一");
                        break;
                    case 2:
                        sb.append("周二");
                        break;
                    case 3:
                        sb.append("周三");
                        break;
                    case 4:
                        sb.append("周四");
                        break;
                    case 5:
                        sb.append("周五");
                        break;
                    case 6:
                        sb.append("周六");
                        break;
                    case 7:
                        sb.append("周日");
                        break;
                }
                if (i < days.length - 1) {
                    sb.append("/");
                }
            }
        }
        return sb.toString();
    }

    private void syncTimeClickAble(boolean enable) {
        startTimeView.setClickable(enable);
        endTimeView.setClickable(enable);
    }
}
