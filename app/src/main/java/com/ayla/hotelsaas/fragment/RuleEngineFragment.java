package com.ayla.hotelsaas.fragment;


import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.RuleEnginePagerAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.present.RuleEnginePresenter;
import com.ayla.hotelsaas.mvp.view.RuleEngineView;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * RuleEngine页面
 */
public class RuleEngineFragment extends BaseMvpFragment<RuleEngineView, RuleEnginePresenter> implements RuleEngineView {
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;

    private RuleEnginePagerAdapter mAdapter;
    private RoomOrderBean.ResultListBean mRoom_order;

    public RuleEngineFragment(RoomOrderBean.ResultListBean room_order) {
        this.mRoom_order = room_order;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ruleengine;
    }

    @Override
    protected void initView(View view) {
        mAdapter = new RuleEnginePagerAdapter(getChildFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mViewPager.setAdapter(mAdapter);
        mRefreshLayout.setEnableLoadMore(false);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }

    @Override
    protected void initListener() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mPresenter != null) {
                    mPresenter.loadData(mRoom_order.getRoomId());
                }
            }
        });

        mRefreshLayout.autoRefresh();//自动刷新

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
    protected RuleEnginePresenter initPresenter() {
        return new RuleEnginePresenter();
    }

    @Override
    public void loadDataSuccess(List<RuleEngineBean> data) {
        {
            OneKeyFragment oneKeyFragment = (OneKeyFragment) mAdapter.getItem(0);
            oneKeyFragment.showData(data);
        }
        {
            OneKeyFragment oneKeyFragment = (OneKeyFragment) mAdapter.getItem(1);
            oneKeyFragment.showData(data);
        }
        loadDataFinish();
    }

    @Override
    public void loadDataFinish() {
        mRefreshLayout.finishRefresh();
        mRefreshLayout.finishLoadMore();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            mRefreshLayout.autoRefresh();
        }
    }
}
