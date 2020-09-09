package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.mvp.present.PersonCenterPresenter;
import com.ayla.hotelsaas.mvp.view.PersonCenterView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import butterknife.BindView;

public class PersonCenterActivity extends BaseMvpActivity<PersonCenterView, PersonCenterPresenter> implements PersonCenterView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_default_username)
    TextView tv_default_username;
    @BindView(R.id.rl_log_out)
    RelativeLayout rl_log_out;
    @BindView(R.id.rl_help_center)
    RelativeLayout rl_help_center;



    @Override
    public void refreshUI() {
        appBar.setCenterText("个人中心");
        super.refreshUI();
    }

    @Override
    protected PersonCenterPresenter initPresenter() {
        return new PersonCenterPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_person_center;
    }

    @Override
    protected void initView() {
        mPresenter.getUserInfo();

    }

    @Override
    protected void initListener() {
        rl_help_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        rl_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlarmDialog
                        .newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                SharePreferenceUtils.remove(PersonCenterActivity.this, Constance.SP_Login_Token);
                                SharePreferenceUtils.remove(PersonCenterActivity.this, Constance.SP_Refresh_Token);
                                Intent intent = new Intent(PersonCenterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setContent(getResources().getString(R.string.sing_out))
                        .show(getSupportFragmentManager(), "");
            }
        });

    }

    @Override
    public void getUserInfoFail(String code, String msg) {
        CustomToast.makeText(MyApplication.getContext(), msg, R.drawable.ic_toast_warming).show();
    }

    @Override
    public void getUserInfoFailSuccess(PersonCenter personCenter) {
        tv_default_username.setText(personCenter.getFamilyName());

    }
}
