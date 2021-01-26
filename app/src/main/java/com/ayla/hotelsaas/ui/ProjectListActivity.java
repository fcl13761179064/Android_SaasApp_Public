package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的项目页面
 */
public class ProjectListActivity extends BaseMvpActivity<ProjectListView, ProjectListPresenter> implements ProjectListView {
    private final int REQUEST_CODE_CREATE_PROJECT = 0x10;

    @BindView(R.id.SmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.appBar)
    AppBar appBar;

    private ProjectListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("upgrade")) {
            VersionUpgradeBean versionUpgradeBean = (VersionUpgradeBean) getIntent().getSerializableExtra("upgrade");
            Constance.saveVersionUpgradeInfo(versionUpgradeBean);
        }
        mPresenter.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = appBar.findViewById(R.id.iv_left);
        VersionUpgradeBean versionUpgradeInfo = Constance.getVersionUpgradeInfo();
        if (versionUpgradeInfo != null) {
            imageView.setImageResource(R.drawable.person_center_tip);
        } else {
            imageView.setImageResource(R.drawable.person_center);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mAdapter.setNewData(null);
        mSmartRefreshLayout.autoRefresh();
    }

    @Override
    protected ProjectListPresenter initPresenter() {
        return new ProjectListPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_project_list;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new ProjectListAdapter(R.layout.item_project_list);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.layout_loading);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setEnableRefresh(false);
    }

    @Override
    protected void initListener() {
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadData();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refresh();
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WorkOrderBean.ResultListBean item = mAdapter.getItem(position);
                startActivity(new Intent(getApplicationContext(), ProjectMainActivity.class).putExtra("bean", item));
            }
        });
    }

    @Override
    public void showData(WorkOrderBean data) {
        mAdapter.setEmptyView(R.layout.empty_project_list);
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.setEnableLoadMore(true);

        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh();
            mAdapter.setNewData(data.getResultList());
        } else if (mSmartRefreshLayout.isLoading()) {
            mSmartRefreshLayout.finishLoadMore();
            mAdapter.addData(data.getResultList());
        } else {
            mAdapter.setNewData(data.getResultList());
        }

        boolean lastPage = data.getCurrentPage() >= data.getTotalPages();
        mSmartRefreshLayout.setNoMoreData(lastPage);
    }

    @Override
    public void onRequestFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
        if (mAdapter.getData().isEmpty()) {//如果是空的数据
            mAdapter.setEmptyView(R.layout.widget_empty_view);
            mAdapter.getEmptyView().findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setEmptyView(R.layout.layout_loading);
                    mPresenter.refresh();
                }
            });
        } else {
            mSmartRefreshLayout.setEnableRefresh(true);
            mAdapter.setEmptyView(R.layout.empty_project_list);
        }
        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh(false);
        } else if (mSmartRefreshLayout.isLoading()) {
            mSmartRefreshLayout.finishLoadMore(false);
        }
    }

    @Override
    protected void appBarLeftIvClicked() {
        startActivity(new Intent(this, PersonCenterActivity.class));
    }

    @OnClick(R.id.bt_add)
    void handleAdd() {
        startActivityForResult(new Intent(this, CreateProjectActivity.class), REQUEST_CODE_CREATE_PROJECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PROJECT && resultCode == RESULT_OK) {
            mSmartRefreshLayout.autoRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
