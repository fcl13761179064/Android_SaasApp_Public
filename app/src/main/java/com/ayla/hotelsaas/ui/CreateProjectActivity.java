package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class CreateProjectActivity extends BaseMvpActivity {
    @BindViews({R.id.cb_01, R.id.cb_02, R.id.cb_03, R.id.cb_04, R.id.cb_05, R.id.cb_06})
    List<CheckBox> mCheckBoxList;
    @BindView(R.id.ed_name)
    EditText mEditText;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_project;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.cb_01, R.id.cb_02, R.id.cb_03, R.id.cb_04, R.id.cb_05, R.id.cb_06})
    void handleCbClicked(View view) {
        switch (view.getId()) {
            case R.id.cb_01:
            case R.id.cb_02:
            case R.id.cb_03:
            case R.id.cb_04:
                for (int i = 0; i < 4; i++) {
                    CheckBox checkBox = mCheckBoxList.get(i);
                    if (checkBox.getId() != view.getId()) {
                        checkBox.setChecked(false);
                    }
                }
                break;
            case R.id.cb_05:
            case R.id.cb_06:
                for (int i = 4; i < 6; i++) {
                    CheckBox checkBox = mCheckBoxList.get(i);
                    if (checkBox.getId() != view.getId()) {
                        checkBox.setChecked(false);
                    }
                }
                break;
        }
    }

    @Override
    protected void appBarRightTvClicked() {
        String newName = mEditText.getText().toString();
        if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
            CustomToast.makeText(MyApplication.getContext(), "名称不能为空", R.drawable.ic_toast_warming).show();
            return;
        }
        boolean check = false;
        for (int i = 0; i < 4; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目行业", R.drawable.ic_toast_warming).show();
            return;
        }
        check = false;
        for (int i = 4; i < 6; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目类型", R.drawable.ic_toast_warming).show();
            return;
        }
        CustomToast.makeText(MyApplication.getContext(), "111111", R.drawable.ic_toast_warming).show();
    }
}
