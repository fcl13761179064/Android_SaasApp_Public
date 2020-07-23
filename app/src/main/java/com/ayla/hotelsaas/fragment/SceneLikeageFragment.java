package com.ayla.hotelsaas.fragment;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneLikeageAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.present.SceneLikeagePresenter;
import com.ayla.hotelsaas.mvp.view.SceneLikeageView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import butterknife.BindView;

public class SceneLikeageFragment extends BaseMvpFragment<SceneLikeageView, SceneLikeagePresenter> implements SceneLikeageView {
    @BindView(R.id.device_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mRefreshLayout;


    private SceneLikeageAdapter mAdapter;
    private RoomOrderBean.ResultListBean mRoom_order;
    private RecyclerView mRecyclerview;

    public SceneLikeageFragment(RoomOrderBean.ResultListBean room_order) {
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
        mAdapter = new SceneLikeageAdapter();
        mRecyclerview.setAdapter(mAdapter);
        mAdapter.bindToRecyclerView(mRecyclerview);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRefreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                RuleEngineBean ruleEngineBean = (RuleEngineBean) adapter.getItem(position);
                mPresenter.runRuleEngine(ruleEngineBean.getRuleId());
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                RuleEngineBean ruleEngineBean = (RuleEngineBean) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
                intent.putExtra("sceneBean", ruleEngineBean);
                startActivityForResult(intent, 0);
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
                    mPresenter.loadFistPage(mRoom_order.getRoomId());
                }

            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadNextPage(mRoom_order.getRoomId());
                }
            }
        });

        mRefreshLayout.autoRefresh();//自动刷新
        mAdapter.setEmptyView(R.layout.empty_scene_page);

        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
                intent.putExtra("scopeId", mRoom_order.getRoomId());
                startActivityForResult(intent, 0);
            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected SceneLikeagePresenter initPresenter() {
        return new SceneLikeagePresenter();
    }

    @Override
    public void loadDataSuccess(List<RuleEngineBean> data) {
        mAdapter.setNewData(data);
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void runSceneSuccess() {
        CustomToast.makeText(getContext(), "触发成功", R.drawable.ic_toast_success).show();
    }

    @Override
    public void runSceneFailed() {
        CustomToast.makeText(getContext(), "触发失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            mRefreshLayout.autoRefresh();
        }
    }
}
