package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.present.LoginPresenter;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.StatusBarUtil;
import com.ayla.hotelsaas.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseMvpActivity<LoginView, LoginPresenter> implements LoginView {
    @BindView(R.id.editCount)
    EditText edite_count;
    @BindView(R.id.editPass)
    EditText edit_password;
    @BindView(R.id.tv_switch)
    TextView tvSwitch;
    @BindView(R.id.submitBtn)
    Button submitBtn;
    @BindView(R.id.tv_error_show)
    TextView tv_error_show;

    private TranslateAnimation mShakeAnimation;


    @Override
    protected void setStatusBar() {
        //用来设置整体下移，状态栏沉浸
        //StatusBarToolUlti.setRootViewFitsSystemWindows(this, false);
        //黑色字体
        //StatusBarToolUlti.setStatusBarDarkTheme(this, true);
      //  StatusBarUtil.setTransparent(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        if (BuildConfig.DEBUG) {
            tvSwitch.setVisibility(View.VISIBLE);
            if (Constance.isNetworkDebug) {
                tvSwitch.setText("测");
            } else {
                tvSwitch.setText("正");
            }
        }
    }

    @OnClick({R.id.submitBtn, R.id.tv_switch})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
                mPresenter.login();
                break;
            case R.id.tv_switch:
                showProgress("切换环境中");
                Constance.isNetworkDebug = !Constance.isNetworkDebug;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constance.isNetworkDebug) {
                            tvSwitch.setText("测");
                            ToastUtils.showShortToast("切换到测试网络");
                        } else {
                            tvSwitch.setText("正");
                            ToastUtils.showShortToast("切换到正式网络环境");
                        }
                        hideProgress();
                    }
                }, 1000);
                break;
            default:
                break;
        }
    }


    @Override
    protected void initListener() {
        edit_password.addTextChangedListener(edtWatcher);
        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()))) {
                    mPresenter.login();
                    SoftInputUtil.hideSysSoftInput(LoginActivity.this);
                    return true;
                }
                return false;
            }
        });

    }

    private TextWatcher edtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            tv_error_show.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(getAccount()) && !TextUtils.isEmpty(getPassword())) {
                submitBtn.setEnabled(true);
            } else {
                submitBtn.setEnabled(false);
            }
        }
    };

    @Override
    protected LoginPresenter initPresenter() {
        return new LoginPresenter();
    }


    @Override
    public String getAccount() {
        return edite_count.getText().toString();
    }

    @Override
    public String getPassword() {
        return edit_password.getText().toString();
    }

    @Override
    public void loginSuccess(User data) {
        SharePreferenceUtils.saveString(LoginActivity.this,Constance.SP_Login_Token,data.getAuthToken());
        Intent mainActivity = new Intent(this, WorkOrderListActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void errorShake(int type, int CycleTimes) {
        tv_error_show.setVisibility(View.VISIBLE);
        // CycleTimes动画重复的次数
        if (null == mShakeAnimation) {
            mShakeAnimation = new TranslateAnimation(0, 10, 0, 0);
            mShakeAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
            mShakeAnimation.setDuration(1000);
            mShakeAnimation.setRepeatMode(Animation.REVERSE);//设置反方向执行
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
