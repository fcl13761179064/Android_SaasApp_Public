package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.UpgradeDownloadService;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.mvp.present.PersonCenterPresenter;
import com.ayla.hotelsaas.mvp.view.PersonCenterView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.utils.UpgradeUnifiedCode;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonCenterActivity extends BaseMvpActivity<PersonCenterView, PersonCenterPresenter> implements PersonCenterView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_default_username)
    TextView tv_default_username;
    @BindView(R.id.tv_version_code)
    TextView tv_version_code;
    @BindView(R.id.rl_log_out)
    RelativeLayout rl_log_out;
    @BindView(R.id.rl_help_center)
    RelativeLayout rl_help_center;
    @BindView(R.id.v_new_vesion)
    View v_new_vesion;

    @Override
    protected void onResume() {
        super.onResume();
        handleVersionTip();
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
        appBar.setCenterText("个人中心");
        tv_version_code.setText(String.format("Version:%s %s", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        mPresenter.getUserInfo();
    }

    private void handleVersionTip() {
        VersionUpgradeBean versionUpgradeInfo = Constance.getVersionUpgradeInfo();
        if (versionUpgradeInfo != null) {
            v_new_vesion.setVisibility(View.VISIBLE);
        } else {
            v_new_vesion.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initListener() {
        rl_help_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonCenterActivity.this, HelpCenterActivity.class);
                intent.putExtra("pageTitle", "帮助中心");
                startActivity(intent);
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
    public void getUserInfoFail(Throwable throwable) {
    }

    @Override
    public void getUserInfoFailSuccess(PersonCenter personCenter) {
        tv_default_username.setText(personCenter.getFullName());
    }

    @Override
    public void onVersionResult(VersionUpgradeBean baseResult) {
        Constance.saveVersionUpgradeInfo(baseResult);
        handleVersionTip();
        if (baseResult != null) {
            UpgradeUnifiedCode.handleUpgrade(this, baseResult);
        } else {
            CustomToast.makeText(this, "当前已是最新版本", R.drawable.ic_toast_warming);
        }
    }

    @Override
    public void onVersionCheckFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @OnClick(R.id.rl_check_upgrade)
    public void handleCheckUpgrade() {
        if (UpgradeDownloadService.isWorking) {
            return;
        }
        mPresenter.fetchVersionUpdate();
    }
}
