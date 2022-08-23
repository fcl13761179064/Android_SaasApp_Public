package com.ayla.hotelsaas.ui.fragment.device_list;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ScaleTabAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.mvp.present.DeviceListContainerPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListContainerView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*
 * 设备列表页面
 * @auth fanchunlei
 */
public class DeviceListContainerFragment extends BaseMvpFragment<DeviceListContainerView, DeviceListContainerPresenter> implements DeviceListContainerView {

    FragmentStatePagerAdapter mAdapter;
    private List<DeviceLocationBean> LocationBeans = new ArrayList<>();
    private long room_id;
    private int billId;
    private long businessId;
    private String mRoom_name;

    @BindView(R.id.content_view)
    View contentView;
    @BindView(R.id.net_error_view)
    View netErrorView;
    @BindView(R.id.loading_view)
    View loadingView;
    private String move_wall_type;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room_id = getArguments().getLong("room_id", 0);
            businessId = getArguments().getLong("businessId", 0);
            mRoom_name = getArguments().getString("roomName");
            move_wall_type = getArguments().getString("move_wall_type");
        }
//        EventBus.getDefault().register(this);
    }


    @Override
    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected DeviceListContainerPresenter initPresenter() {
        return new DeviceListContainerPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_container;
    }


    @Override
    protected void initView(View view) {
        loadingView.setVisibility(View.VISIBLE);
        loadAllData();
    }


    protected void initMagicIndicator() {
        mPresenter.getAllDeviceLocation(room_id);
//        loadAllData();
    }


    @Override
    protected void initListener() {
        netErrorView.findViewById(R.id.bt_refresh).setOnClickListener(v -> loadAllData());
        SmartRefreshLayout refreshLayout = contentView.findViewById(R.id.device_refreshLayout);
        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(refreshLayout1 -> {
                loadAllData();
            });
        }
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
                bundle.putLong("mRegionId", LocationBeans.get(position).getRegionId());
                bundle.putInt("mPosition", position);
                bundle.putString("name", LocationBeans.get(position).getRegionName());
                bundle.putString("roomName", mRoom_name);
                bundle.putLong("businessId", businessId);
                bundle.putString("move_wall_type", move_wall_type);
                //调用Fragment的setArguments方法，传入Bundle对象
                deviceListFragmentNew.setArguments(bundle);

                return deviceListFragmentNew;
            }

            @Override
            public int getCount() {
                if (LocationBeans == null) {
                    return 0;
                } else
                    return LocationBeans.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return LocationBeans.get(position).getRegionName();
            }
        };

    }


    @Override
    public void loadDataSuccess(List<BaseDevice> data) {
//        loadingView.setVisibility(View.GONE);
//        netErrorView.setVisibility(View.GONE);
//        contentView.setVisibility(View.VISIBLE);
//        SmartRefreshLayout refreshLayout = contentView.findViewById(R.id.device_refreshLayout);
//        if (refreshLayout != null) {
//            refreshLayout.finishRefresh();
//        }
//        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void loadAllDeviceDataSuccess(DeviceListBean data) {
        MyApplication.getInstance().setDevicesBean(data);

    }

    @Override
    public void loadAllDeviceDataFail(Throwable throwable) {
        if (throwable != null)
            Log.d(TAG, "loadAllDeviceDataFail: " + throwable.getMessage());
    }

    @Override
    public void loadDeviceLocationSuccess(List<DeviceLocationBean> data) {
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        netErrorView.setVisibility(View.GONE);
        SmartRefreshLayout refreshLayout = contentView.findViewById(R.id.device_refreshLayout);
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
        }
        MyApplication.getInstance().getDevicesLocationBean().clear();
        MyApplication.getInstance().getDevicesLocationBean().addAll(data);
        LocationBeans.clear();
        DeviceLocationBean deviceLocationBean = new DeviceLocationBean();
        deviceLocationBean.setRegionName("全部");
        deviceLocationBean.setRegionId(-1L);
        LocationBeans.add(0, deviceLocationBean);
        LocationBeans.addAll(data);
        ViewPager viewPager = contentView.findViewById(R.id.viewPager);
        MagicIndicator tabLayout = contentView.findViewById(R.id.home_tabLayout);
        if (viewPager != null) {
            viewPager.setAdapter(mAdapter);
            CommonNavigator commonNavigator = new CommonNavigator(getActivity());
            commonNavigator.setAdjustMode(false);
            ScaleTabAdapter adapter = new ScaleTabAdapter(LocationBeans, viewPager, tabLayout);
            commonNavigator.setAdapter(adapter);
            tabLayout.setNavigator(commonNavigator);
            ViewPagerHelper.bind(tabLayout, viewPager);
            viewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void loadDataFinish(Throwable throwable) {
        loadingView.setVisibility(View.GONE);
        netErrorView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.GONE);
        if (throwable != null) {
            CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleDeviceRemoved(DeviceRemovedEvent event) {
//        loadAllData();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleDeviceRemoved(DeviceAddEvent event) {
//        loadAllData();
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleDeviceRemoved(RegionChangeEvent event) {
//        loadAllData();
//    }

/*

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAllData(MoveAllDataEvent event) {
        loadData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getBufenData(MoveBufenDataEvent event) {
        if (deviceListBean!=null){
            for (int x= 0; x<deviceListBean.getDevices().size();x++){
                DeviceListBean.DevicesBean devicesBean = deviceListBean.getDevices().get(x);
                if (devicesBean.getBindType() != 0) {//待绑定设备
                    deviceListBean.getDevices().remove(x);
                }
            }
        }

    }
*/

    private void loadAllData() {
        mPresenter.loadData(room_id);
//        mPresenter.loadGroupDeviceData(room_id, null, null, null);
        mPresenter.getAllDeviceLocation(room_id);
    }

}
