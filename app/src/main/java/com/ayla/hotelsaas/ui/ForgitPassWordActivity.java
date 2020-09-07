package com.ayla.hotelsaas.ui;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.ForgitPresenter;
import com.ayla.hotelsaas.mvp.view.ForgitView;
import com.ayla.hotelsaas.utils.AppManager;
import com.ayla.hotelsaas.utils.PregnancyUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.ayla.hotelsaas.widget.AppBar;

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


    private TranslateAnimation mShakeAnimation;
    private CountDownTimer mTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgit;
    }

    @Override
    protected void initView() {
        appBar.setAppBarlineHider(false);
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
                mPresenter.modifyforgit();
                SoftIntPutUtils.closeKeyboard(ForgitPassWordActivity.this);
                break;
            default:
                break;
        }
    }


    @Override
    protected void initListener() {
        et_user_name.addTextChangedListener(edtWatcher);
        rt_yanzhengma.addTextChangedListener(edtWatcher);
        tv_send_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftIntPutUtils.closeKeyboard(ForgitPassWordActivity.this);
                String userName = getUserName();
                if (TextUtils.isEmpty(userName)) {
                    CustomToast.makeText(MyApplication.getContext(), "手机号不能为空", R.drawable.ic_toast_warming).show();
                    return;
                }else if (!PregnancyUtil.checkPhoneNum(userName)){
                    CustomToast.makeText(MyApplication.getContext(), "手机格式错误", R.drawable.ic_toast_warming).show();
                    return;
                }
                tv_send_code.setClickable(false);
                mTimer.start();
                mPresenter.send_sms();
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
            tv_error_show.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(getUserName()) && !TextUtils.isEmpty(getYanzhengMa())) {
                register_submitBtn.setEnabled(true);
            } else {
                register_submitBtn.setEnabled(false);
            }
        }
    };

    @Override
    protected ForgitPresenter initPresenter() {
        return new ForgitPresenter();
    }


    @Override
    public String getUserName() {
        return et_user_name.getText().toString();
    }

    @Override
    public String getYanzhengMa() {
        return rt_yanzhengma.getText().toString();
    }


    @Override
    public void errorShake(int type, int CycleTimes, String code) {
        tv_error_show.setVisibility(View.VISIBLE);
        if ("171000".equals(code)) {
            tv_error_show.setText("用户已存在");
        } else if (TextUtils.isEmpty(code)) {

        } else {
            tv_error_show.setText("服务异常");
        }

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
    public void RegistSuccess(Boolean data) {
        CustomToast.makeText(this, "注册成功", R.drawable.ic_success).show();
        finish();
    }

    @Override
    public void sendCodeSuccess(Boolean data) {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().AppExit(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
