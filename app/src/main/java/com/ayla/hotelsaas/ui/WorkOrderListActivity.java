package com.ayla.hotelsaas.ui;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;

import butterknife.BindView;

public class WorkOrderListActivity extends BaseMvpActivity{

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;


    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_order_list_activity;
    }

    @Override
    protected void initView() {

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


    }

    @Override
    protected void initListener() {

    }
}
