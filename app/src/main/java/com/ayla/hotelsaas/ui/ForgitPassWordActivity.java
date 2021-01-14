package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.ForgitPresenter;
import com.ayla.hotelsaas.mvp.view.ForgitView;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class ForgitPassWordActivity extends BaseMvpActivity<ForgitView, ForgitPresenter> implements ForgitView {
    @BindView(R.id.et_user_name)
    EditText et_user_name;
    @BindView(R.id.rt_yanzhengma)
    EditText rt_yanzhengma;
    @BindView(R.id.register_submitBtn)
    Button register_submitBtn;
    @BindView(R.id.tv_send_code)
    TextView tv_send_code;
    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_error_show)
    TextView tv_error_show;
    @BindView(R.id.et_new_password)
    EditText et_new_password;
    @BindView(R.id.ll_forgit_password)
    LinearLayout ll_forgit_password;
    @BindView(R.id.ll_new_password)
    LinearLayout ll_new_password;

    private TranslateAnimation mShakeAnimation;
    private CountDownTimer mTimer;
    private boolean is_forgit_password = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgit;
    }

    @Override
    protected void initView() {
        mTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_send_code.setText("重新发送" + millisUntilFinished / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                tv_send_code.setText("发送验证码");
                tv_send_code.setClickable(true);
                mTimer.cancel();
            }
        };
    }


    @OnClick({R.id.register_submitBtn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.register_submitBtn:

                if (is_forgit_password) {
                    modifyPassword();
                } else {
                    resetPassword(getUserName());
                }

                SoftIntPutUtils.closeKeyboard(ForgitPassWordActivity.this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initListener() {
        tv_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_error_show.setText(null);
                SoftIntPutUtils.closeKeyboard(ForgitPassWordActivity.this);
                String userName = getUserName();
                if (TextUtils.isEmpty(userName)) {
                    CustomToast.makeText(MyApplication.getContext(), "手机号不能为空", R.drawable.ic_toast_warming);
                    return;
                } else if (!RegexUtils.isMobileSimple(userName)) {
                    CustomToast.makeText(MyApplication.getContext(), "手机格式错误", R.drawable.ic_toast_warming);
                    return;
                }
                tv_send_code.setClickable(false);
                mTimer.start();
                String iphone_youxiang = getUserName();
                mPresenter.send_sms(iphone_youxiang);
            }
        });
    }

    private void modifyPassword() {
        tv_error_show.setText(null);

        String userName = getUserName();
        String yanzhengma = getYanzhengMa();
        if (TextUtils.isEmpty(userName)) {
            CustomToast.makeText(MyApplication.getContext(), "手机号不能为空", R.drawable.ic_toast_warming);
            errorShake(1, 2, "");
            return;
        }

        if (TextUtils.isEmpty(yanzhengma)) {
            CustomToast.makeText(MyApplication.getContext(), "验证码不能为空", R.drawable.ic_toast_warming);
            errorShake(2, 2, "");
            return;
        }
        if (RegexUtils.isMobileSimple(userName)) {
            mPresenter.modifyforgit(userName, yanzhengma);
        } else {
            CustomToast.makeText(this, R.string.account_forgit_password, R.drawable.ic_toast_warming);
        }
    }

    @Override
    protected ForgitPresenter initPresenter() {
        return new ForgitPresenter();
    }


    private String getUserName() {
        return et_user_name.getText().toString();
    }

    private String getYanzhengMa() {
        return rt_yanzhengma.getText().toString();
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
            rt_yanzhengma.startAnimation(mShakeAnimation);
        }
    }


    @Override
    public void sendCodeSuccess(Boolean data) {
        CustomToast.makeText(MyApplication.getInstance(), "发送成功", R.drawable.ic_success);
    }

    @Override
    public void sendCodeFailed(String msg) {
        mTimer.onFinish();
        errorShake(0, 2, msg);
    }

    @Override
    public void modifyPasswordSuccess(Boolean data) {
        is_forgit_password = false;
        ll_forgit_password.setVisibility(View.GONE);
        ll_new_password.setVisibility(View.VISIBLE);
        register_submitBtn.setText("重置密码");
    }

    @Override
    public void modifyPasswordFailed(String msg) {
        errorShake(0, 2, msg);
    }

    @Override
    public void resertPasswordSuccess(Boolean data) {
        CustomToast.makeText(MyApplication.getInstance(), "重置密码成功", R.drawable.ic_success);
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public String resetPassword() {
        return et_new_password.getText().toString();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    public void resetPassword(String phone) {
        tv_error_show.setText(null);

        String new_password = resetPassword();
        if (TextUtils.isEmpty(new_password)) {
            CustomToast.makeText(MyApplication.getContext(), "密码不能为空", R.drawable.ic_toast_warming);
            return;
        } else if (new_password.length() < 6) {
            CustomToast.makeText(MyApplication.getContext(), "密码至少为6位", R.drawable.ic_toast_warming);
            return;
        }
        mPresenter.reset_Password(phone, new_password);
    }
}
