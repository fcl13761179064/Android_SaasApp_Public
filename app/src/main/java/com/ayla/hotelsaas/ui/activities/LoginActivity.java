package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.mvp.present.LoginPresenter;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.utils.UpgradeUnifiedCode;
import com.blankj.utilcode.util.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseMvpActivity<LoginView, LoginPresenter> implements LoginView {
    @BindView(R.id.editCount)
    EditText edite_count;
    @BindView(R.id.editPass)
    EditText edit_password;
    @BindView(R.id.submitBtn)
    Button submitBtn;
    @BindView(R.id.tv_error_show)
    TextView tv_error_show;
    @BindView(R.id.rl_root_view)
    RelativeLayout rl_root_view;
    @BindView(R.id.ll_content_view)
    LinearLayout ll_content_view;
    @BindView(R.id.tv_forgit)
    TextView tv_forgit;
    @BindView(R.id.tv_register)
    TextView tv_register;

    private TranslateAnimation mShakeAnimation;

    private VersionUpgradeBean upgradeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upgradeBean = (VersionUpgradeBean) getIntent().getSerializableExtra("upgrade");
        if (upgradeBean != null) {
            UpgradeUnifiedCode.handleUpgrade(this, upgradeBean);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        String account = SharePreferenceUtils.getString(LoginActivity.this, ConstantValue.SP_Login_account, "");
        edite_count.setText(account);
    }

    @OnClick({R.id.submitBtn})
    public void handleLogin() {
        String account = edite_count.getText().toString();
        String password = edit_password.getText().toString();

        if (TextUtils.isEmpty(account)) {
            CustomToast.makeText(MyApplication.getContext(), "????????????????????????", R.drawable.ic_toast_warning);
            errorShake(1, 2, "");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.makeText(MyApplication.getContext(), "????????????????????????", R.drawable.ic_toast_warning);
            errorShake(2, 2, "");
            return;
        }
        if (!RegexUtils.isEmail(account) && !RegexUtils.isMobileSimple(account)) {
            CustomToast.makeText(this, R.string.account_error, R.drawable.ic_toast_warning);
            return;
        }
        if (SoftInputUtil.isOpen()){
            SoftInputUtil.hideSysSoftInput(submitBtn);
        }
        if (upgradeBean == null) {
            mPresenter.checkVersion();
        } else {
            mPresenter.login(account, password);
        }
    }

    @Override
    protected void initListener() {
        tv_forgit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(LoginActivity.this, ForgitPassWordActivity.class);
                startActivity(mainActivity);
            }
        });
        //???????????????????????????
        rl_root_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftIntPutUtils.closeKeyboard(LoginActivity.this);
                return false;
            }
        });
        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()))) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mainActivity);
            }
        });

    }

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void loginSuccess(User data, String account) {
        SharePreferenceUtils.saveString(LoginActivity.this, ConstantValue.SP_Login_Token, data.getAuthToken());
        SharePreferenceUtils.saveString(LoginActivity.this, ConstantValue.SP_Refresh_Token, data.getRefreshToken());
        SharePreferenceUtils.saveString(LoginActivity.this, ConstantValue.SP_Login_account,account);
       Intent mainActivity = new Intent(this, ProjectListActivity.class);
        mainActivity.putExtra("upgrade", upgradeBean);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void loginFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        errorShake(0, 2, TempUtils.getLocalErrorMsg(throwable));
    }

    @Override
    public void checkVersionFailed(Throwable throwable) {
        hideProgress();
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
    }

    @Override
    public void checkVersionSuccess(VersionUpgradeBean versionUpgradeBean) {
        if (versionUpgradeBean != null) {
            if (versionUpgradeBean.getIsForce() != 0) {
                hideProgress();
                UpgradeUnifiedCode.handleUpgrade(this, versionUpgradeBean);
                return;
            }
        }

        upgradeBean = versionUpgradeBean;

        String account = edite_count.getText().toString();
        String password = edit_password.getText().toString();

        mPresenter.login(account, password);
    }

    private void errorShake(int type, int CycleTimes, String msg) {
        tv_error_show.setVisibility(View.VISIBLE);
        tv_error_show.setText(msg);
        // CycleTimes?????????????????????
        if (null == mShakeAnimation) {
            mShakeAnimation = new TranslateAnimation(0, 10, 0, 0);
            mShakeAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
            mShakeAnimation.setDuration(1000);
            mShakeAnimation.setRepeatMode(Animation.REVERSE);//?????????????????????
        }
        if (type == 1) {
            edite_count.startAnimation(mShakeAnimation);
        } else if (type == 2) {
            edit_password.startAnimation(mShakeAnimation);
        } else {
            edite_count.startAnimation(mShakeAnimation);
            edit_password.startAnimation(mShakeAnimation);
        }
    }
}
