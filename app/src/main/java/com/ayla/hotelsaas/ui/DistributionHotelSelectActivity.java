package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.DistributionHotelAdapter;
import com.ayla.hotelsaas.adapter.DistributionRoomAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.blankj.utilcode.util.SizeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 分配房间,酒店选择页面
 */
public class DistributionHotelSelectActivity extends BaseMvpActivity   {
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

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<CheckableSupport<RoomManageBean.RecordsBean>> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            RoomManageBean.RecordsBean recordsBean = new RoomManageBean.RecordsBean();
            recordsBean.setContentName("room" + i);
            CheckableSupport<RoomManageBean.RecordsBean> recordsBeanCheckableSupport = new CheckableSupport<>(recordsBean);
            recordsBeanCheckableSupport.setChecked(i == 5);
            data.add(recordsBeanCheckableSupport);
        }
        mAdapter.setNewData(data);
    }
}
