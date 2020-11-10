package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;

/**
 * 酒店信息页面
 */
public class ProjectDetailFragment extends BaseMvpFragment {
    public static ProjectDetailFragment newInstance() {

        Bundle args = new Bundle();

        ProjectDetailFragment fragment = new ProjectDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_project_detail;
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
}
