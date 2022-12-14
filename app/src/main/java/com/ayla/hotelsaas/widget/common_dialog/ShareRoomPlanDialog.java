package com.ayla.hotelsaas.widget.common_dialog;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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

import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.databinding.DialogRoomPlanShareBinding;
import com.ayla.hotelsaas.ui.activities.ProjectMainActivity;

public class ShareRoomPlanDialog extends DialogFragment {
    DialogRoomPlanShareBinding binding;

    public static ShareRoomPlanDialog newInstance(String msg, WorkOrderBean.ResultListBean bean) {

        Bundle args = new Bundle();
        args.putString("msg", msg);
        args.putSerializable("bean",bean);
        ShareRoomPlanDialog fragment = new ShareRoomPlanDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
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
        binding.vDone.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                startActivity(new Intent(getContext(), ProjectMainActivity.class).putExtra("bean",getArguments().getSerializable("bean")));
            }
        });
        binding.tvContent.setText(getArguments().getString("msg"));
    }
}
