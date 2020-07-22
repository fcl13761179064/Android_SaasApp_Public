package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.DeviceListShowPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.List;
import butterknife.BindView;

import static androidx.core.content.ContextCompat.startActivity;

public class DeviceListShowActivity extends BaseMvpActivity<DeviceListView, DeviceListShowPresenter> implements DeviceListView {


    @BindView(R.id.device_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.appBar)
    AppBar appBar;
    private DeviceListAdapter mAdapter;
    private RoomOrderBean.ResultListBean mRoom_order;
    private WorkOrderBean.ResultListBean mWork_order;

    @Override
    protected int getLayoutId() {
        return R.layout.device_list_show;
    }

    @Override
    public void refreshUI() {
        mRoom_order = (RoomOrderBean.ResultListBean) getIntent().getSerializableExtra("roomData");
        mWork_order = (WorkOrderBean.ResultListBean) getIntent().getSerializableExtra("workOrderdata");
      //  appBar.setCenterText(mWork_order.getTitle());
        super.refreshUI();
    }

    @Override
    protected void initView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DeviceListAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mAdapter.setEmptyView(R.layout.empty_device_order);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initListener() {
    /*    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(DeviceListShowActivity.this, Devi.class);
                    intent.putExtra("device", (Serializable) mAdapter.getData().get(position));
                    startActivityForResult(intent, 0x101);
                }
            }
        });*/

        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (null != mAdapter.getData()) {
                    mAdapter.getData().clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (mPresenter != null) {
                    mPresenter.loadFistPage(1+ "");
                }

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage(1 + "");
                }
            }
        });
        mRefreshLayout.autoRefresh();//自动刷新

        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeviceListShowActivity.this, DeviceAddCategoryActivity.class);
                startActivity(intent);
            }
        });

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
