package com.ayla.hotelsaas.ui.fragment.scene;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.AutoRunRuleEngineAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.bean.scene_bean.LocalSceneBean;
import com.ayla.hotelsaas.bean.scene_bean.RemoteSceneBean;
import com.ayla.hotelsaas.mvp.present.AutoRunFragmentPresenter;
import com.ayla.hotelsaas.mvp.view.AutoRunView;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.ui.activities.local_scene.LocalSceneSettingActivity;
import com.ayla.hotelsaas.ui.activities.remote_scene.RemoteSceneSettingActivity;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.SizeUtils;

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
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new AutoRunRuleEngineAdapter();
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_scene_page);
    }

    @Override
    protected void initListener() {
        mAdapter.setEnableChangedListener((buttonView, isChecked, position) -> {
            Log.d("AutoRunFragment", "onCheckedChanged: " + isChecked + " " + position);
            BaseSceneBean ruleEngineBean = mAdapter.getItem(position);
            if (ruleEngineBean != null) {
                mPresenter.changeSceneStatus(ruleEngineBean, isChecked);
            } else
                Log.e("AutoRunFragment", "onCheckedChanged: " + "点击项不存在");
        });
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            BaseSceneBean ruleEngineBean = (BaseSceneBean) adapter.getItem(position);
            if (ruleEngineBean instanceof LocalSceneBean) {
                Intent intent = new Intent(getActivity(), LocalSceneSettingActivity.class);
                intent.putExtra(KEYS.LOCALSCENEBEAN, ruleEngineBean);
                if (getParentFragment() != null) {
                    getParentFragment().startActivityForResult(intent, RuleEngineFragment.REQUEST_CODE_SETTING);
                }
            } else {
                if (ruleEngineBean instanceof RemoteSceneBean) {
                    Intent intent = new Intent(getActivity(), RemoteSceneSettingActivity.class);
                    intent.putExtra(KEYS.REMOTESCENEBEAN, ruleEngineBean);
                    if (getParentFragment() != null) {
                        getParentFragment().startActivityForResult(intent, RuleEngineFragment.REQUEST_CODE_SETTING);
                    }
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
    public void changeFailed(BaseSceneBean ruleEngineBean, Throwable throwable) {
        CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg("修改失败", throwable), R.drawable.ic_toast_warning);
        for (int i = 0; i < mAdapter.getData().size(); i++) {
            BaseSceneBean bean = mAdapter.getData().get(i);
            if (bean.getRuleId() == ruleEngineBean.getRuleId()) {
                mAdapter.notifyItemChanged(i);
                break;
            }
        }
    }
}
