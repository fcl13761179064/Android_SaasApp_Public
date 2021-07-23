package com.ayla.hotelsaas.fragment;

import android.view.View;
import android.widget.RadioButton;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.widget.RoundProgressBar;

import butterknife.BindView;

public class TestFragment extends BaseMvpFragment {

    @BindView(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.empty_test_fragment_page;
    }

    @Override
    protected void initView(View view) {
        roundProgressBar.refreshDrawableState();
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
