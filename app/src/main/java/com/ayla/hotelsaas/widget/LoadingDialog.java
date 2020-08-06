package com.ayla.hotelsaas.widget;




import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class LoadingDialog extends DialogFragment {
    public static LoadingDialog newInstance(String msg) {

        Bundle args = new Bundle();
        args.putString("msg", msg);
        LoadingDialog fragment = new LoadingDialog();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        setCancelable(false);
        return inflater.inflate(R.layout.dialog_laoding, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.tv);
        textView.setText(getArguments().getString("msg"));
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (null != dialog) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
            lp.height = (ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.width = (ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.dimAmount = 0;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            if (window != null) {
                window.setLayout(lp.width, lp.height);
            }
        }
    }
}
