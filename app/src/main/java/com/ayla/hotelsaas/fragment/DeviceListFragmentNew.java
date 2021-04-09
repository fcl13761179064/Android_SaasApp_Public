package com.ayla.hotelsaas.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.DeviceCategoryHandler;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.databinding.FragmentDeviceListNewBinding;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.mvp.present.DeviceListPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.ui.CustomToast;
import com.ayla.hotelsaas.ui.DeviceDetailH5Activity;
import com.ayla.hotelsaas.ui.DeviceMoreActivity;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragmentNew extends BaseMvpFragment<DeviceListView, DeviceListPresenter> implements DeviceListView {

    private final long regionId;
    private  int  position;
    FragmentDeviceListNewBinding binding;
    List<DeviceListBean.DevicesBean> devices;

    private DeviceListAdapter mAdapter;

    DeviceCategoryHandler deviceCategoryHandler;

    private long room_id;

    public DeviceListFragmentNew(long room_id, List<DeviceListBean.DevicesBean> devices, long regionId, int position) {
        this.room_id = room_id;
        this.devices = devices;
        this.regionId = regionId;
        this.position = position;
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
    protected DeviceListPresenter initPresenter() {
        return new DeviceListPresenter();
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
        deviceCategoryHandler = new DeviceCategoryHandler(this, room_id);

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
        mAdapter.setEmptyView(R.layout.empty_device_order);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final DeviceListBean.DevicesBean devicesBean = mAdapter.getData().get(position).getDevicesBean();
                if (devicesBean.getBindType() == 0) {
                    Intent intent;
                    if (devicesBean.isHasH5()) {
                        intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                    } else {
                        intent = new Intent(getContext(), DeviceMoreActivity.class);
                    }
                    intent.putExtra("deviceId", devicesBean.getDeviceId());
                    intent.putExtra("scopeId", room_id);
                    startActivity(intent);
                } else {//待绑定的设备
                    if (devicesBean.getDeviceUseType() == 1) {//如果是用途设备，跳过
                        return;
                    }
                    mPresenter.loadCategory(devicesBean);
                }
            }
        });
    }

    @Override
    protected void initData() {
        if (devices != null && position==0 ) {
            List<DeviceListAdapter.DeviceItem> deviceItems = new ArrayList<>();
            for (DeviceListBean.DevicesBean devicesBean : devices) {
                DeviceListAdapter.DeviceItem deviceItem = new DeviceListAdapter.DeviceItem(devicesBean);
                deviceItems.add(deviceItem);
            }
            mAdapter.setNewData(deviceItems);
        }else {
            mPresenter.loadData(room_id,regionId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadDataSuccess(DeviceListBean.DevicesBean devicesBean, List<DeviceCategoryBean> data) {
        Intent intent = new Intent();
        Bundle addForWaitBundle = new Bundle();
        addForWaitBundle.putString("waitBindDeviceId", devicesBean.getDeviceId());
        addForWaitBundle.putString("nickname", devicesBean.getNickname());
        addForWaitBundle.putString("pid", devicesBean.getPid());
        intent.putExtra("addForWait", addForWaitBundle);

        deviceCategoryHandler.bindOrReplace(data, intent);//添加待绑定的设备
    }

    @Override
    public void loadDeviceDataSuccess(DeviceListBean data) {
        List<DeviceListAdapter.DeviceItem> deviceItems = new ArrayList<>();
        for (DeviceListBean.DevicesBean devicesBean : data.getDevices()) {
            DeviceListAdapter.DeviceItem deviceItem = new DeviceListAdapter.DeviceItem(devicesBean);
            deviceItems.add(deviceItem);
        }
        mAdapter.setNewData(deviceItems);
    }

    @Override
    public void loadDataFailed(Throwable throwable) {
        CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        deviceCategoryHandler.onActivityResult(requestCode, resultCode, data);
    }
}
