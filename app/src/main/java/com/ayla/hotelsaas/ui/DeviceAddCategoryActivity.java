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

import com.ayla.hotelsaas.DeviceCategoryHandler;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceCategoryListLeftAdapter;
import com.ayla.hotelsaas.adapter.DeviceCategoryListRightAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
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
 * {@link Bundle replaceInfo} include:replaceDeviceId、targetGatewayDeviceId、replaceDeviceNickname
 * @作者 吴友金
 */
public class DeviceAddCategoryActivity extends BaseMvpActivity<DeviceAddCategoryView, DeviceAddCategoryPresenter> implements DeviceAddCategoryView {
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
    private DeviceCategoryHandler deviceCategoryHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_add_category_list;
    }

    @Override
    protected void initView() {
        scopeId = getIntent().getLongExtra("scopeId", 0);
        deviceCategoryHandler = new DeviceCategoryHandler(this, scopeId);
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
                deviceCategoryHandler.handleAddJump(nodeBeans);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        deviceCategoryHandler.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.bt_refresh)
    void handleRefresh() {
        mContentView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mPresenter.loadCategory();
    }

    private void handleShouldExit() {
        if (getIntent().hasExtra("addForWait")) {
            finish();
        } else if (getIntent().hasExtra("replaceInfo")) {
            finish();
        }
    }
}