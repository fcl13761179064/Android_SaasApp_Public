package com.ayla.hotelsaas.ui.fragment.scene;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.RuleEnginePagerAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.events.SceneChangedEvent;
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.RuleEnginePresenter;
import com.ayla.hotelsaas.mvp.view.RuleEngineView;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * RuleEngine页面
 */
public class RuleEngineFragment extends BaseMvpFragment<RuleEngineView, RuleEnginePresenter> implements RuleEngineView {
    public static final int REQUEST_CODE_SETTING = 0X10;
    private final int REQUEST_CODE_SELECT_GATEWAY = 0X11;
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.fl_content)
    FrameLayout mFrameLayout;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;

    private RuleEnginePagerAdapter mAdapter;
    private long mRoom_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        if (getArguments()!=null){
            mRoom_id = getArguments().getLong("room_id",0);
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ruleengine;
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RuleEnginePagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.setTabTextColors(Color.parseColor("#282828"), ContextCompat.getColor(getContext(), R.color.colorAccent));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadMore(false);
        mSmartRefreshLayout.setVisibility(View.INVISIBLE);
        mFrameLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null));
    }

    @Override
    protected void initListener() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadData(mRoom_id);
                }
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.loadData(mRoom_id);
    }

    @Override
    protected RuleEnginePresenter initPresenter() {
        return new RuleEnginePresenter();
    }

    @Override
    public void loadDataSuccess(List<BaseSceneBean> data) {
        if (mFrameLayout.getChildCount() != 1) {
            mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);
        }
        mSmartRefreshLayout.setVisibility(View.VISIBLE);
        List<BaseSceneBean> oneKeys = new ArrayList<>();
        List<BaseSceneBean> autoRuns = new ArrayList<>();

        for (BaseSceneBean ruleEngineBean : data) {
            if (ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ONE_KEY) {//一键执行
                oneKeys.add(ruleEngineBean);
            }
            if (ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.AUTO || ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ACTION_ONE_RULE) {//自动化
                autoRuns.add(ruleEngineBean);
            }
        }
        {
            OneKeyFragment oneKeyFragment = (OneKeyFragment) mAdapter.getItem(0);
            oneKeyFragment.showData(oneKeys);
        }
        {
            AutoRunFragment oneKeyFragment = (AutoRunFragment) mAdapter.getItem(1);
            oneKeyFragment.showData(autoRuns);
        }
        mSmartRefreshLayout.setEnableRefresh(true);
        mSmartRefreshLayout.finishRefresh(true);
    }

    @Override
    public void loadDataFailed(Throwable throwable) {
        if (mFrameLayout.getChildCount() != 1) {
            mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);
            View view = LayoutInflater.from(getContext()).inflate(R.layout.widget_empty_view, null);
            mFrameLayout.addView(view);
            view.findViewById(R.id.bt_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFrameLayout.removeViewAt(mFrameLayout.getChildCount() - 1);
                    mFrameLayout.addView(LayoutInflater.from(getContext()).inflate(R.layout.layout_loading, null));
                    mPresenter.loadData(mRoom_id);
                }
            });
            mSmartRefreshLayout.setVisibility(View.INVISIBLE);
        }
        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh(false);
        }
    }

    /**
     * subFragment 通知刷新的入口
     */
    public final void notifyRefresh() {
        mSmartRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mSmartRefreshLayout.autoRefresh();//自动刷新
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceRemovedEvent event) {
        notifyRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceAddEvent event) {
        notifyRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSceneChanged(SceneChangedEvent event) {
        notifyRefresh();
    }
}
