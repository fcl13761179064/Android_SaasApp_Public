package com.ayla.hotelsaas.ui;

import android.content.Intent;
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
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
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
 * 1.{@link String targetId} 目标资源的id，如果没传，说明是对酒店进行房间分配。
 * <p>
 * 返回参数
 * 1.{@link String[] rooms} 被分配的房间id集合
 */
public class DistributionActivity extends BaseMvpActivity<DistributionView, DistributionPresenter> implements DistributionView {
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
        String targetId = getIntent().getStringExtra("targetId");
        boolean isMultSelect = TextUtils.isEmpty(targetId);
        tv_type.setText(String.format("分配房间选择（%s）", isMultSelect ? "多选" : "单选"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionRoomAdapter(null);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CheckableSupport<RoomManageBean.RecordsBean> item = mAdapter.getItem(position);
                item.setChecked(!item.isChecked());
                mAdapter.notifyItemChanged(position);
                if (!isMultSelect) {
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

    @Override
    public void doSuccess(String[] rooms) {
        CustomToast.makeText(this, "操作成功", R.drawable.ic_success);
        setResult(RESULT_OK, new Intent().putExtra("rooms", rooms));
        finish();
    }

    @Override
    public void doFailed() {
        CustomToast.makeText(this, "操作失败", R.drawable.ic_toast_warming);
    }

    @OnClick(R.id.ll_repeat_data)
    void jumpHotelSelect() {
        Intent intent = new Intent(this, DistributionHotelSelectActivity.class);
        startActivity(intent);
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        String hotelId = getIntent().getStringExtra("hotelId");
        String targetId = getIntent().getStringExtra("targetId");
        String hotelName = getIntent().getStringExtra("hotelName");
        List<String> sourceId = new ArrayList<>();
        for (CheckableSupport<RoomManageBean.RecordsBean> datum : mAdapter.getData()) {
            if (datum.isChecked()) {
                sourceId.add(String.valueOf(datum.getData().getId()));
            }
        }
        if (sourceId.isEmpty()) {
            CustomToast.makeText(this, "至少选择一个分配房间", R.drawable.ic_toast_warming);
            return;
        }
        CustomAlarmDialog
                .newInstance(new CustomAlarmDialog.Callback() {
                    @Override
                    public void onDone(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                        if (!TextUtils.isEmpty(targetId)) {//房间->结构
                            mPresenter.transferToRoom(hotelId, sourceId.get(0), targetId);
                        } else {//房间->酒店
                            mPresenter.transferToHotel(hotelId, sourceId.toArray(new String[]{}));
                        }
                    }

                    @Override
                    public void onCancel(CustomAlarmDialog dialog) {
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .setTitle("确认是否分配")
                .setContent(String.format("您是否确认将所选房间分配到“%s”？", hotelName))
                .show(getSupportFragmentManager(), "dialog");

    }
}
