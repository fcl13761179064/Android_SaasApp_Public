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
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.WorkOrderPresenter;
import com.ayla.hotelsaas.mvp.view.WorkOrderView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.StatusBarUtil;
import com.ayla.hotelsaas.widget.AppBar;
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
    @BindView(R.id.appBar)
    AppBar appBar;
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
                if (mPresenter != null) {
                    mPresenter.loadNextPage("1");
                }
            }
        });
        mRefreshLayout.autoRefresh();//自动刷新

    }

    @Override
    protected void mExitApp() {
        finish();
        MyApplication.getInstance().setUserEntity(null);
        Intent intent = new Intent(WorkOrderListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void loadDataSuccess(ArrayList<WorkOrderBean.WorkOrder> data) {
        mAdapter.addData(data);
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
         ArrayList<WorkOrderBean.WorkOrder> workOrderBeans = new ArrayList<>();
         WorkOrderBean.WorkOrder  workOrderBean;
        for (int x=0;x<20;x++){
            workOrderBean = new WorkOrderBean.WorkOrder();
            workOrderBean.setProjectName("你好啊");
            workOrderBean.setStartDate("2019-2-32");
            workOrderBean.setEndDate("2019-6-32");
            workOrderBean.setProgressStatus("带施工");
            workOrderBeans.add(workOrderBean);
        }
        mAdapter.addData(workOrderBeans);
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

}
