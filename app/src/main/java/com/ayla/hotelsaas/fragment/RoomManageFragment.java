package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.WorkOrderAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.RoomOrderPresenter;
import com.ayla.hotelsaas.mvp.view.RoomOrderView;
import com.ayla.hotelsaas.ui.RoomOrderListActivity;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.io.Serializable;
import butterknife.BindView;

public class RoomManageFragment extends BaseMvpFragment<RoomOrderView, RoomOrderPresenter> implements RoomOrderView {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    private WorkOrderAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.room_manage_fragment;
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
                    mPresenter.loadFistPage("1");
                }
                mRefreshLayout.setEnableLoadMore(true);

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
    protected void initData() {

    }


    @Override
    public void loadDataSuccess(RoomOrderBean data) {
        ToastUtils.showShortToast(data.getCurrentPage()+"");
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    protected RoomOrderPresenter initPresenter() {
        return new RoomOrderPresenter();
    }
}
