package com.ayla.hotelsaas.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceListShowPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.DeviceAddCategoryActivity;
import com.ayla.hotelsaas.ui.DeviceDetailH5Activity;
import com.ayla.hotelsaas.ui.DeviceMoreActivity;
import com.ayla.hotelsaas.ui.TouchPanelActivity;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.UnknownHostException;

import butterknife.BindView;

public class DeviceListFragment extends BaseMvpFragment<DeviceListView, DeviceListShowPresenter> implements DeviceListView {

    private final int REQUEST_CODE_DEVICE_EDIT = 0X10;
    private final Long room_id;
    @BindView(R.id.device_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    public static int[] drawableIcon = new int[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten, R.drawable.eleven, R.drawable.tween};
    private DeviceListAdapter mAdapter;

    public DeviceListFragment(Long room_id) {
        this.room_id = room_id;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_list;
    }

    @Override
    protected void initView(View view) {
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });

        mAdapter = new DeviceListAdapter();
        mAdapter.bindToRecyclerView(mRecyclerview);
        mAdapter.setEmptyView(R.layout.layout_loading);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final DeviceListBean.DevicesBean devicesBean = mAdapter.getData().get(position);
                if (devicesBean.getCuId() == 1 && "a1UR1BjfznK".equals(devicesBean.getDeviceCategory())) {
                    Intent intent = new Intent(getContext(), TouchPanelActivity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    intent.putExtra("pannel_type", "1");
                    startActivityForResult(intent, REQUEST_CODE_DEVICE_EDIT);
                } else if (devicesBean.isHasH5()) {
                    Intent intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), DeviceMoreActivity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    startActivity(intent);
                }
            }
        });

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refresh(room_id);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadData(room_id);
            }
        });

        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), DeviceAddCategoryActivity.class);
                intent.putExtra("scopeId", room_id);
                startActivityForResult(intent, REQUEST_CODE_DEVICE_EDIT);
            }
        });

    }

    @Override
    protected void initData() {
        mPresenter.refresh(room_id);
    }

    @Override
    protected DeviceListShowPresenter initPresenter() {
        return new DeviceListShowPresenter();
    }

    @Override
    public void loadDataSuccess(DeviceListBean data) {
        MyApplication.getInstance().setDevicesBean(data.getDevices());

        mAdapter.setEmptyView(R.layout.empty_device_order);
        mSmartRefreshLayout.setEnableRefresh(true);

        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh();
        }
        mAdapter.setNewData(data.getDevices());
    }

    @Override
    public void loadDataFinish(Throwable throwable) {
        if (mAdapter.getData().isEmpty()) {//如果是空的数据
            mAdapter.setEmptyView(R.layout.widget_empty_view);
            mAdapter.getEmptyView().findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setEmptyView(R.layout.layout_loading);
                    mPresenter.refresh(room_id);
                }
            });
        } else {
            mSmartRefreshLayout.setEnableRefresh(true);
            mAdapter.setEmptyView(R.layout.empty_device_order);
            if (throwable instanceof UnknownHostException) {
                CustomToast.makeText(getContext(), R.string.request_not_connect, R.drawable.ic_toast_warming);
            }
        }
        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DEVICE_EDIT && resultCode == Activity.RESULT_OK) {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceRemovedEvent event) {
        mSmartRefreshLayout.autoRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        mAdapter.notifyDataSetChanged();
    }
}