package com.ayla.hotelsaas.widget;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;

public class SceneNameSetDialog extends DialogFragment {
    public static SceneNameSetDialog newInstance(String currentSceneName, DoneCallback doneCallback) {

        Bundle args = new Bundle();
        args.putString("currentSceneName", currentSceneName);
        SceneNameSetDialog fragment = new SceneNameSetDialog();
        fragment.setArguments(args);
        fragment.doneCallback = doneCallback;
        return fragment;
    }

    private DoneCallback doneCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_scene_set_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editText = view.findViewById(R.id.et_dsn);
        editText.setText(getArguments().getString("currentSceneName"));
        view.findViewById(R.id.v_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = editText.getText().toString();
                if (doneCallback != null) {
                    doneCallback.onDone(SceneNameSetDialog.this, msg);
                }
            }
        });
        view.findViewById(R.id.v_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    public interface DoneCallback {
        void onDone(DialogFragment dialog, String txt);
    }
}
