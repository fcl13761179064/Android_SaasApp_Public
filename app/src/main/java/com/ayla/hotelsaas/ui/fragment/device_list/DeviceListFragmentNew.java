package com.ayla.hotelsaas.ui.fragment.device_list;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.DeviceCategoryHandler;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.bean.BaseDevice;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceItem;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.databinding.FragmentDeviceListNewBinding;
import com.ayla.hotelsaas.events.AllAddDeviceEvent;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.DeviceRemovedEvent;
import com.ayla.hotelsaas.events.GroupDeleteEvent;
import com.ayla.hotelsaas.events.GroupUpdateEvent;
import com.ayla.hotelsaas.events.LocationChangedEvent;
import com.ayla.hotelsaas.events.RegionChangeEvent;
import com.ayla.hotelsaas.events.TobeAddDeviceEvent;
import com.ayla.hotelsaas.mvp.present.DeviceListPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceListView;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.ui.activities.DeviceDetailH5Activity;
import com.ayla.hotelsaas.ui.activities.DeviceMoreActivity;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.SizeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class DeviceListFragmentNew extends BaseMvpFragment<DeviceListView, DeviceListPresenter> implements DeviceListView {


    private List<BaseDevice> mDevices;

    FragmentDeviceListNewBinding binding;

    private DeviceListAdapter mAdapter;

    DeviceCategoryHandler deviceCategoryHandler;

    private long mRoomId;
    private long businessId;
    private int mPosition;
    private long mRegionId;
    private List<BaseDevice> deviceItems;
    public static boolean isAllData = true;
    Long maxDeviceId = null;
    Long maxGroupId = null;
    private String mRoom_name;
    private String move_wall_type;

    public DeviceListFragmentNew() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle b = getArguments();
        mRoomId = b.getLong("mRoomId", 0);
        businessId = b.getLong("businessId", 0);
        mRegionId = b.getLong("mRegionId", 0);
        mDevices = (List<BaseDevice>) b.getSerializable("mDevices");
        mPosition = b.getInt("mPosition", 0);
        mRoom_name = b.getString("roomName");
        move_wall_type = b.getString("move_wall_type");
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
        deviceCategoryHandler = new DeviceCategoryHandler(this, mRoomId);
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
        if (mRegionId == -1) {
            mAdapter.setOnLoadMoreListener(() -> {
                loadData(maxDeviceId, maxGroupId);
            }, binding.deviceRecyclerview);
        }
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            final BaseDevice devicesBean = mAdapter.getData().get(position);
            Intent intent = null;
            if (devicesBean instanceof DeviceItem) {
                if (devicesBean.getBindType() == 0) {
                    if (((DeviceItem) devicesBean).isHasH5()) {//判断是否是h5页面
                        intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                    } else {//更多页面
                        intent = new Intent(getContext(), DeviceMoreActivity.class);
                        intent.putExtra("businessId", businessId);
                        intent.putExtra("roomName", mRoom_name);
                        intent.putExtra("move_wall_type", move_wall_type);
                    }
                    intent.putExtra(KEYS.PRODUCTLABEL, ((DeviceItem) devicesBean).getProductLabel());
                    intent.putExtra("deviceId", ((DeviceItem) devicesBean).getDeviceId());
                    intent.putExtra("scopeId", mRoomId);
                    intent.putExtra("domainUrl", ((DeviceItem) devicesBean).getDomain());
                    intent.putExtra("h5url", ((DeviceItem) devicesBean).getH5Url());
                    intent.putExtra("DevicePid", ((DeviceItem) devicesBean).getPid());
                } else {
                    if (((DeviceItem) devicesBean).getDeviceUseType() == 1) {//如果是用途设备，跳过
                        return;
                    }
                    mPresenter.loadCategory(((DeviceItem) devicesBean), ((DeviceItem) devicesBean).getPid());
                }
            } else if (devicesBean instanceof GroupItem) {
                intent = new Intent(getContext(), DeviceDetailH5Activity.class);
                intent.putExtra("groupId", ((GroupItem) devicesBean).getGroupId());
                intent.putExtra("groupName", ((GroupItem) devicesBean).getGroupName());
                intent.putExtra("scopeId", mRoomId);
                intent.putExtra("group_url", ConstantValue.Group_H5_Url);
                intent.putExtra(KEYS.PRODUCTLABEL, ((GroupItem) devicesBean).getProductLabels());
            }
            if (intent != null) {
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        refreshData();
    }

    public void loadData(Long maxDeviceId, Long maxGroupId) {
        mPresenter.loadData(mRoomId, maxDeviceId, maxGroupId, mRegionId == -1 ? null : mRegionId);

    }

    public void refreshLoadData(Long maxDeviceId, Long maxGroupId) {
        mPresenter.refreshDeviceAndGroup(mRoomId, maxDeviceId, maxGroupId, mRegionId == -1 ? null : mRegionId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceChangedEvent(DeviceChangedEvent event) {
        if (!TextUtils.isEmpty(event.deviceId)) {
            List<BaseDevice> data = mAdapter.getData();
            for (int i = 0; i < data.size(); i++) {
                BaseDevice baseDevice = data.get(i);
                if (baseDevice instanceof DeviceItem) {
                    if (TextUtils.equals(((DeviceItem) baseDevice).getDeviceId(), event.deviceId)) {
                        ((DeviceItem) baseDevice).setNickname(event.newName);
                        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(event.deviceId);
                        devicesBean.setNickname(event.newName);
                        mAdapter.notifyItemChanged(i);
                        break;
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleLocationChangedEvent(LocationChangedEvent event) {
        refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleRemoveDeviceEvent(DeviceRemovedEvent event) {
        if (!TextUtils.isEmpty(event.deviceId)) {
            refreshData();
        }
    }

    @Override
    public void loadDataSuccess(DeviceItem devicesBean, List<DeviceCategoryBean> data, DeviceCategoryBean.SubBean.NodeBean deviceNodeBean) {
        Intent intent = new Intent();
        Bundle addForWaitBundle = new Bundle();
        addForWaitBundle.putString("waitBindDeviceId", devicesBean.getDeviceId());
        addForWaitBundle.putString("nickname", devicesBean.getNickname());
        addForWaitBundle.putString("pid", devicesBean.getPid());
        intent.putExtra("addForWait", addForWaitBundle);

        deviceCategoryHandler.bindOrReplace(data, intent, deviceNodeBean);//添加待绑定的设备
    }

    @Override
    public void loadDeviceDataSuccess(List<BaseDevice> data) {
        deviceItems = new ArrayList<>();
        for (BaseDevice devicesBean : data) {
            if (isAllData) {
                deviceItems.add(devicesBean);
            } else {
                if (devicesBean.getBindType() == 1) {
                    deviceItems.add(devicesBean);
                }
            }
        }
        //全部设备 进行分页，按区域的不分页
        if (mRegionId == -1) {
            if (maxGroupId == null && maxDeviceId == null)
                mAdapter.setNewData(deviceItems);
            else {
                mAdapter.loadMoreComplete();
                mAdapter.addData(deviceItems);
            }
            if (data.size() > 0) {
                mAdapter.setEnableLoadMore(true);
                for (int i = 0; i < data.size(); i++) {
                    BaseDevice item = data.get(i);
                    if (item instanceof DeviceItem) {
                        maxDeviceId = item.id;
                    } else if (item instanceof GroupItem) {
                        maxGroupId = item.id;
                    }
                }

            } else {
                mAdapter.loadMoreEnd();
                mAdapter.setEnableLoadMore(false);
            }
        } else mAdapter.setNewData(deviceItems);

    }

    @Override
    public void loadDataFailed(Throwable throwable) {
        if (maxGroupId != null || maxDeviceId != null) {
            mAdapter.loadMoreFail();
        }
        CustomToast.makeText(getContext(), TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        deviceCategoryHandler.onActivityResult(requestCode, resultCode, data);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AllAddDeviceEvent(AllAddDeviceEvent event) {
        isAllData = true;

//        List<BaseDevice> deviceItems = new ArrayList<>();
//        for (BaseDevice devicesBean : mAdapter.getData()) {
//            if (devicesBean instanceof DeviceItem) {
//                if (((DeviceItem) devicesBean).getRegionId() == mRegionId || mRegionId == -1) {
//                    deviceItems.add(devicesBean);
//                }
//            } else if (devicesBean instanceof GroupItem){
////                if (((GroupItem) devicesBean).getRegionId() == mRegionId || mRegionId == -1) {
////                    deviceItems.add(devicesBean);
////                }
//            }
//
//        }
//        mAdapter.setNewData(deviceItems);
        refreshData();
//        loadData(null, null);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void TobeAddDeviceEvent(TobeAddDeviceEvent event) {
        isAllData = false;

        List<BaseDevice> deviceItems = new ArrayList<>();
        for (BaseDevice devicesBean : mAdapter.getData()) {
            if (devicesBean instanceof DeviceItem) {
                if (devicesBean.getBindType() == 1 && (((DeviceItem) devicesBean).getRegionId() == mRegionId || mRegionId == -1)) {
                    deviceItems.add(devicesBean);
                }
            } else {
                //TODO  编组
            }

        }
        mAdapter.setNewData(deviceItems);
    }

    /**
     * 编组名称更新时，更新列表名称
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleUpdateGroup(GroupUpdateEvent event) {
        if (mAdapter == null)
            return;
        refreshData();
        //编组设备数量变化了，刷新列表，更新状态
//        if (event.updateDeviceCount)
//            refreshData();
//        else {
//            if (!TextUtils.isEmpty(event.groupId)) {
//                List<BaseDevice> data = mAdapter.getData();
//                for (int i = 0; i < data.size(); i++) {
//                    BaseDevice baseDevice = data.get(i);
//                    if (baseDevice instanceof GroupItem) {
//                        if (TextUtils.equals(((GroupItem) baseDevice).getGroupId(), event.groupId)) {
//                            ((GroupItem) baseDevice).setGroupName(event.groupName);
//                            mAdapter.notifyItemChanged(i);
//                            break;
//                        }
//                    }
//                }
//            }
//        }
    }

    private void refreshData() {
        maxGroupId = null;
        maxDeviceId = null;
        refreshLoadData(null, null);
//        loadData(null, null);
    }

    /**
     * 编组删除时
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeleteGroupEvent(GroupDeleteEvent event) {
        if (mAdapter == null)
            return;
        if (!TextUtils.isEmpty(event.groupId)) {
            //删除编组时，需要显示编组下的设备，所以这里只能进行刷新操作
            refreshData();
//            List<BaseDevice> data = mAdapter.getData();
//            for (int i = 0; i < data.size(); i++) {
//                BaseDevice baseDevice = data.get(i);
//                if (baseDevice instanceof GroupItem) {
//                    if (TextUtils.equals(((GroupItem) baseDevice).getGroupId(), event.groupId)) {
//                        data.remove(i);
//                        mAdapter.notifyItemRemoved(i);
//                        break;
//                    }
//                }
//            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceAdd(DeviceAddEvent event) {
        refreshData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleDeviceRegionChange(RegionChangeEvent event) {
        refreshData();
    }
}
