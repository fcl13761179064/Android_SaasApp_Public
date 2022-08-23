package com.ayla.hotelsaas.widget.common_dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TimePicker;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class TimePickerDialog extends DialogFragment {
    private TimePicker mTimePicker;
    private TimePickerDialog.DoneCallback doneCallback;

    public static TimePickerDialog newInstance(DoneCallback doneCallback) {

        Bundle args = new Bundle();

        TimePickerDialog fragment = new TimePickerDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.v_picker, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTimePicker = view.findViewById(R.id.tp);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        view.findViewById(R.id.tv_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneCallback != null) {
                    doneCallback.onDone(TimePickerDialog.this, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = getResources().getDisplayMetrics().widthPixels;
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);
    }

    private int hour, minute;

    public TimePickerDialog setHour(@IntRange(from = 0, to = 23) int hour) {
        this.hour = hour;
        return this;
    }

    public TimePickerDialog setMinute(@IntRange(from = 0, to = 59) int minute) {
        this.minute = minute;
        return this;
    }

    public interface DoneCallback {
        void onDone(TimePickerDialog dialog, int hour, int minute);
    }
}
