package com.ayla.hotelsaas.ui;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ayla.hotelsaas.utils.StatusBarUtil;
import com.ayla.hotelsaas.utils.TempUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @描述 添加设备入口页面，展示产品分类二级列表
 * 进入时必须带上参数scopeId
 * @作者 吴友金
 */
public class DeviceAddCategoryActivity extends BaseMvpActivity<DeviceAddCategoryView, DeviceAddCategoryPresenter> implements DeviceAddCategoryView {

    @BindView(R.id.rv_left)
    RecyclerView leftRecyclerView;

    @BindView(R.id.rv_right)
    RecyclerView rightRecyclerView;

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
        mRightAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DeviceCategoryBean.SubBean bean = (DeviceCategoryBean.SubBean) adapter.getItem(position);
                handleAddJump(bean);
            }
        });
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
        if (2 == subBean.getNetworkType()) {//跳转艾拉网关添加
            if (gatewayCount == 0) {//没有网关
                Intent mainActivity = new Intent(this, GatewayAddGuideActivity.class);
                mainActivity.putExtra("cuId", subBean.getCuId());
                mainActivity.putExtra("scopeId", getIntent().getLongExtra("scopeId", 0));
                mainActivity.putExtra("deviceName", subBean.getDeviceName());
                startActivityForResult(mainActivity, 0);
            }else{
                CustomToast.makeText(this, "只能绑定一个网关", R.drawable.ic_toast_warming).show();
            }
        } else if (3 == subBean.getNetworkType()) {//跳转艾拉节点添加
            if (gatewayCount == 0) {//没有网关
                CustomToast.makeText(this, "请先绑定网关", R.drawable.ic_toast_warming).show();
            } else if (gatewayCount == 1) {//一个网关
                Intent mainActivity = new Intent(this, ZigBeeAddGuideActivity.class);
                mainActivity.putExtra("deviceId", gateway.getDeviceId());
                mainActivity.putExtra("cuId", gateway.getCuId());
                mainActivity.putExtra("scopeId", getIntent().getLongExtra("scopeId", 0));
                mainActivity.putExtra("deviceName", subBean.getDeviceName());
                startActivityForResult(mainActivity, 0);
            } else {//多个网关
                Intent mainActivity = new Intent(this, ZigBeeAddSelectGatewayActivity.class);
                mainActivity.putExtra("scopeId", getIntent().getLongExtra("scopeId", 0));
                mainActivity.putExtra("deviceName", subBean.getDeviceName());
                startActivityForResult(mainActivity, 0);
            }
        }else{
            CustomToast.makeText(this, "暂不支持该设备", R.drawable.ic_toast_warming).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
