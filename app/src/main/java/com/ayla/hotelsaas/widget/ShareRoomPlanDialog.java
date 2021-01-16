package com.ayla.hotelsaas.widget;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.databinding.DialogRoomPlanShareBinding;

public class ShareRoomPlanDialog extends DialogFragment {
    DialogRoomPlanShareBinding binding;

    public static ShareRoomPlanDialog newInstance(String msg) {

        Bundle args = new Bundle();
        args.putString("msg", msg);
        ShareRoomPlanDialog fragment = new ShareRoomPlanDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClipboardManager cm  = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("share", getArguments().getString("msg"));
        cm.setPrimaryClip(clipData);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogRoomPlanShareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
        binding.tvContent.setText(getArguments().getString("msg"));
    }

}
