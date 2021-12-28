package com.ayla.hotelsaas.base;


import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.NetWorkChangReceiver;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ScreenUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author fancunlei
 * BaseActivity
 * 基础Activity
 */
public abstract class BasicActivity extends AppCompatActivity implements NetworkUtils.OnNetworkStatusChangedListener {

    protected final String TAG = getClass().getSimpleName();
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkUtils.registerNetworkStatusChangedListener(this);
        int layoutId = getLayoutId();
        View layoutView = getLayoutView();
        if (layoutView != null) {
            setContentView(layoutView);
        } else {
            setContentView(layoutId);
        }
        unbinder = ButterKnife.bind(this);
        initAppbar();
        initView();
        initListener();
        if (!ScreenUtils.isFullScreen(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BarUtils.setStatusBarColor(this, Color.TRANSPARENT);
                BarUtils.setStatusBarLightMode(this, true);
            } else {
                BarUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_statusbar_compare_color));
            }
        }
    }


    @Deprecated
    protected abstract int getLayoutId();

    protected abstract View getLayoutView();

    protected abstract void initView();

    protected abstract void initListener();

    private void initAppbar() {
        final View appbarRoot = findViewById(R.id.appbar_root_rl_ff91090);
        if (appbarRoot != null) {
            View leftIV = appbarRoot.findViewById(R.id.iv_left);
            if (leftIV != null && !leftIV.hasOnClickListeners()) {
                leftIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        appBarLeftIvClicked();
                    }
                });
            }
            View leftTV = appbarRoot.findViewById(R.id.tv_left);
            if (leftTV != null && !leftTV.hasOnClickListeners()) {
                leftTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftTvClicked();
                    }
                });
            }
            View rightIV = appbarRoot.findViewById(R.id.iv_right);
            if (rightIV != null && !rightIV.hasOnClickListeners()) {
                rightIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightIvClicked();
                    }
                });
            }
            View rightTv = appbarRoot.findViewById(R.id.tv_right);
            if (rightTv != null && !rightTv.hasOnClickListeners()) {
                rightTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightTvClicked();
                    }
                });
            }

            RadioGroup rg_tab_group = appbarRoot.findViewById(R.id.rg_tab_group);
            RadioButton rd_select = appbarRoot.findViewById(R.id.rd_select);
            RadioButton rd_select_two = appbarRoot.findViewById(R.id.rd_select_two);
            rg_tab_group.check(R.id.rd_select);
            rd_select.setSelected(true);
            if (rg_tab_group != null && !rg_tab_group.hasOnClickListeners()) {
                rg_tab_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.rd_select: {
                                if (!rd_select_two.isChecked()) {
                                    rd_select.setSelected(true);
                                    rd_select.setTextColor(getResources().getColor(R.color.white,getTheme()));
                                    rd_select_two.setTextColor(getResources().getColor(R.color.color_333333,getTheme()));
                                    rd_select_two.setSelected(false);
                                    rb_all_data();
                                }
                                break;
                            }
                            case R.id.rd_select_two: {
                                if (!rd_select.isChecked()) {
                                    rd_select.setSelected(false);
                                    rd_select_two.setSelected(true);
                                    rd_select_two.setTextColor(getResources().getColor(R.color.white));
                                    rd_select.setTextColor(getResources().getColor(R.color.color_333333));
                                    rb_bufen_data();
                                    break;
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    protected void rb_all_data() {

    }

    protected void rb_bufen_data() {

    }

    /**
     * appbar左侧图标点击事件
     */
    protected void appBarLeftIvClicked() {
        onBackPressed();
    }

    protected void appBarLeftTvClicked() {
        onBackPressed();
    }

    protected void appBarRightIvClicked() {

    }

    protected void appBarRightTvClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        NetworkUtils.unregisterNetworkStatusChangedListener(this);
    }

    @Override
    public void onDisconnected() {
        CustomToast.makeOnlyHaseText(this, "网络异常");
    }

    @Override
    public void onConnected(NetworkUtils.NetworkType networkType) {
    }
}

