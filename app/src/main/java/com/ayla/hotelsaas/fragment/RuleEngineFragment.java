package com.ayla.hotelsaas.fragment;


import android.app.Activity;
import android.content.Intent;
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
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.events.SceneChangedEvent;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.RuleEnginePresenter;
import com.ayla.hotelsaas.mvp.view.RuleEngineView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.GatewaySelectActivity;
import com.ayla.hotelsaas.ui.SceneSettingActivity;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.CustomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private final long mRoom_ID;
    public static final int REQUEST_CODE_SETTING = 0X10;
    private final int REQUEST_CODE_SELECT_GATEWAY = 0X11;
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.fl_content)
    FrameLayout mFrameLayout;
    @BindView(R.id.device_refreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;

    private RuleEnginePagerAdapter mAdapter;

    public RuleEngineFragment(long mRoom_ID) {
        this.mRoom_ID = mRoom_ID;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
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
                    mPresenter.loadData(mRoom_ID);
                }
            }
        });

        float_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomSheet show = new CustomSheet.Builder(getActivity())
                        .setText("本地联动（本地存储）", "云端联动（云端存储）")
                        .show(new CustomSheet.CallBack() {
                            @Override
                            public void callback(int index) {
                                Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
                                intent.putExtra("scopeId", mRoom_ID);
                                intent.putExtra("siteType", index == 0 ? 1 : 2);
                                if (index == 0) {//选择了本地联动
                                    List<DeviceListBean.DevicesBean> gateways = new ArrayList<>();
                                    List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
                                    for (DeviceListBean.DevicesBean bean : devicesBean) {
                                        if (TempUtils.isDeviceGateway(bean)) {
                                            gateways.add(bean);
                                        }
                                    }
                                    if (!gateways.isEmpty()) {//存在网关
                                        if (gateways.size() == 1) {
                                            DeviceListBean.DevicesBean gateway = gateways.get(0);
                                            if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                                                intent.putExtra("targetGateway", gateway.getDeviceId());
                                            } else {
                                                CustomToast.makeText(getContext(), "请确保网关在线", R.drawable.ic_toast_warming);
                                                return;
                                            }
                                        } else {
                                            intent = new Intent(getActivity(), GatewaySelectActivity.class).putExtras(intent);
                                            startActivityForResult(intent, REQUEST_CODE_SELECT_GATEWAY);
                                            return;
                                        }
                                    } else {
                                        CustomToast.makeText(getContext(), "请先添加网关", R.drawable.ic_toast_warming);
                                        return;
                                    }
                                }
                                startActivityForResult(intent, REQUEST_CODE_SETTING);
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });
    }

    @Override
    protected void initData() {
        mPresenter.loadData(mRoom_ID);
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
            if (ruleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.AUTO) {//自动化
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
                    mPresenter.loadData(mRoom_ID);
                }
            });
            mSmartRefreshLayout.setVisibility(View.INVISIBLE);
        }
        if (mSmartRefreshLayout.isRefreshing()) {
            mSmartRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTING && resultCode == Activity.RESULT_OK) {
//            mSmartRefreshLayout.autoRefresh();
        }
        if (requestCode == REQUEST_CODE_SELECT_GATEWAY && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(getActivity(), SceneSettingActivity.class);
            intent.putExtras(data).putExtra("targetGateway", data.getStringExtra("deviceId"));
            startActivityForResult(intent, REQUEST_CODE_SETTING);
        }
    }

    /**
     * subFragment 通知刷新的入口
     */
    public final void notifyRefresh() {
        mSmartRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSmartRefreshLayout.autoRefresh();//自动刷新
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
