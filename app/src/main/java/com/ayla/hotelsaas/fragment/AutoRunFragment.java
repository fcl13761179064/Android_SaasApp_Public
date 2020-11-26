package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.AutoRunRuleEngineAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.AutoRunFragmentPresenter;
import com.ayla.hotelsaas.mvp.view.AutoRunView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * 自动化联动页面
 */
public class AutoRunFragment extends BaseMvpFragment<AutoRunView, AutoRunFragmentPresenter> implements AutoRunView {
    @BindView(R.id.device_recyclerview)
    RecyclerView mRecyclerView;

    AutoRunRuleEngineAdapter mAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ruleengine_show;
    }

    @Override
    protected void initView(View view) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AutoRunRuleEngineAdapter();
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_scene_page);
    }

    @Override
    protected void initListener() {
        mAdapter.setEnableChangedListener(new AutoRunRuleEngineAdapter.OnEnableChangedListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, int position) {
                Log.d("wyj", "onCheckedChanged: " + isChecked + " " + position);
                BaseSceneBean ruleEngineBean = mAdapter.getItem(position);
                mPresenter.changeSceneStatus(ruleEngineBean, isChecked);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_edit:
                        BaseSceneBean ruleEngineBean = (BaseSceneBean) adapter.getItem(position);
                        Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
                        intent.putExtra("sceneBean", ruleEngineBean);
                        if (getParentFragment() != null) {
                            getParentFragment().startActivityForResult(intent, RuleEngineFragment.REQUEST_CODE_SETTING);
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected AutoRunFragmentPresenter initPresenter() {
        return new AutoRunFragmentPresenter();
    }

    public void showData(List<BaseSceneBean> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public void changeSuccess() {
        CustomToast.makeText(getContext(), "修改成功", R.drawable.ic_success);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof RuleEngineFragment) {
            ((RuleEngineFragment) parentFragment).notifyRefresh();
        }
    }

    @Override
    public void changeFailed(BaseSceneBean ruleEngineBean) {
        CustomToast.makeText(getContext(), "修改失败", R.drawable.ic_toast_warming);
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            BaseSceneBean bean = mAdapter.getData().get(i);
            if (bean.getRuleId() == ruleEngineBean.getRuleId()) {
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
}
