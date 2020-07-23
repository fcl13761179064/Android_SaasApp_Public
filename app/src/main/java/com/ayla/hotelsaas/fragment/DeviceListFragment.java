package com.ayla.hotelsaas.fragment;


import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.DeviceListShowPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.ui.DeviceAddCategoryActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import butterknife.BindView;

public class DeviceListFragment extends BaseMvpFragment<DeviceListView, DeviceListShowPresenter> implements DeviceListView {
    @BindView(R.id.device_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    private DeviceListAdapter mAdapter;
    private RoomOrderBean.ResultListBean mRoom_order;
    private WorkOrderBean.ResultListBean mWork_order;
    private RecyclerView mRecyclerview;

    public DeviceListFragment(RoomOrderBean.ResultListBean room_order) {
        this.mRoom_order = room_order;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_list;
    }

    @Override
    protected void initView(View view) {
        mRefreshLayout = view.findViewById(R.id.device_refreshLayout);
        mRecyclerview = view.findViewById(R.id.device_recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new DeviceListAdapter();
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null != mAdapter.getData()) {
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.loadFistPage(mRoom_order.getRoomId() + "");
                }

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage(mRoom_order.getRoomId() + "");
                }
            }
        });

        mRefreshLayout.autoRefresh();//自动刷新
        mAdapter.setEmptyView(R.layout.empty_device_order);

        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceAddCategoryActivity.class);
                intent.putExtra("scopeId", mRoom_order.getRoomId());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected DeviceListShowPresenter initPresenter() {
        return new DeviceListShowPresenter();
    }

    @Override
    public void loadDataSuccess(DeviceListBean data) {
        final List<DeviceListBean.DevicesBean> devices = data.getDevices();
        mAdapter.setNewData(devices);
        MyApplication.getInstance().setDevicesBean(devices);
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }
}
