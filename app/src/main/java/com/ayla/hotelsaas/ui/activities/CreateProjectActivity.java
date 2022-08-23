package com.ayla.hotelsaas.ui.activities;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.events.RefreshDataEvent;
import com.ayla.hotelsaas.mvp.present.CreateProjectPresenter;
import com.ayla.hotelsaas.mvp.view.CreateProjectView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.common_dialog.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class CreateProjectActivity extends BaseMvpActivity<CreateProjectView, CreateProjectPresenter> implements CreateProjectView {
    @BindViews({R.id.cb_01, R.id.cb_03,  R.id.cb_05, R.id.cb_06})
    List<CheckBox> mCheckBoxList;
    @BindView(R.id.ed_name)
    TextView mEditText;
    @BindView(R.id.ll_03)
    LinearLayout ll_03;
    @BindView(R.id.ll_01)
    LinearLayout ll_01;
    @BindView(R.id.cb_01)
    AppCompatCheckBox cb_01;
    @BindView(R.id.cb_03)
    AppCompatCheckBox cb_03;
    private String project_type;

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
        project_type = getIntent().getStringExtra("project_type");
        if ("1".equals(project_type)){
            ll_03.setVisibility(View.GONE);
            ll_01.setVisibility(View.VISIBLE);
            cb_03.setChecked(false);
            cb_01.setChecked(true);
        }else {
            ll_03.setVisibility(View.VISIBLE);
            ll_01.setVisibility(View.GONE);
            cb_03.setChecked(true);
            cb_01.setChecked(false);
        }

    }

    @Override
    protected void initListener() {

    }

    @OnClick({R.id.cb_01, R.id.cb_03, R.id.cb_05, R.id.cb_06})
    void handleCbClicked(View view) {
        switch (view.getId()) {
            case R.id.cb_01:
            case R.id.cb_03:
                for (int i = 0; i < 2; i++) {
                    CheckBox checkBox = mCheckBoxList.get(i);
                    if (checkBox.getId() != view.getId()) {
                        checkBox.setChecked(false);
                    }
                }
                break;
            case R.id.cb_05:
            case R.id.cb_06:
                for (int i = 2; i < 4; i++) {
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
                            CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warning);
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
            CustomToast.makeText(MyApplication.getContext(), "名称不能为空", R.drawable.ic_toast_warning);
            return;
        }
        boolean check = false;
        for (int i = 0; i < 2; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                trade = i + 1;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目行业", R.drawable.ic_toast_warning);
            return;
        }
        check = false;
        for (int i = 2; i < 4; i++) {
            if (mCheckBoxList.get(i).isChecked()) {
                check = true;
                type = i - 1;
                break;
            }
        }
        if (!check) {
            CustomToast.makeText(MyApplication.getContext(), "请选择项目类型", R.drawable.ic_toast_warning);
            return;
        }
        mPresenter.createProject(newName, trade, type);
    }

    @Override
    public void onFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg("创建失败", throwable), R.drawable.ic_toast_warning);
    }

    @Override
    public void onSuccess() {
        EventBus.getDefault().post(new RefreshDataEvent());
        finish();
    }
}
