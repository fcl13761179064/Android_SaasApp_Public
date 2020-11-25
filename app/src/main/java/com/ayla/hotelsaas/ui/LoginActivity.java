package com.ayla.hotelsaas.ui;

import android.content.Intent;
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
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.mvp.present.LoginPresenter;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.blankj.utilcode.util.RegexUtils;

import java.net.UnknownHostException;

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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {

    }

    @OnClick({R.id.submitBtn})
    public void handleLogin() {
        String account = edite_count.getText().toString();
        String password = edit_password.getText().toString();

        if (TextUtils.isEmpty(account)) {
            CustomToast.makeText(MyApplication.getContext(), "登录账号不能为空", R.drawable.ic_toast_warming).show();
            errorShake(1, 2, "");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            CustomToast.makeText(MyApplication.getContext(), "登陆密码不能为空", R.drawable.ic_toast_warming).show();
            errorShake(2, 2, "");
            return;
        }
        if (!RegexUtils.isEmail(account) && !RegexUtils.isMobileSimple(account)) {
            CustomToast.makeText(MyApplication.getContext(), R.string.account_error, R.drawable.ic_toast_warming).show();
            return;
        }
        SoftInputUtil.hideSysSoftInput(LoginActivity.this);
        mPresenter.login(account, password);
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
        //触摸外部，键盘消失
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
    public void loginSuccess(User data) {
        SharePreferenceUtils.saveString(LoginActivity.this, Constance.SP_Login_Token, data.getAuthToken());
        SharePreferenceUtils.saveString(LoginActivity.this, Constance.SP_Refresh_Token, data.getRefreshToken());
        Intent mainActivity = new Intent(this, ProjectListActivity.class);
        startActivity(mainActivity);
        finish();

    }

    @Override
    public void loginFailed(Throwable throwable) {
        String msg = "未知错误";
        if (throwable instanceof RxjavaFlatmapThrowable) {
            msg = ((RxjavaFlatmapThrowable) throwable).getMsg();
        }
        if (throwable instanceof UnknownHostException) {
            msg = getString(R.string.request_not_connect);
        }
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
            edite_count.startAnimation(mShakeAnimation);
        } else if (type == 2) {
            edit_password.startAnimation(mShakeAnimation);
        } else {
            edite_count.startAnimation(mShakeAnimation);
            edit_password.startAnimation(mShakeAnimation);
        }
    }
}
