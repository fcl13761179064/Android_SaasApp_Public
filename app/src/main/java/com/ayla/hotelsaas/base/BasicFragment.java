package com.ayla.hotelsaas.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.utils.ClickUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BasicFragment extends Fragment {
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshUI();
        initView(view);
        initListener();
        initData();
    }

    /**
     * 初始化App需要使用的工具类
     */
    protected void initParams() {

    }


    protected abstract void initData();

    // 初始化UI，setContentView等
    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected abstract void initListener();

    public void showProgress(String msg) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).showProgress(msg);
        }
    }

    public void showProgress() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).showProgress();
        }
    }

    public void refreshUI() {
        final View appbarRoot = getActivity().findViewById(R.id.appbar_root_rl_ff91090);
        if (appbarRoot != null) {
            View leftIV = appbarRoot.findViewById(R.id.iv_left);
            if (leftIV != null && !leftIV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(leftIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftIvClicked();
                    }
                });
            }
            View leftTV = appbarRoot.findViewById(R.id.tv_left);
            if (leftTV != null && !leftTV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(leftTV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarLeftTvClicked();
                    }
                });
            }
            View rightIV = appbarRoot.findViewById(R.id.iv_right);
            if (rightIV != null && !rightIV.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(rightIV, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightIvClicked();
                    }
                });
            }
            View rightTv = appbarRoot.findViewById(R.id.tv_right);
            if (rightTv != null && !rightTv.hasOnClickListeners()) {
                ClickUtils.applySingleDebouncing(rightTv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        appBarRightTvClicked();
                    }
                });
            }
        }
    }

    public void hideProgress() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).hideProgress();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        hideProgress();
    }


    /**
     * appbar左侧图标点击事件
     */
    protected void appBarLeftIvClicked() {
        getActivity().onBackPressed();
    }

    protected void appBarRightIvClicked() {

    }

    protected void appBarLeftTvClicked() {
        mExitApp();
    }

    protected void appBarRightTvClicked() {

    }

    protected void mExitApp() {
    }

}
