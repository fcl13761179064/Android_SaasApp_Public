package com.ayla.hotelsaas.widget;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.databinding.LayoutScrollerPickDialogBinding;

import java.util.ArrayList;
import java.util.List;

public class ScrollerPickerDialog extends DialogFragment {
    LayoutScrollerPickDialogBinding binding;

    List<String> data = new ArrayList<>();

    private int defaultIndex = -1;//默认选中的下标

    private Callback callback;

    private @DrawableRes
    int iconRes;

    private String title, subTitle;

    public static ScrollerPickerDialog newInstance() {

        Bundle args = new Bundle();

        ScrollerPickerDialog fragment = new ScrollerPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = LayoutScrollerPickDialogBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.np1.setMinValue(0);
        binding.np1.setMaxValue(data.size() - 1);
        binding.np1.setDisplayedValues(data.toArray(new String[]{}));

        binding.textView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });


        binding.textView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (callback != null) {
                    callback.onCallback(binding.np1.getValue());
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

    public ScrollerPickerDialog setData(List<String> data) {
        this.data = data;
        return this;
    }

    public ScrollerPickerDialog setIconRes(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }

    public ScrollerPickerDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ScrollerPickerDialog setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public ScrollerPickerDialog setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
        return this;
    }

    public ScrollerPickerDialog setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    public interface Callback {
        void onCallback(int index);
    }
}
