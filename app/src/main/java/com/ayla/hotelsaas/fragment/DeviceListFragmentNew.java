package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.FragmentDeviceListNewBinding;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.ui.DeviceAddCategoryActivity;
import com.ayla.hotelsaas.ui.DeviceDetailH5Activity;
import com.ayla.hotelsaas.ui.DeviceMoreActivity;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragmentNew extends BaseMvpFragment {
    FragmentDeviceListNewBinding binding;
    List<DeviceListBean.DevicesBean> devices;

    private DeviceListAdapter mAdapter;

    private long room_id;

    public DeviceListFragmentNew(long room_id, List<DeviceListBean.DevicesBean> devices) {
        this.room_id = room_id;
        this.devices = devices;
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
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = FragmentDeviceListNewBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView(View view) {
        binding.deviceRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.deviceRecyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });

        mAdapter = new DeviceListAdapter(null);
        mAdapter.bindToRecyclerView(binding.deviceRecyclerview);
        mAdapter.setEmptyView(R.layout.layout_loading);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final DeviceListBean.DevicesBean devicesBean = mAdapter.getData().get(position).getDevicesBean();
                Intent intent;
                if (devicesBean.getBindType() == 0) {
                    if (devicesBean.isHasH5()) {
                        intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                    } else {
                        intent = new Intent(getContext(), DeviceMoreActivity.class);
                    }
                } else {
                    if (devicesBean.getDeviceUseType() == 1) {
                        return;
                    }
                    intent = new Intent(getContext(), DeviceAddCategoryActivity.class);
                    intent.putExtra("addForWait", true);
                    intent.putExtra("waitBindDeviceId", devicesBean.getDeviceId());
                    intent.putExtra("deviceName", devicesBean.getDeviceName());
                    intent.putExtra("deviceCategory", devicesBean.getDeviceCategory());
                }
                intent.putExtra("deviceId", devicesBean.getDeviceId());
                intent.putExtra("scopeId", room_id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        if (devices != null) {
            List<DeviceListAdapter.DeviceItem> deviceItems = new ArrayList<>();
            for (DeviceListBean.DevicesBean devicesBean : devices) {
                DeviceListAdapter.DeviceItem deviceItem = new DeviceListAdapter.DeviceItem(devicesBean);
                deviceItems.add(deviceItem);
            }
            mAdapter.setNewData(deviceItems);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        mAdapter.notifyDataSetChanged();
    }
}
