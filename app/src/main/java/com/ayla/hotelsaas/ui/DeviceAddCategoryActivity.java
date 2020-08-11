package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.iot.aep.component.router.Router;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClient;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
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
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @描述 添加设备入口页面，展示产品分类二级列表
 * 进入时必须带上参数scopeId
 * @作者 吴友金
 */
public class DeviceAddCategoryActivity extends BaseMvpActivity<DeviceAddCategoryView, DeviceAddCategoryPresenter> implements DeviceAddCategoryView {
    private final int REQUEST_CODE_ADD_DEVICE = 0X10;
    private final int REQUEST_CODE_HONEYAN_ROUTE = 0X11;
    @BindView(R.id.rv_left)
    RecyclerView leftRecyclerView;

    @BindView(R.id.rv_right)
    RecyclerView rightRecyclerView;

    private DeviceCategoryListLeftAdapter mLeftAdapter;
    private DeviceCategoryListRightAdapter mRightAdapter;
    private String mIotId = "100";
    private DeviceCategoryBean.SubBean mSubBean;

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
                DeviceCategoryBean.SubBean bean = (DeviceCategoryBean.SubBean) adapter.getItem(position);
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
        mLeftAdapter.setNewData(deviceCategoryBeans);
        adjustData(0);
    }

    /**
     * 点击一级菜单后，调整二级菜单显示
     *
     * @param categoryIndex
     */
    private void adjustData(int categoryIndex) {
        if (mLeftAdapter.getData().size() > 0) {
            mLeftAdapter.setSelectedPosition(categoryIndex);
            mRightAdapter.setNewData(mLeftAdapter.getData().get(categoryIndex).getSub());
        }
    }

    /**
     * 处理点击二级菜单item后的配网页面跳转逻辑
     *
     * @param subBean
     */
    private void handleAddJump(DeviceCategoryBean.SubBean subBean) {
        int gatewayCount = 0;
        DeviceListBean.DevicesBean gateway = null;
        List<DeviceListBean.DevicesBean> devicesBean = MyApplication.getInstance().getDevicesBean();
        if (devicesBean != null) {
            for (DeviceListBean.DevicesBean device : devicesBean) {
                if (TempUtils.isDeviceGateway(device)) {
                    gatewayCount++;
                    gateway = device;
                }
            }
        }
        if (2 == subBean.getNetworkType()) {//顺舟和ayla-插网线网关配网
            if (gatewayCount == 0) {//没有网关
                Intent mainActivity = new Intent(this, GatewayAddGuideActivity.class);
                mainActivity.putExtra("cuId", subBean.getCuId());
                mainActivity.putExtras(getIntent());
                mainActivity.putExtra("deviceName", subBean.getDeviceName());
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            } else {
                CustomToast.makeText(this, "只能绑定一个网关", R.drawable.ic_toast_warming).show();
            }
        } else if (3 == subBean.getNetworkType()) {//跳转艾拉节点添加
            if (gatewayCount == 0) {//没有网关
                CustomToast.makeText(this, "请先绑定网关", R.drawable.ic_toast_warming).show();
            } else if (gatewayCount == 1) {//一个网关
                if (TempUtils.isDeviceOnline(gateway)) {//网关在线
                    Intent mainActivity = new Intent(this, ZigBeeAddGuideActivity.class);
                    mainActivity.putExtra("deviceId", gateway.getDeviceId());
                    mainActivity.putExtra("cuId", gateway.getCuId());
                    mainActivity.putExtras(getIntent());
                    mainActivity.putExtra("deviceName", subBean.getDeviceName());
                    startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
                } else {
                    CustomToast.makeText(this, "当前网关离线", R.drawable.ic_toast_warming).show();
                }
            } else {//多个网关
                Intent mainActivity = new Intent(this, ZigBeeAddSelectGatewayActivity.class);
                mainActivity.putExtras(getIntent());
                mainActivity.putExtra("deviceName", subBean.getDeviceName());
                startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
            }
        } else if (1 == subBean.getNetworkType()) {
            //跳转到阿里云的鸿雁网关设备
            Intent mainActivity = new Intent(this, HongyanGatewayAddGuideActivity.class);
            mainActivity.putExtra("cuId", subBean.getCuId());
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("deviceName", subBean.getDeviceName());
            mainActivity.putExtra("productKey", subBean.getOemModel());
            mainActivity.putExtra("is_getway", true);
            startActivityForResult(mainActivity, REQUEST_CODE_ADD_DEVICE);
        } else if (4 == subBean.getNetworkType()) {//跳转鸿雁节点添加
            this.mSubBean = subBean;
            HongyanZigBeeAddGuideActivity(subBean.getOemModel());
            //bindVirturalZigbeeToUser("a1ZPeSFEOFO","000D6F001066E3C2");
        }
    }


    //基于时间窗口的方式绑定设备
    private void bindVirturalZigbeeToUser(String pk, String dn) {
        Map<String, Object> maps = new HashMap<>();
        maps.put("productKey", pk);
        maps.put("deviceName", dn);
        IoTRequestBuilder builder = new IoTRequestBuilder()
                .setPath("/awss/time/window/user/bind")
                .setApiVersion("1.0.8")
                .setAuthType("iotAuth")
                .setParams(maps);

        IoTRequest request = builder.build();

        IoTAPIClient ioTAPIClient = new IoTAPIClientFactory().getClient();
        ioTAPIClient.send(request, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {

            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                final int code = ioTResponse.getCode();
                final JsonObject data = (JsonObject) ioTResponse.getData();
                final JsonObject iotId = data.getAsJsonObject("iotId");
                final byte[] rawData = ioTResponse.getRawData();
                if (code == 200) {

                }
            }
        });
    }


    private void HongyanZigBeeAddGuideActivity(String productKey) {
        // 启动插件
        Bundle bundle = new Bundle();
        bundle.putString("productKey", productKey);
        Router.getInstance().toUrlForResult(this, "link://router/connectConfig", REQUEST_CODE_HONEYAN_ROUTE, bundle);
    }

    // 接收配网结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_HONEYAN_ROUTE && resultCode == RESULT_OK) {
            String productKey = data.getStringExtra("productKey");
            String deviceName = data.getStringExtra("deviceName");
            // 配网成功
            Log.d("onResponse_HONGYAN_four", "productKey:" + productKey + "  deviceName:" + deviceName);
            if (TextUtils.isEmpty(productKey) && TextUtils.isEmpty(deviceName)) {
                return;
            }
            Intent intent = new Intent(DeviceAddCategoryActivity.this, HongyanGatewayAddActivity.class);
            intent.putExtra("HongyanproductKey", productKey);
            intent.putExtra("is_getway", "false");
            intent.putExtra("HongyandeviceName", deviceName);
            intent.putExtra("deviceName", mSubBean.getDeviceName());
            intent.putExtra("productKey", mSubBean.getOemModel());
            intent.putExtra("cuId", mSubBean.getCuId());
            intent.putExtras(getIntent());
            intent.putExtras(getIntent());
            startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE);
        }
        if (requestCode == REQUEST_CODE_ADD_DEVICE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}