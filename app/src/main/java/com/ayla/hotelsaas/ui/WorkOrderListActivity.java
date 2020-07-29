package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkOrderAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class WorkOrderListActivity extends BaseMvpActivity<WorkOrderView, WorkOrderPresenter> implements WorkOrderView {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private WorkOrderAdapter mAdapter;


    @Override
    public void refreshUI() {
        appBar.setLeftText("退出");
        appBar.setCenterText("我的工单");
        super.refreshUI();
    }


    @Override
    protected WorkOrderPresenter initPresenter() {
        return new WorkOrderPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_order_list_activity;
    }

    @Override
    protected void initView() {
        //是否在刷新的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenRefresh(true);
        //是否在加载的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenLoading(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mAdapter = new WorkOrderAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
    }


    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(WorkOrderListActivity.this, RoomOrderListActivity.class);
                    WorkOrderBean.ResultListBean resultListBean = mAdapter.getData().get(position);
                    intent.putExtra("work_order", (Serializable) resultListBean);
                    startActivity(intent);
                }
            }
        });


        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null != mAdapter.getData()) {
                    mAdapter.removeAllFooterView();
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.loadFistPage();
                }
                mRefreshLayout.setEnableLoadMore(true);

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage();
                }
            }
        });
        mRefreshLayout.autoRefresh();//自动刷新

    }

    @Override
    protected void mExitApp() {
        finish();
        SharePreferenceUtils.remove(this, Constance.SP_Login_Token);
        MyApplication.getInstance().setUserEntity(null);
        Intent intent = new Intent(WorkOrderListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void loadDataSuccess(WorkOrderBean data) {
        final List<WorkOrderBean.ResultListBean> resultList = data.getResultList();
        if (resultList.isEmpty()) {
            if (mAdapter.getData().isEmpty()) {
                mAdapter.setEmptyView(R.layout.empty_work_order);
            }
            final View inflate = LayoutInflater.from(this).inflate(R.layout.room_root_view, null);
            mAdapter.setFooterView(inflate);
            mRefreshLayout.setEnableLoadMore(false);
        } else {
            mAdapter.addData(resultList);
        }
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

}