package com.ayla.hotelsaas.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class CommonDialog extends DialogFragment {
    private static String mTitle;

    public static CommonDialog newInstance(DoneCallback doneCallback, String title) {

        Bundle args = new Bundle();
        CommonDialog fragment = new CommonDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        mTitle =title;
        return fragment;
    }

    private DoneCallback doneCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.common_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.v_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = view.findViewById(R.id.et_dsn);
                String msg = editText.getText().toString();
                if (doneCallback != null) {
                    doneCallback.onDone(CommonDialog.this, msg);
                }
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        TextView tv_dialog_title = view.findViewById(R.id.tv_dialog_title);
        tv_dialog_title.setText(mTitle);
    }

    public interface DoneCallback {
        void onDone(DialogFragment dialog, String txt);
    }
}
