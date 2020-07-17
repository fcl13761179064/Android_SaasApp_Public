package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.RoomOrderListAdapter;
import com.ayla.hotelsaas.adapter.WorkOrderAdapter;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;


public class RoomOrderListActivity extends BasicActivity {

    @BindView(R.id.room_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.appBar)
    AppBar appBar;
    private RoomOrderListAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.room_order_list_activity;
    }

    @Override
    public void refreshUI() {
        WorkOrderBean.WorkOrder work_order = (WorkOrderBean.WorkOrder) getIntent().getSerializableExtra("work_order");
        appBar.setCenterText(work_order.getProjectName());
        super.refreshUI();
    }

    @Override
    protected void initView() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RoomOrderListAdapter();
        recyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(recyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        ArrayList<WorkOrderBean> workOrderBeans = new ArrayList<>();
        WorkOrderBean workOrderBean;
        for (int x = 0; x < 20; x++) {
            workOrderBean = new WorkOrderBean();
            //workOrderBean.setResourceNum("房间号1");
            workOrderBeans.add(workOrderBean);
        }
        mAdapter.addData(workOrderBeans);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!FastClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(RoomOrderListActivity.this, RoomOrderListActivity.class);
                    intent.putExtra("voucher", (Serializable) mAdapter.getData());
                    intent.putExtra("position", position);
                    startActivityForResult(intent, 0x101);
                }
            }
        });
    }


}
