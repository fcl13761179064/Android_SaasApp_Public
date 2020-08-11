package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.OneKeyRuleEngineAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.present.OneKeyPresenter;
import com.ayla.hotelsaas.mvp.view.OneKeyView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * 一键联动页面
 */
public class OneKeyFragment extends BaseMvpFragment<OneKeyView, OneKeyPresenter> implements OneKeyView {
    @BindView(R.id.device_recyclerview)
    RecyclerView mRecyclerView;

    OneKeyRuleEngineAdapter mAdapter;

    @Override
    protected OneKeyPresenter initPresenter() {
        return new OneKeyPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ruleengine_show;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OneKeyRuleEngineAdapter();
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_scene_page);
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
                if (getParentFragment() != null) {
                    getParentFragment().startActivityForResult(intent, 0);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    public void showData(List<RuleEngineBean> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public void runSceneSuccess() {
        CustomToast.makeText(getContext(), "触发成功", R.drawable.ic_toast_success).show();
    }

    @Override
    public void runSceneFailed() {
        CustomToast.makeText(getContext(), "触发失败", R.drawable.ic_toast_warming).show();
    }
}
