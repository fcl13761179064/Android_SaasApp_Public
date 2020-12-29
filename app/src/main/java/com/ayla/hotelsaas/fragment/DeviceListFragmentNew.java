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
import com.ayla.hotelsaas.ui.DeviceDetailH5Activity;
import com.ayla.hotelsaas.ui.DeviceMoreActivity;
import com.ayla.hotelsaas.ui.TouchPanelActivity;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

        mAdapter = new DeviceListAdapter();
        mAdapter.bindToRecyclerView(binding.deviceRecyclerview);
        mAdapter.setEmptyView(R.layout.layout_loading);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final DeviceListBean.DevicesBean devicesBean = mAdapter.getData().get(position);
                if (devicesBean.getCuId() == 1 && "a1UR1BjfznK".equals(devicesBean.getDeviceCategory())) {
                    Intent intent = new Intent(getContext(), TouchPanelActivity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    intent.putExtra("pannel_type", "1");
                    startActivity(intent);
                } else if (devicesBean.isHasH5()) {
                    Intent intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), DeviceMoreActivity.class);
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void initData() {
        mAdapter.setNewData(devices);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        mAdapter.notifyDataSetChanged();
    }
}
