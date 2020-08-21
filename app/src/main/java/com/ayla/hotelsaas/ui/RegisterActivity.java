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
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.mvp.present.RegisterPresenter;
import com.ayla.hotelsaas.mvp.view.RegisterView;
import com.ayla.hotelsaas.utils.AppManager;
import com.ayla.hotelsaas.utils.SoftInputUtil;
import com.ayla.hotelsaas.utils.SoftIntPutUtils;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;
import butterknife.OnClick;

public class RegisterActivity extends BaseMvpActivity<RegisterView, RegisterPresenter> implements RegisterView {
    @BindView(R.id.registeraccount)
    EditText registeraccount;
    @BindView(R.id.registerPass)
    EditText registerPass;
    @BindView(R.id.register_submitBtn)
    Button register_submitBtn;
    @BindView(R.id.tv_also_account)
    TextView tv_also_account;
    @BindView(R.id.rl_root_view)
    RelativeLayout rl_root_view;
    @BindView(R.id.ll_content_view)
    LinearLayout ll_content_view;
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
        appBar.setAppBarlineHider(false);
        keepLoginBtnNotOver(rl_root_view, ll_content_view);
        //触摸外部，键盘消失
        rl_root_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftIntPutUtils.closeKeyboard(RegisterActivity.this);
                return false;
            }
        });
    }


    @OnClick({R.id.tv_also_account,R.id.register_submitBtn})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.tv_also_account:
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.register_submitBtn:
                mPresenter.register();
                SoftIntPutUtils.closeKeyboard(RegisterActivity.this);
                break;
            default:
                break;
        }
    }


    @Override
    protected void initListener() {
        registerPass.addTextChangedListener(edtWatcher);
        registeraccount.addTextChangedListener(edtWatcher);
        registerPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction()))) {
                    mPresenter.register();
                    SoftInputUtil.hideSysSoftInput(RegisterActivity.this);
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
            tv_error_show.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(getAccount()) && !TextUtils.isEmpty(getPassword())) {
                register_submitBtn.setEnabled(true);
            } else {
                register_submitBtn.setEnabled(false);
            }
        }
    };

    @Override
    protected RegisterPresenter initPresenter() {
        return new RegisterPresenter();
    }


    @Override
    public String getAccount() {
        return registeraccount.getText().toString();
    }

    @Override
    public String getPassword() {
        return registerPass.getText().toString();
    }

    @Override
    public void loginSuccess(User data) {
        Intent mainActivity = new Intent(this, LoginActivity.class);
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
            registeraccount.startAnimation(mShakeAnimation);
        } else if (type == 2) {
            registerPass.startAnimation(mShakeAnimation);
        } else {
            registeraccount.startAnimation(mShakeAnimation);
            registerPass.startAnimation(mShakeAnimation);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppManager.getAppManager().AppExit(this);
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
}
