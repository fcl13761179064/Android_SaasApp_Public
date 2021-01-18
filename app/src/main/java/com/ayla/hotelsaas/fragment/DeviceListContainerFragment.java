package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.FragmentDeviceContainerBinding;
import com.ayla.hotelsaas.databinding.ViewStubDeviceListContainerBinding;
import com.ayla.hotelsaas.databinding.WidgetEmptyViewBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceListContainerPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListContainerView;
import com.ayla.hotelsaas.ui.DeviceAddCategoryActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceListContainerFragment extends BaseMvpFragment<DeviceListContainerView, DeviceListContainerPresenter> implements DeviceListContainerView {

    public static int[] drawableIcon = new int[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten, R.drawable.eleven, R.drawable.tween};

    private final Long room_id;
    FragmentDeviceContainerBinding binding;
    ViewStubDeviceListContainerBinding deviceListContainerBinding;


    FragmentStatePagerAdapter mAdapter;

    public DeviceListContainerFragment(Long room_id) {
        this.room_id = room_id;
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
    protected DeviceListContainerPresenter initPresenter() {
        return new DeviceListContainerPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = FragmentDeviceContainerBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    protected void initView(View view) {
        loadData();
    }

    @Override
    protected void initListener() {
        binding.floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceAddCategoryActivity.class);
                intent.putExtra("scopeId", room_id);
                startActivity(intent);
            }
        });
        binding.contentViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                deviceListContainerBinding = ViewStubDeviceListContainerBinding.bind(inflated);
                deviceListContainerBinding.deviceRefreshLayout.setEnableRefresh(false);
                deviceListContainerBinding.deviceRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                        mPresenter.loadData(room_id);
                    }
                });
                deviceListContainerBinding.viewPager.setAdapter(mAdapter);
                deviceListContainerBinding.tlTabs.setupWithViewPager(deviceListContainerBinding.viewPager);
//                deviceListContainerBinding.emptyDeviceViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
//                    @Override
//                    public void onInflate(ViewStub stub, View inflated) {
//                        EmptyDeviceListBinding bind = EmptyDeviceListBinding.bind(inflated);
//                        bind.tvEmptyText.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                            }
//                        });
//                    }
//                });
            }
        });
        binding.netErrorViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                Log.d(TAG, "onInflate: 1111111");
                WidgetEmptyViewBinding emptyViewBinding = WidgetEmptyViewBinding.bind(inflated);
                emptyViewBinding.btRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData();
                    }
                });
            }
        });
    }

    @Override
    protected void initData() {
        mAdapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                return new DeviceListFragmentNew(room_id, deviceListBean.getDevices());
            }

            @Override
            public int getCount() {
                return deviceListBean == null ? 0 : 1;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "全部";
            }
        };
    }

    private DeviceListBean deviceListBean;

    @Override
    public void loadDataSuccess(DeviceListBean data) {
        deviceListBean = data;

        binding.loadingViewStub.setVisibility(View.GONE);
        binding.netErrorViewStub.setVisibility(View.GONE);
        binding.contentViewStub.setVisibility(View.VISIBLE);

        deviceListContainerBinding.deviceRefreshLayout.setEnableRefresh(true);
        deviceListContainerBinding.deviceRefreshLayout.finishRefresh(true);

        MyApplication.getInstance().setDevicesBean(data.getDevices());

        if (data.getDevices().size() == 0) {
            deviceListContainerBinding.emptyDeviceViewStub.setVisibility(View.VISIBLE);
            deviceListContainerBinding.viewPager.setVisibility(View.GONE);
        } else {
            deviceListContainerBinding.emptyDeviceViewStub.setVisibility(View.GONE);
            deviceListContainerBinding.viewPager.setVisibility(View.VISIBLE);
        }
        deviceListContainerBinding.tlTabs.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadDataFinish(Throwable throwable) {
        binding.loadingViewStub.setVisibility(View.GONE);

        if (deviceListContainerBinding != null) {
            deviceListContainerBinding.deviceRefreshLayout.finishRefresh(false);
            if (mAdapter.getCount() == 0) {
                deviceListContainerBinding.emptyDeviceViewStub.setVisibility(View.VISIBLE);
                deviceListContainerBinding.viewPager.setVisibility(View.GONE);
            }
        } else {
            binding.netErrorViewStub.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceRemovedEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(DeviceAddEvent event) {
        loadData();
    }

    private void loadData() {
        if (deviceListContainerBinding != null) {
            deviceListContainerBinding.deviceRefreshLayout.autoRefresh();
        } else {
            binding.loadingViewStub.setVisibility(View.VISIBLE);
            binding.netErrorViewStub.setVisibility(View.GONE);
            mPresenter.loadData(room_id);
        }
    }
}
