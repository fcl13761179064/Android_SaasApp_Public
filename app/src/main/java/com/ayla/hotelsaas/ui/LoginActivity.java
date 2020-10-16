package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.present.LoginPresenter;
import com.ayla.hotelsaas.mvp.view.LoginView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.blankj.utilcode.util.AppUtils;

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
    public void onViewClicked(View v) {
        if (FastClickUtils.isDoubleClick()) {
            return;
        }
        mPresenter.login();
        SoftInputUtil.hideSysSoftInput(LoginActivity.this);
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
        keepLoginBtnNotOver(rl_root_view, ll_content_view);
        //触摸外部，键盘消失
        rl_root_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftIntPutUtils.closeKeyboard(LoginActivity.this);
                return false;
            }
        });
        edit_password.addTextChangedListener(edtWatcher);
        edite_count.addTextChangedListener(edtWatcher);
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

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(mainActivity);
            }
        });

    }


    /**
     * 保持登录按钮始终不会被覆盖
     *
     * @param root
     * @param subView
     */
    private void keepLoginBtnNotOver(final View root, final View subView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                // 获取root在窗体的可视区域
                root.getWindowVisibleDisplayFrame(rect);
                // 获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                // 若不可视区域高度大于200，则键盘显示,其实相当于键盘的高度
                if (rootInvisibleHeight > 200) {
                    // 显示键盘时
                    int srollHeight = rootInvisibleHeight - (root.getHeight() - subView.getHeight()) - SoftIntPutUtils.getNavigationBarHeight(root.getContext());
                    if (srollHeight > 0) {//当键盘高度覆盖按钮时
                        root.scrollTo(0, srollHeight + 10);
                    }
                } else {
                    // 隐藏键盘时
                    root.scrollTo(0, 0);
                }
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
        SharePreferenceUtils.saveString(LoginActivity.this, Constance.SP_Login_Token, data.getAuthToken());
        SharePreferenceUtils.saveString(LoginActivity.this, Constance.SP_Refresh_Token, data.getRefreshToken());
        Intent mainActivity = new Intent(this, WorkRoomManageActivity.class);
        startActivity(mainActivity);
        finish();

    }

    @Override
    public void errorShake(int type, int CycleTimes, String msg) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppUtils.exitApp();
    }
}
