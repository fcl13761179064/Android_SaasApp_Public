package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DistributionHotelAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.mvp.present.DistributionHotelSelectPresenter;
import com.ayla.hotelsaas.mvp.view.DistributionHotelSelectView;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import butterknife.BindView;

/**
 * 分配房间,酒店选择页面
 */
public class DistributionHotelSelectActivity extends BaseMvpActivity<DistributionHotelSelectView, DistributionHotelSelectPresenter> implements DistributionHotelSelectView {
    private final int REQUEST_CODE_SELECT_STRUCT = 0x10;
    @BindView(R.id.rv_rooms)
    RecyclerView mRecyclerView;

    private DistributionHotelAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_distribution_hotel_select;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionHotelAdapter(null);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                HotelListBean.RecordsBean recordsBean = mAdapter.getItem(position);
                Intent intent = new Intent(DistributionHotelSelectActivity.this, DistributionStructSelectActivity.class);
                intent.putExtra("hotelId",recordsBean.getId());
                intent.putExtra("hotelName",recordsBean.getHotelName());
                startActivityForResult(intent, REQUEST_CODE_SELECT_STRUCT);
            }
        });
    }

    @Override
    protected DistributionHotelSelectPresenter initPresenter() {
        return new DistributionHotelSelectPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.fetchTransferHotelList();
    }

    @Override
    public void showData(List<HotelListBean.RecordsBean> records) {
        mAdapter.setNewData(records);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_STRUCT && resultCode == RESULT_OK){
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
