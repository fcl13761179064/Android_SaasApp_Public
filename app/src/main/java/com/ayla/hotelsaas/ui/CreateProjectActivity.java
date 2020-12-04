package com.ayla.hotelsaas.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.mvp.present.CreateProjectPresenter;
import com.ayla.hotelsaas.mvp.view.CreateProjectView;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class CreateProjectActivity extends BaseMvpActivity<CreateProjectView, CreateProjectPresenter> implements CreateProjectView {
    @BindViews({R.id.cb_01, R.id.cb_02, R.id.cb_03, R.id.cb_04, R.id.cb_05, R.id.cb_06})
    List<CheckBox> mCheckBoxList;
    @BindView(R.id.ed_name)
    TextView mEditText;

    @Override
    protected CreateProjectPresenter initPresenter() {
        return new CreateProjectPresenter();
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

    @OnClick(R.id.ed_name)
    void handleNameInput() {
        ValueChangeDialog
                .newInstance(new ValueChangeDialog.DoneCallback() {
                    @Override
                    public void onDone(DialogFragment dialog, String txt) {
                        if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                            CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warming);
                            return;
                        } else {
                            mEditText.setText(txt);
                        }
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .setEditValue(mEditText.getText().toString())
                .setTitle("项目名称")
                .setEditHint("输入项目名称")
                .setMaxLength(20)
                .show(getSupportFragmentManager(), "scene_name");
    }

    @Override
    protected void appBarRightTvClicked() {
        String newName = mEditText.getText().toString();
        int trade = 0, type = 0;
        if (TextUtils.isEmpty(newName) || newName.trim().isEmpty()) {
            CustomToast.makeText(MyApplication.getContext(), "名称不能为空", R.drawable.ic_toast_warming);
            return;
        }
        boolean check = false;
        for (int i = 0; i < 4; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                trade = i + 1;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目行业", R.drawable.ic_toast_warming);
            return;
        }
        check = false;
        for (int i = 4; i < 6; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                type = i - 3;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目类型", R.drawable.ic_toast_warming);
            return;
        }
        mPresenter.createProject(newName, trade, type);
    }

    @Override
    public void onFailed(Throwable throwable) {
        String msg = "创建失败";
        if (throwable instanceof RxjavaFlatmapThrowable) {
            msg = ((RxjavaFlatmapThrowable) throwable).getMsg();
        }
        CustomToast.makeText(this, msg, R.drawable.ic_toast_warming);
    }

    @Override
    public void onSuccess() {
        setResult(RESULT_OK);
        finish();
    }
}
