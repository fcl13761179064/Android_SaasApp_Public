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
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @描述 添加设备入口页面，展示产品分类二级列表
 * 进入时必须带上参数scopeId
 * <p>
 * 补全待绑定设备时，必须传入
 * {@link Bundle addForWait} include:waitBindDeviceId、nickname、pid
 * 替换设备时，必须传入
 * {@link Bundle replaceInfo} include:replaceDeviceId、targetGatewayDeviceId
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

    private long scopeId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_add_category_list;
    }

    @Override
    protected void initView() {
        scopeId = getIntent().getLongExtra("scopeId", 0);
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
                List<DeviceCategoryBean.SubBean.NodeBean> items = (List<DeviceCategoryBean.SubBean.NodeBean>) adapter.getItem(position);
                DeviceCategoryBean.SubBean.NodeBean[] nodeBeans = items.toArray(new DeviceCategoryBean.SubBean.NodeBean[]{});
                handleAddJump(nodeBeans);
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

        Bundle addForWaitBundle = getIntent().getBundleExtra("addForWait");
        if (addForWaitBundle != null) {//绑定待添加设备逻辑
            String pid = addForWaitBundle.getString("pid");
            for (DeviceCategoryBean deviceCategoryBean : deviceCategoryBeans) {
                for (DeviceCategoryBean.SubBean subBean : deviceCategoryBean.getSub()) {
                    for (DeviceCategoryBean.SubBean.NodeBean nodeBean : subBean.getNode()) {
                        if (TextUtils.equals(pid, nodeBean.getPid())) {
                            handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{nodeBean});
                            return;
                        }
                    }
                }
            }
            //如果跳转了品类的添加，但是又取消了添加设备，就需要判断是否存在addForWait ，如果是，就要结束这个页面。
            getIntent().removeExtra("addForWait");
        }
        Bundle replaceInfoBundle = getIntent().getBundleExtra("replaceInfo");
        if (replaceInfoBundle != null) {//替换设备逻辑
            String replaceDeviceId = replaceInfoBundle.getString("replaceDeviceId");
            DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(replaceDeviceId);
            for (DeviceCategoryBean deviceCategoryBean : deviceCategoryBeans) {
                for (DeviceCategoryBean.SubBean subBean : deviceCategoryBean.getSub()) {
                    for (DeviceCategoryBean.SubBean.NodeBean nodeBean : subBean.getNode()) {
                        if (TextUtils.equals(nodeBean.getPid(), devicesBean.getPid())) {
                            handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{nodeBean});
                            return;
                        }
                    }
                }
            }
            getIntent().removeExtra("replaceInfo");
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
            List<List<DeviceCategoryBean.SubBean.NodeBean>> nodeBeans = new ArrayList<>();
            List<DeviceCategoryBean.SubBean> subBeanList = mLeftAdapter.getData().get(categoryIndex).getSub();
            for (DeviceCategoryBean.SubBean subBean : subBeanList) {
                List<DeviceCategoryBean.SubBean.NodeBean> nodes = subBean.getNode();
                for (DeviceCategoryBean.SubBean.NodeBean node : nodes) {
                    nodeBeans.add(new ArrayList<>(Arrays.asList(node)));
                }
            }

            List<List<DeviceCategoryBean.SubBean.NodeBean>> result = new ArrayList<>();
            s1:
            for (List<DeviceCategoryBean.SubBean.NodeBean> nodeBean : nodeBeans) {
                DeviceCategoryBean.SubBean.NodeBean nodeBean1 = nodeBean.get(0);
                for (List<DeviceCategoryBean.SubBean.NodeBean> beans : result) {
                    DeviceCategoryBean.SubBean.NodeBean nodeBean2 = beans.get(0);
                    if (nodeBean1.getIsNeedGateway() == 1 && nodeBean2.getIsNeedGateway() == 1) {
                        if (TextUtils.equals(nodeBean1.getProductName(), nodeBean2.getProductName())) {
                            beans.add(nodeBean1);
                            continue s1;
                        }
                    }
                }
                result.add(nodeBean);
            }
            mRightAdapter.setNewData(result);
        }
    }

    /**
     * 处理点击二级菜单item后的配网页面跳转逻辑
     */
    private void handleAddJump(DeviceCategoryBean.SubBean.NodeBean[] subBeans) {
        Bundle _replaceInfo = getIntent().getBundleExtra("replaceInfo");
        String replaceNodeGatewayId = null;//需要替换设备时，节点所在的网关id
        if (_replaceInfo != null) {
            replaceNodeGatewayId = _replaceInfo.getString("targetGatewayDeviceId");
        }

        List<DeviceListBean.DevicesBean> aylaGateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> hyGateways = new ArrayList<>();
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        for (DeviceListBean.DevicesBean device : devicesBean) {
            if (TempUtils.isDeviceGateway(device) && (TextUtils.isEmpty(replaceNodeGatewayId) || TextUtils.equals(device.getDeviceId(), replaceNodeGatewayId))) {
                if (device.getCuId() == 0) {
                    aylaGateways.add(device);
                } else if (device.getCuId() == 1) {
                    hyGateways.add(device);
                }
            }
        }

        if (subBeans.length == 1) {
            DeviceCategoryBean.SubBean.NodeBean subBean = subBeans[0];
            int networkType = calculateNetworkType(subBean);

            Bundle addInfo = new Bundle();
            addInfo.putInt("networkType", networkType);
            addInfo.putInt("cuId", subBean.getSource());
            addInfo.putLong("scopeId", scopeId);
            addInfo.putString("pid", subBean.getPid());
            addInfo.putString("deviceCategory", subBean.getOemModel());
            addInfo.putString("productName", subBean.getProductName());
            Bundle addForWait = getIntent().getBundleExtra("addForWait");
            if (addForWait != null) {
                addInfo.putString("waitBindDeviceId", addForWait.getString("waitBindDeviceId"));
                addInfo.putString("nickname", addForWait.getString("nickname"));
            }
            Bundle replaceInfo = getIntent().getBundleExtra("replaceInfo");
            if (replaceInfo != null) {
                addInfo.putString("replaceDeviceId", replaceInfo.getString("replaceDeviceId"));
            }

            if (networkType == 2) {//艾拉网关
                Intent mainActivity = new Intent(this, AylaGatewayAddGuideActivity.class);
                mainActivity.putExtra("addInfo", addInfo);
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else if (networkType == 3) {//跳转艾拉节点
                if (aylaGateways.size() == 0) {//没有艾拉网关
                    CustomToast.makeText(this, "请先绑定网关", R.drawable.ic_toast_warming);
                } else if (aylaGateways.size() == 1) {//一个艾拉网关
                    DeviceListBean.DevicesBean gateway = aylaGateways.get(0);
                    if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                        Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                        addInfo.putString("deviceId", gateway.getDeviceId());
                        mainActivity.putExtra("addInfo", addInfo);
                        startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                    } else {
                        CustomToast.makeText(this, "当前网关离线", R.drawable.ic_toast_warming);
                    }
                } else {//多个网关
                    Intent mainActivity = new Intent(this, GatewaySelectActivity.class);
                    mainActivity.putExtra("addInfo", addInfo);
                    mainActivity.putExtra("sourceId", subBean.getSource());
                    startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                }
            } else if (networkType == 5) {//跳转艾拉wifi
                Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                mainActivity.putExtra("addInfo", addInfo);
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else if (networkType == 1) {//跳转鸿雁网关
                Intent mainActivity = new Intent(this, HongyanGatewayAddGuideActivity.class);
                mainActivity.putExtra("addInfo", addInfo);
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else if (networkType == 4) {//跳转鸿雁节点
                if (hyGateways.size() == 0) {//没有鸿雁网关
                    CustomToast.makeText(this, "请先绑定网关", R.drawable.ic_toast_warming);
                } else if (hyGateways.size() == 1) {//一个网关
                    DeviceListBean.DevicesBean gateway = hyGateways.get(0);
                    if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                        Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);
                        addInfo.putString("deviceId", gateway.getDeviceId());
                        mainActivity.putExtra("addInfo", addInfo);
                        startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                    } else {
                        CustomToast.makeText(this, "当前网关离线", R.drawable.ic_toast_warming);
                    }
                } else {//多个网关
                    Intent mainActivity = new Intent(this, GatewaySelectActivity.class);
                    mainActivity.putExtra("addInfo", addInfo);
                    mainActivity.putExtra("sourceId", subBean.getSource());
                    startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                }
            }
        } else if (subBeans.length == 2) {
            DeviceCategoryBean.SubBean.NodeBean nodeBean1 = subBeans[0];
            DeviceCategoryBean.SubBean.NodeBean nodeBean2 = subBeans[1];
            if (nodeBean1.getIsNeedGateway() == 1 && nodeBean2.getIsNeedGateway() == 1 && nodeBean1.getSource() != nodeBean2.getSource()) {
                DeviceCategoryBean.SubBean.NodeBean aylaNodeBean = null;//待绑定的艾拉节点描述
                DeviceCategoryBean.SubBean.NodeBean hyNodeBean = null;//待绑定的鸿雁节点描述
                if (nodeBean1.getSource() == 0) {
                    aylaNodeBean = nodeBean1;
                } else if (nodeBean1.getSource() == 1) {
                    hyNodeBean = nodeBean1;
                }
                if (nodeBean2.getSource() == 0) {
                    aylaNodeBean = nodeBean2;
                } else if (nodeBean2.getSource() == 1) {
                    hyNodeBean = nodeBean2;
                }
                if (aylaNodeBean != null && hyNodeBean != null) {//一个icon 表示 两种 节点设备，一个鸿雁、一个艾拉。
                    if (aylaGateways.size() == 0 && hyGateways.size() == 0) {//没有绑定过任何网关
                        CustomToast.makeText(this, "请先绑定网关", R.drawable.ic_toast_warming);
                        return;
                    } else if (aylaGateways.size() + hyGateways.size() > 1) {//当前存在多个网关 ,跳转到网关选择页面
                        Intent mainActivity = new Intent(this, GatewaySelectActivity.class);
                        ArrayList<DeviceCategoryBean.SubBean.NodeBean> nodeBeans = new ArrayList<>();
                        nodeBeans.add(aylaNodeBean);
                        nodeBeans.add(hyNodeBean);
                        mainActivity.putExtra("waitCategoryNodes", nodeBeans);
                        if (aylaGateways.size() == 0) {
                            mainActivity.putExtra("sourceId", hyGateways.get(0).getCuId());
                        } else if (hyGateways.size() == 0) {
                            mainActivity.putExtra("sourceId", aylaGateways.get(0).getCuId());
                        }
                        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_GATEWAY);
                    } else {//只有一个网关
                        DeviceCategoryBean.SubBean.NodeBean forAddNode;
                        if (aylaGateways.size() == 0) {
                            forAddNode = hyNodeBean;
                        } else {
                            forAddNode = aylaNodeBean;
                        }
                        handleAddJump(new DeviceCategoryBean.SubBean.NodeBean[]{forAddNode});
                    }
                }
            }
        }
    }

    private int calculateNetworkType(DeviceCategoryBean.SubBean.NodeBean subBean) {
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
        return networkType;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_DEVICE) {
            if (resultCode == RESULT_OK) {
                finish();
            } else {
                if (getIntent().hasExtra("addForWait")) {
                    finish();
                    return;
                }
                if (getIntent().hasExtra("replaceInfo")) {
                    finish();
                    return;
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_GATEWAY) {
            if (resultCode == RESULT_OK) {
                String deviceId = data.getStringExtra("deviceId");

                Intent mainActivity = new Intent(this, DeviceAddGuideActivity.class);

                if (data.hasExtra("addInfo")) {
                    mainActivity.putExtra("addInfo", data.getBundleExtra("addInfo"));
                } else if (data.hasExtra("waitCategoryNodes")) {
                    List<DeviceCategoryBean.SubBean.NodeBean> nodes = (List<DeviceCategoryBean.SubBean.NodeBean>) data.getSerializableExtra("waitCategoryNodes");

                    DeviceListBean.DevicesBean gatewayDevice = MyApplication.getInstance().getDevicesBean(deviceId);
                    for (DeviceCategoryBean.SubBean.NodeBean subBean : nodes) {
                        if (subBean.getSource() == gatewayDevice.getCuId()) {
                            Bundle addInfo = new Bundle();
                            addInfo.putInt("networkType", calculateNetworkType(subBean));
                            addInfo.putInt("cuId", subBean.getSource());
                            addInfo.putLong("scopeId", scopeId);
                            addInfo.putString("pid", subBean.getPid());
                            addInfo.putString("deviceCategory", subBean.getOemModel());
                            addInfo.putString("productName", subBean.getProductName());
                            Bundle addForWait = getIntent().getBundleExtra("addForWait");
                            if (addForWait != null) {
                                addInfo.putString("waitBindDeviceId", addForWait.getString("waitBindDeviceId"));
                                addInfo.putString("nickname", addForWait.getString("nickname"));
                            }
                            Bundle replaceInfo = getIntent().getBundleExtra("replaceInfo");
                            if (replaceInfo != null) {
                                addInfo.putString("replaceDeviceId", replaceInfo.getString("replaceDeviceId"));
                            }

                            mainActivity.putExtra("addInfo", addInfo);
                            break;
                        }
                    }
                }
                Bundle addInfo = mainActivity.getBundleExtra("addInfo");
                addInfo.putString("deviceId", deviceId);

                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else {
                if (getIntent().hasExtra("addForWait")) {
                    finish();
                    return;
                }
                if (getIntent().hasExtra("replaceInfo")) {
                    finish();
                    return;
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