package com.ayla.hotelsaas.widget;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_laoding, null);
        Dialog loadingDailog = new Dialog(getContext(), R.style.MyDialogStyle);
        TextView msgText = (TextView) view.findViewById(R.id.tv);
        msgText.setText(getArguments().getString("msg"));
        loadingDailog.setContentView(view);
        loadingDailog.setCancelable(false);
        loadingDailog.setCanceledOnTouchOutside(false);
        return loadingDailog;
    }
}
