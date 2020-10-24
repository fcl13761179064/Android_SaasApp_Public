package com.ayla.hotelsaas.fragment;

import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;

public class TestFragment extends BaseMvpFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.empty_test_fragment_page;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }
}
