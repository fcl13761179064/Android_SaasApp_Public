package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.adapter.DistributionRoomAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.mvp.present.DistributionPresenter;
import com.ayla.hotelsaas.mvp.view.DistributionView;
import com.blankj.utilcode.util.SizeUtils;
import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 分配房间页面
 */
public class DistributionActivity extends BaseMvpActivity<DistributionView, DistributionPresenter> implements DistributionView {
    private final int REQUEST_CODE_HOTEL_SELECT = 0x10;
    @BindView(R.id.rv_rooms)
    RecyclerView mRecyclerView;

    private DistributionRoomAdapter mAdapter;

    @Override
    protected DistributionPresenter initPresenter() {
        return new DistributionPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_distribution;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionRoomAdapter(null);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadHotels();
    }

    @Override
    public void hotelLoadSuccess(List<CheckableSupport<RoomManageBean.RecordsBean>> hotels) {
        mAdapter.setNewData(hotels);
    }

    @OnClick(R.id.ll_repeat_data)
    void jumpHotelSelect() {
        Intent intent = new Intent(this, DistributionHotelSelectActivity.class);
        startActivityForResult(intent, REQUEST_CODE_HOTEL_SELECT);
    }
}
