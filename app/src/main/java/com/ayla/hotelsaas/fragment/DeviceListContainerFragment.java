package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.ayla.hotelsaas.adapter.ScaleTabAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.databinding.FragmentDeviceContainerBinding;
import com.ayla.hotelsaas.databinding.ViewStubDeviceListContainerBinding;
import com.ayla.hotelsaas.databinding.WidgetEmptyViewBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.events.RegionChangeEvent;
import com.ayla.hotelsaas.mvp.present.DeviceListContainerPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListContainerView;
import com.ayla.hotelsaas.ui.DeviceAddCategoryActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.Serializable;
import java.util.List;

/*
 * 设备列表页面
 * @auth fanchunlei
 */
public class DeviceListContainerFragment extends BaseMvpFragment<DeviceListContainerView, DeviceListContainerPresenter> implements DeviceListContainerView {


    FragmentDeviceContainerBinding binding;
    ViewStubDeviceListContainerBinding deviceListContainerBinding;
    FragmentStatePagerAdapter mAdapter;
    private List<DeviceLocationBean> LocationBeans;
    private long room_id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            room_id = getArguments().getLong("room_id",0);
        }
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


    protected void initMagicIndicator() {
        mPresenter.getAllDeviceLocation(room_id);

    }

    @Override
    protected void initListener() {
        binding.floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DeviceAddCategoryActivity.class);//正常的添加设备
                intent.putExtra("scopeId", room_id);
                startActivity(intent);
            }
        });
        binding.contentViewStub.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub stub, View inflated) {
                deviceListContainerBinding = ViewStubDeviceListContainerBinding.bind(inflated);
                deviceListContainerBinding.deviceRefreshLayout.setEnableRefresh(false);
                initMagicIndicator();
                deviceListContainerBinding.deviceRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                    @Override
                    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                        mPresenter.loadData(room_id);
                        mPresenter.getAllDeviceLocation(room_id);
                    }
                });
                deviceListContainerBinding.tlTabs.setupWithViewPager(deviceListContainerBinding.viewPager);
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
            public DeviceListFragmentNew getItem(int position) {
                DeviceListFragmentNew deviceListFragmentNew = new DeviceListFragmentNew();
                Bundle bundle = new Bundle();
                //存入数据到Bundle对象中
                bundle.putLong("mRoomId", room_id);
                bundle.putSerializable("mDevices", (Serializable) deviceListBean.getDevices());
                bundle.putLong("mRegionId", LocationBeans.get(position).getRegionId());
                bundle.putInt("mPosition", position);
                //调用Fragment的setArguments方法，传入Bundle对象
                deviceListFragmentNew.setArguments(bundle);

                return deviceListFragmentNew;
            }

            @Override
            public int getCount() {
                if (LocationBeans==null){
                    return 0;
                }else
                return  LocationBeans.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return LocationBeans.get(position).getRegionName();
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
            deviceListContainerBinding.homeTabLayout.setVisibility(View.GONE);
            deviceListContainerBinding.emptyDeviceViewStub.setVisibility(View.VISIBLE);
            deviceListContainerBinding.viewPager.setVisibility(View.GONE);
        } else {
            deviceListContainerBinding.homeTabLayout.setVisibility(View.VISIBLE);
            deviceListContainerBinding.emptyDeviceViewStub.setVisibility(View.GONE);
            deviceListContainerBinding.viewPager.setVisibility(View.VISIBLE);
        }
        deviceListContainerBinding.tlTabs.setVisibility(View.GONE);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadDeviceLocationSuccess(List<DeviceLocationBean> data) {
        LocationBeans = data;
        DeviceLocationBean deviceLocationBean = new DeviceLocationBean();
        deviceLocationBean.setRegionName("全部");
        deviceLocationBean.setRegionId(-1l);
        LocationBeans.add(0, deviceLocationBean);
        deviceListContainerBinding.viewPager.setAdapter(mAdapter);
        binding.loadingViewStub.setVisibility(View.GONE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdjustMode(false);
        ScaleTabAdapter adapter = new ScaleTabAdapter(data, deviceListContainerBinding.viewPager, deviceListContainerBinding.homeTabLayout);
        commonNavigator.setAdapter(adapter);
        deviceListContainerBinding.homeTabLayout.setNavigator(commonNavigator);
        ViewPagerHelper.bind(deviceListContainerBinding.homeTabLayout, deviceListContainerBinding.viewPager);
        deviceListContainerBinding.viewPager.setCurrentItem(0, false);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRemoved(RegionChangeEvent event) {
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
