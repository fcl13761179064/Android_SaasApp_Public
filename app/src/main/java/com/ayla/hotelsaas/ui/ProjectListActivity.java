package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的项目页面
 */
public class ProjectListActivity extends BaseMvpActivity<ProjectListView, ProjectListPresenter> implements ProjectListView {
    @BindView(R.id.SwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.RecyclerView)
    RecyclerView mRecyclerView;

    private ProjectListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadData();
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
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void showData(List<Object> data) {
        mAdapter.setNewData(data);
    }

    @Override
    protected void appBarLeftIvClicked() {
        startActivity(new Intent(this, PersonCenterActivity.class));
    }

    @OnClick(R.id.bt_add)
    void handleAdd() {
        startActivity(new Intent(this, CreateProjectActivity.class));
    }
}
