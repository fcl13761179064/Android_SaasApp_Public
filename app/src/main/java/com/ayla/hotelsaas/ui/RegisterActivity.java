package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.mvp.present.RegisterPresenter;
import com.ayla.hotelsaas.mvp.view.RegisterView;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseMvpActivity<RegisterView, RegisterPresenter> implements RegisterView {
    @BindView(R.id.et_user_name)
    EditText et_user_name;
    @BindView(R.id.registeraccount)
    EditText registeraccount;
    @BindView(R.id.registerPass)
    EditText registerPass;
    @BindView(R.id.register_submitBtn)
    Button register_submitBtn;
    @BindView(R.id.tv_also_account)
    TextView tv_also_account;
    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_error_show)
    TextView tv_error_show;


    private TranslateAnimation mShakeAnimation;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
    }


    @OnClick({R.id.tv_also_account, R.id.register_submitBtn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_also_account:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.register_submitBtn:
                register();
                SoftIntPutUtils.closeKeyboard(RegisterActivity.this);
                break;
            default:
                break;
        }
    }


    @Override
    protected void initListener() {
        registerPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()))) {
                    register();
                    SoftInputUtil.hideSysSoftInput(registerPass);
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected RegisterPresenter initPresenter() {
        return new RegisterPresenter();
    }

    @Override
    public void RegistSuccess(Boolean data) {
        CustomToast.makeText(this, "注册成功", R.drawable.ic_success);
        finish();
    }

    @Override
    public void registerFailed(String msg) {
        errorShake(0, 2, msg);
    }

    private void errorShake(int type, int CycleTimes, String msg) {
        tv_error_show.setVisibility(View.VISIBLE);
        tv_error_show.setText(msg);

        // CycleTimes动画重复的次数
        if (null == mShakeAnimation) {
            mShakeAnimation = new TranslateAnimation(0, 10, 0, 0);
            mShakeAnimation.setInterpolator(new CycleInterpolator(CycleTimes));
            mShakeAnimation.setDuration(1000);
            mShakeAnimation.setRepeatMode(Animation.REVERSE);//设置反方向执行
        }
        if (type == 1) {
            et_user_name.startAnimation(mShakeAnimation);
        } else if (type == 2) {
            registeraccount.startAnimation(mShakeAnimation);
        } else if (type == 3) {
            registerPass.startAnimation(mShakeAnimation);
        }
    }

    private void register() {
        tv_error_show.setVisibility(View.INVISIBLE);

        String userName = et_user_name.getText().toString();
        String account = registeraccount.getText().toString();
        String password = registerPass.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            String s = "用户名不能为空";
            errorShake(1, 2, s);
            return;
        }
        if (TextUtils.isEmpty(account)) {
            String s = "账号不能为空";
            errorShake(2, 2, s);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            String s = "密码不能为空";
            errorShake(3, 2, s);
            return;
        }
        if (password.length() < 6) {
            String s = "密码长度不能小于6位";
            errorShake(3, 2, s);
            return;
        }
        if (RegexUtils.isMobileSimple(account)) {
            mPresenter.register(userName, account, password);
        } else {
            String s = getString(R.string.account_error);
            errorShake(3, 2, s);
        }
    }
}
