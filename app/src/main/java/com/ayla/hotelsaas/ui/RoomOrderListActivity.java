package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.RoomOrderListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.RoomOrderPresenter;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;
import com.ayla.hotelsaas.utils.AppManager;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

public class RoomOrderListActivity extends BaseMvpActivity<RoomOrderView, RoomOrderPresenter> implements RoomOrderView {

    @BindView(R.id.room_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.room_refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.appBar)
    AppBar appBar;
    private RoomOrderListAdapter mAdapter;
    private WorkOrderBean.ResultListBean mWork_order;

    @Override
    protected int getLayoutId() {
        return R.layout.room_order_list_activity;
    }

    @Override
    public void refreshUI() {
        mWork_order = (WorkOrderBean.ResultListBean) getIntent().getSerializableExtra("work_order");
        appBar.setCenterText(mWork_order.getTitle());
        super.refreshUI();
    }

    @Override
    protected void initView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RoomOrderListAdapter();
        final View view = View.inflate(this, R.layout.room_head_view, null);
        final TextView item_tv_name = view.findViewById(R.id.item_tv_name);
        final TextView item_room_srart_date = view.findViewById(R.id.item_room_srart_date);
        final TextView item_room_end_date = view.findViewById(R.id.item_room_end_date);
        final TextView item_work_status = view.findViewById(R.id.item_work_status);
        item_tv_name.setText(mWork_order.getTitle());
        item_room_srart_date.setText(mWork_order.getStartDate());
        item_room_end_date.setText(mWork_order.getEndDate());
        if (mWork_order.getConstructionStatus() == 1) {
            item_work_status.setText("待施工");
        }
        mAdapter.addHeaderView(view, 0);
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(RoomOrderListActivity.this, MainActivity.class);
                    final RoomOrderBean.ResultListBean room_result = mAdapter.getData().get(position);
                    intent.putExtra("roomData", (Serializable)room_result);
                    intent.putExtra("workOrderdata", (Serializable) mWork_order);
                    startActivity(intent);
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
                    mPresenter.loadFistPage(mWork_order.getId() + "");
                }

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage(mWork_order.getId() + "");
                }
            }
        });
        mRefreshLayout.autoRefresh();//自动刷新

    }


    @Override
    protected RoomOrderPresenter initPresenter() {
        return new RoomOrderPresenter();
    }

    @Override
    public void loadDataSuccess(RoomOrderBean data) {
        final List<RoomOrderBean.ResultListBean> resultList = data.getResultList();
        if (resultList.isEmpty()) {
            mAdapter.setEmptyView(R.layout.empty_room_order);
        } else {
            mAdapter.setNewData(resultList);
        }
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

}
