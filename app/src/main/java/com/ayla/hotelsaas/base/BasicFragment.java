package com.ayla.hotelsaas.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract class BasicFragment extends Fragment {
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
        initData();
    }

    // 初始化UI，setContentView等
    protected abstract int getLayoutId();

    protected abstract void initView(View view);

    protected abstract void initListener();

    protected abstract void initData();

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
}
