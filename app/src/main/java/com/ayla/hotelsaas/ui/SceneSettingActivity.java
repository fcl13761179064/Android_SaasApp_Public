package com.ayla.hotelsaas.ui;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.SceneSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.widget.SceneNameSetDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;

import butterknife.BindView;
import butterknife.OnClick;

public class SceneSettingActivity extends BaseMvpActivity<SceneSettingView, SceneSettingPresenter> implements SceneSettingView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.tv_scene_name)
    public TextView mSceneNameTextView;

    private SceneSettingActionItemAdapter mAdapter;

    @Override
    protected SceneSettingPresenter initPresenter() {
        return new SceneSettingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting;
    }

    @Override
    protected void initView() {
        mAdapter = new SceneSettingActionItemAdapter(R.layout.item_scene_setting_action_device);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.item_scene_setting_action_empty);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

    @Override
    protected void initListener() {

    }

    @OnClick(R.id.tv_scene_name)
    public void sceneNameClicked() {
        new SceneNameSetDialog().show(getSupportFragmentManager(), "scene_name");
    }
}
