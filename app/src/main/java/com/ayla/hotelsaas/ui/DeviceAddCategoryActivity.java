package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceCategoryListLeftAdapter;
import com.ayla.hotelsaas.adapter.DeviceCategoryListRightAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.DeviceAddCategoryPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @描述 添加设备入口页面，展示产品分类二级列表
 * 进入时必须带上参数scopeId
 * 如果是添加待绑定的设备，还要带上：
 * boolean addForWait
 * waitBindDeviceId
 * deviceCategory
 * deviceName
 * @作者 吴友金
 */
public class DeviceAddCategoryActivity extends BaseMvpActivity<DeviceAddCategoryView, DeviceAddCategoryPresenter> implements DeviceAddCategoryView {
    private final int REQUEST_CODE_ADD_DEVICE = 0X10;
    private final int REQUEST_CODE_SELECT_GATEWAY = 0X11;
    @BindView(R.id.rv_left)
    RecyclerView leftRecyclerView;

    @BindView(R.id.rv_right)
    RecyclerView rightRecyclerView;
    @BindView(R.id.empty_layout)
    View mEmptyView;
    @BindView(R.id.ll_content)
    View mContentView;

    private DeviceCategoryListLeftAdapter mLeftAdapter;
    private DeviceCategoryListRightAdapter mRightAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_add_category_list;
    }

    @Override
    protected void initView() {
        leftRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLeftAdapter = new DeviceCategoryListLeftAdapter(R.layout.item_device_add_category);
        mLeftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                adjustData(position);
            }
        });
        leftRecyclerView.setAdapter(mLeftAdapter);

        mRightAdapter = new DeviceCategoryListRightAdapter(R.layout.item_device_add);
        rightRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        rightRecyclerView.setAdapter(mRightAdapter);
        rightRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int layoutPosition = parent.getChildLayoutPosition(view);
                outRect.top = AutoSizeUtils.dp2px(DeviceAddCategoryActivity.this, 5);
                outRect.bottom = AutoSizeUtils.dp2px(DeviceAddCategoryActivity.this, 5);
                if (layoutPosition < 3) {
                    outRect.top += AutoSizeUtils.dp2px(DeviceAddCategoryActivity.this, 5);
                }
                int count = parent.getAdapter().getItemCount();
                int i = count % 3;
                if (layoutPosition >= (i == 0 ? count - 3 : count - i)) {
                    outRect.bottom += AutoSizeUtils.dp2px(DeviceAddCategoryActivity.this, 5);
                }
            }
        });
    }

    @Override
    protected void initListener() {
        mRightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceCategoryBean.SubBean.NodeBean bean = (DeviceCategoryBean.SubBean.NodeBean) adapter.getItem(position);
                handleAddJump(bean);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadCategory();
    }

    @Override
    protected DeviceAddCategoryPresenter initPresenter() {
        return new DeviceAddCategoryPresenter();
    }

    @Override
    public void showCategory(List<DeviceCategoryBean> deviceCategoryBeans) {
        mContentView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLeftAdapter.setNewData(deviceCategoryBeans);
        adjustData(0);

        boolean addForWait = getIntent().getBooleanExtra("addForWait", false);
        if (addForWait) {
            String deviceCategory = getIntent().getStringExtra("deviceCategory");// TODO: 1/18/21  没找到对应的字段
            String deviceName = getIntent().getStringExtra("deviceName");
            for (DeviceCategoryBean.SubBean.NodeBean nodeBean : mRightAdapter.getData()) {
                if (TextUtils.equals(deviceCategory, nodeBean.getOemModel())
                        && TextUtils.equals(deviceName, nodeBean.getProductModel())) {
                    handleAddJump(nodeBean);
                    return;
                }
            }
            getIntent().removeExtra("addForWait");
            getIntent().removeExtra("waitBindDeviceId");
            getIntent().removeExtra("nickname");
            getIntent().removeExtra("deviceCategory");
            getIntent().removeExtra("deviceName");
        }
    }

    @Override
    public void categoryLoadFail(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
        mContentView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    /**
     * 点击一级菜单后，调整二级菜单显示
     *
     * @param categoryIndex
     */
    private void adjustData(int categoryIndex) {
        if (mLeftAdapter.getData().size() > 0) {
            mLeftAdapter.setSelectedPosition(categoryIndex);
            List<DeviceCategoryBean.SubBean.NodeBean> nodeBeans = new ArrayList<>();
            List<DeviceCategoryBean.SubBean> subBeanList = mLeftAdapter.getData().get(categoryIndex).getSub();
            for (DeviceCategoryBean.SubBean subBean : subBeanList) {
                List<DeviceCategoryBean.SubBean.NodeBean> nodes = subBean.getNode();
                nodeBeans.addAll(nodes);
            }
            mRightAdapter.setNewData(nodeBeans);
        }
    }

    /**
     * 处理点击二级菜单item后的配网页面跳转逻辑
     *
     * @param subBean cuId
     */
    private void handleAddJump(DeviceCategoryBean.SubBean.NodeBean subBean) {

        List<DeviceListBean.DevicesBean> aylaGateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> hyGateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    if (device.getCuId() == 0) {
                        aylaGateways.add(device);
                    } else if (device.getCuId() == 1) {
                        hyGateways.add(device);
                    }
                }
            }
        }

        int networkType = -1;//2：艾拉网关  3：艾拉节点 5：艾拉WiFi   1：鸿雁网关 4：鸿雁节点
        if (subBean.getSource() == 0) {//艾拉云
            if (subBean.getProductType() == 1) {//网关设备
                networkType = 2;//艾拉网关
            } else {//其他设备
                if (subBean.getIsNeedGateway() == 1) {//节点设备
                    networkType = 3;//艾拉节点
                } else {
                    networkType = 5;//艾拉WiFi
                }
            }
        } else if (subBean.getSource() == 1) {//阿里云
            if (subBean.getProductType() == 1) {//网关设备
                networkType = 1;//鸿雁网关
            } else {//其他设备
                if (subBean.getIsNeedGateway() == 1) {//节点设备
                    networkType = 4;//鸿雁节点
                }
            }
        }

        if (networkType == 2) {//艾拉网关
            Intent mainActivity = new Intent(this, AylaGatewayAddGuideActivity.class);
            mainActivity.putExtra("cuId", subBean.getSource());
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("deviceCategory", subBean.getOemModel());
            mainActivity.putExtra("deviceName", subBean.getProductName());
            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
        } else if (networkType == 3) {//跳转艾拉节点
            if (aylaGateways.size() == 0) {//没有艾拉网关
                CustomToast.makeText(this, "该设备无法绑定", R.drawable.ic_toast_warming);
            } else if (aylaGateways.size() == 1) {//一个艾拉网关
                DeviceListBean.DevicesBean gateway = aylaGateways.get(0);
                if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                    Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                    mainActivity.putExtra("networkType", networkType);
                    mainActivity.putExtras(getIntent());
                    mainActivity.putExtra("deviceId", gateway.getDeviceId());
                    mainActivity.putExtra("cuId", gateway.getCuId());
                    mainActivity.putExtra("deviceCategory", subBean.getOemModel());
                    mainActivity.putExtra("deviceName", subBean.getProductName());
                    mainActivity.putExtra("categoryId", subBean.getId());
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                } else {
                    CustomToast.makeText(this, "当前网关离线", R.drawable.ic_toast_warming);
                }
            } else {//多个网关
                Intent mainActivity = new Intent(this, GatewaySelectActivity.class);
                mainActivity.putExtras(getIntent());
                mainActivity.putExtra("networkType", networkType);
                mainActivity.putExtra("cuId", subBean.getSource());
                mainActivity.putExtra("deviceCategory", subBean.getOemModel());
                mainActivity.putExtra("deviceName", subBean.getProductName());
                mainActivity.putExtra("categoryId", subBean.getId());
                startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
            }
        } else if (networkType == 5) {//跳转艾拉wifi
            Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
            mainActivity.putExtra("networkType", networkType);
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("deviceCategory", subBean.getOemModel());
            mainActivity.putExtra("deviceName", subBean.getProductName());
            mainActivity.putExtra("categoryId", subBean.getId());
            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
        } else if (networkType == 1) {//跳转鸿雁网关
            Intent mainActivity = new Intent(this, HongyanGatewayAddGuideActivity.class);
            mainActivity.putExtra("cuId", subBean.getSource());
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("deviceCategory", subBean.getOemModel());
            mainActivity.putExtra("deviceName", subBean.getProductName());
            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
        } else if (networkType == 4) {//跳转鸿雁节点
            if (hyGateways.size() == 0) {//没有鸿雁网关
                CustomToast.makeText(this, "该设备无法绑定", R.drawable.ic_toast_warming);
            } else if (hyGateways.size() == 1) {//一个网关
                DeviceListBean.DevicesBean gateway = hyGateways.get(0);
                if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                    Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                    mainActivity.putExtra("networkType", networkType);
                    mainActivity.putExtras(getIntent());
                    mainActivity.putExtra("deviceId", gateway.getDeviceId());
                    mainActivity.putExtra("cuId", gateway.getCuId());
                    mainActivity.putExtra("deviceCategory", subBean.getOemModel());
                    mainActivity.putExtra("deviceName", subBean.getProductName());
                    mainActivity.putExtra("categoryId", subBean.getId());
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                } else {
                    CustomToast.makeText(this, "当前网关离线", R.drawable.ic_toast_warming);
                }
            } else {//多个网关
                Intent mainActivity = new Intent(this, GatewaySelectActivity.class);
                mainActivity.putExtra("networkType", networkType);
                mainActivity.putExtras(getIntent());
                mainActivity.putExtra("cuId", subBean.getSource());
                mainActivity.putExtra("deviceCategory", subBean.getOemModel());
                mainActivity.putExtra("deviceName", subBean.getProductName());
                mainActivity.putExtra("categoryId", subBean.getId());
                startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_DEVICE) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                boolean addForWait = getIntent().getBooleanExtra("addForWait", false);
                if (addForWait) {
                    finish();
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_GATEWAY) {
            if (resultCode == RESULT_OK) {
                Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                mainActivity.putExtras(data);
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else {
                boolean addForWait = getIntent().getBooleanExtra("addForWait", false);
                if (addForWait) {
                    finish();
                }
            }
        }
    }

    @OnClick(R.id.bt_refresh)
    void handleRefresh() {
        mContentView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mPresenter.loadCategory();
    }
}