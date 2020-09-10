package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

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
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 分配房间页面
 * 进入时带入参数
 * 1.{@link String names} 层级的名称
 * 1.{@link String hotelId}
 * 1.{@link String hotelName}
 * 1.{@link Boolean isRoom} 标记上层是否为房间
 */
public class DistributionActivity extends BaseMvpActivity<DistributionView, DistributionPresenter> implements DistributionView {
    private final int REQUEST_CODE_HOTEL_SELECT = 0x10;
    @BindView(R.id.rv_rooms)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_stack)
    TextView mParentsNameTextView;
    @BindView(R.id.tv_hotel_name)
    TextView tv_hotel_name;
    @BindView(R.id.tv_type)
    TextView tv_type;

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
        boolean isRoom = getIntent().getBooleanExtra("isRoom", false);
        tv_type.setText(String.format("分配房间选择（%s）", isRoom ? "单选" : "多选"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionRoomAdapter(null);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CheckableSupport<RoomManageBean.RecordsBean> item = mAdapter.getItem(position);
                item.setChecked(!item.isChecked());
                mAdapter.notifyItemChanged(position);
                if (isRoom) {//如果上层是房间，就单选
                    for (int i = 0; i < mAdapter.getData().size(); i++) {
                        if (position != i) {
                            if (mAdapter.getItem(i).isChecked()) {
                                mAdapter.getItem(i).setChecked(false);
                                mAdapter.notifyItemChanged(i);
                            }
                        }
                    }
                }
            }
        });
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        String names = getIntent().getStringExtra("names");
        if (TextUtils.isEmpty(names)) {
            mParentsNameTextView.setVisibility(View.GONE);
        } else {
            mParentsNameTextView.setText(names);
        }
        tv_hotel_name.setText(getIntent().getStringExtra("hotelName"));
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.loadSelfRooms();
    }

    @Override
    public void hotelLoadSuccess(List<RoomManageBean.RecordsBean> rooms) {
        List<CheckableSupport<RoomManageBean.RecordsBean>> data = new ArrayList<>();
        for (RoomManageBean.RecordsBean room : rooms) {
            data.add(new CheckableSupport<>(room));
        }
        mAdapter.setNewData(data);
    }

    @OnClick(R.id.ll_repeat_data)
    void jumpHotelSelect() {
    }
}
