package com.ayla.hotelsaas.fragment;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkOrderAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;
import com.ayla.hotelsaas.ui.RoomOrderListActivity;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class WorkOrderListFragment extends BaseMvpFragment<WorkOrderView, WorkOrderPresenter> implements WorkOrderView {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private WorkOrderAdapter mAdapter;

    @Override
    protected WorkOrderPresenter initPresenter() {
        return new WorkOrderPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.work_order_list_activity;
    }


    @Override
    protected void initView(View view) {
        //是否在刷新的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenRefresh(true);
        //是否在加载的时候禁止列表的操作
        mRefreshLayout.setDisableContentWhenLoading(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerview.setHasFixedSize(true);
        recyclerview.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 3, R.color.all_bg_color));
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
                    Intent intent = new Intent(
                            getContext(), RoomOrderListActivity.class);
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
    protected void initData() {

    }

    @Override
    public void loadDataSuccess(WorkOrderBean data) {
        final List<WorkOrderBean.ResultListBean> resultList = data.getResultList();
        if (resultList != null) {
            if (resultList.isEmpty()) {
                if (mAdapter.getData().isEmpty()) {
                    mAdapter.setEmptyView(R.layout.empty_work_order);
                }

                if (mAdapter.getData().size() > 10) {
                    final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.room_root_view, null);
                    mAdapter.setFooterView(inflate);
                }
                mRefreshLayout.setEnableLoadMore(false);
            } else {
                mAdapter.addData(resultList);
            }
        } else {
            final View inflate = LayoutInflater.from(getContext()).inflate(R.layout.room_root_view, null);
            mAdapter.setFooterView(inflate);
            mRefreshLayout.setEnableLoadMore(false);
        }
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

}