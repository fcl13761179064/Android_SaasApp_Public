package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.OneKeyRuleEngineAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.OneKeyPresenter;
import com.ayla.hotelsaas.mvp.view.OneKeyView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.blankj.utilcode.util.SizeUtils;
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
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseSceneBean ruleEngineBean = (BaseSceneBean) adapter.getItem(position);
                boolean needWarming = false;
                s1:
                for (BaseSceneBean.Action actionItem : ruleEngineBean.getActions()) {
                    if (!(actionItem instanceof BaseSceneBean.DeviceAction)) {
                        continue;
                    }
                    String targetDeviceId = actionItem.getTargetDeviceId();
                    for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                        if (devicesBean.getDeviceId().equals(targetDeviceId)) {
                            continue s1;
                        }
                    }
                    if (ruleEngineBean.getActions().size() == 1) {
                        CustomToast.makeText(getContext(), "执行失败，设备已移除", R.drawable.ic_toast_warming);
                        return;
                    } else {
                        needWarming = true;
                    }
                }
                mPresenter.runRuleEngine(ruleEngineBean.getRuleId(), needWarming);
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BaseSceneBean ruleEngineBean = (BaseSceneBean) adapter.getItem(position);
                Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
                intent.putExtra("sceneBean", ruleEngineBean);
                if (getParentFragment() != null) {
                    getParentFragment().startActivityForResult(intent, RuleEngineFragment.REQUEST_CODE_SETTING);
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    public void showData(List<BaseSceneBean> data) {
        mAdapter.setNewData(data);
    }

    @Override
    public void runSceneSuccess(boolean needWarming) {
        CustomToast.makeText(getContext(), String.format("%s%s", "执行成功", needWarming ? "，有设备已移除请检查" : ""), R.drawable.ic_success);
    }

    @Override
    public void runSceneFailed() {
        CustomToast.makeText(getContext(), "执行失败", R.drawable.ic_toast_warming);
    }
}
