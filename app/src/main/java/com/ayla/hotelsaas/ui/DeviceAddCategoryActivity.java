package com.ayla.hotelsaas.ui;


import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceCategoryListLeftAdapter;
import com.ayla.hotelsaas.adapter.DeviceCategoryListRightAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.mvp.present.DeviceAddCategoryPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @描述 添加设备入口页面，展示产品分类二级列表
 * @作者 吴友金
 */
public class DeviceAddCategoryActivity extends BaseMvpActivity<DeviceAddCategoryView,DeviceAddCategoryPresenter> implements DeviceAddCategoryView {

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
        leftRecyclerView.setAdapter(mLeftAdapter);
        mLeftAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                adjustData(position);
            }
        });

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

    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void adjustData(int categoryIndex) {
        if (mLeftAdapter.getData().size() > 0) {
            mLeftAdapter.setSelectedPosition(categoryIndex);
            mRightAdapter.setNewData(mLeftAdapter.getData().get(categoryIndex).subBeans);
        }
    }
}
