package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.WorkOrderAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.StatusBarUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;

public class WorkOrderListActivity extends BaseMvpActivity<WorkOrderView, WorkOrderPresenter> implements WorkOrderView {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private WorkOrderAdapter mAdapter;


    @Override
    protected void setStatusBar() {
        //用来设置整体下移，状态栏沉浸
        //StatusBarToolUlti.setRootViewFitsSystemWindows(this, false);
        //黑色字体
        //StatusBarToolUlti.setStatusBarDarkTheme(this, true);
        StatusBarUtil.setTransparent(this);
    }

    @Override
    protected WorkOrderPresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_order_list_activity;
    }

    @Override
    protected void initView() {
        initToolbarTitle(R.string.main_todo);
        //initToolbar(R.layout.);
        //是否在刷新的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenRefresh(true);
        //是否在加载的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenLoading(true);

        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new WorkOrderAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setEmptyView(R.layout.empty_work_order);


    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(WorkOrderListActivity.this, RoomOrderListActivity.class);
                    WorkOrderBean todoItem = (WorkOrderBean) adapter.getData().get(position);
                    intent.putExtra("voucher", (Serializable) todoItem);
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 0x101);
                }
            }
        });

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null != mAdapter.getData()) {
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.loadFistPage("1");
                }

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });
        mRefreshLayout.autoRefresh();//自动刷新

    }

    @Override
    public void loadDataSuccess(ArrayList<WorkOrderBean> data) {
        mAdapter.addData(data);
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

}
