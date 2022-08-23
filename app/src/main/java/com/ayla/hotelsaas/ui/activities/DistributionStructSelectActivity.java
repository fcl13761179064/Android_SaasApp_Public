package com.ayla.hotelsaas.ui.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DistributionStructAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.mvp.present.DistributionStructSelectPresenter;
import com.ayla.hotelsaas.mvp.view.DistributionStructSelectView;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;

/**
 * 分配房间,楼层选择页面
 * 进入时传入参数
 * 1.{@link String hotelId}
 * 1.{@link String hotelName}
 */
public class DistributionStructSelectActivity extends BaseMvpActivity<DistributionStructSelectView, DistributionStructSelectPresenter> implements DistributionStructSelectView {
    private final int REQUEST_CODE_DO_TRANSFER = 0x10;
    @BindView(R.id.rv_rooms)
    RecyclerView mRecyclerView;

    private DistributionStructAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_distribution_struct_select;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DistributionStructAdapter(null);
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

    private Stack<List<TreeListBean>> stackMap = new Stack<>();//维护调
    private Stack<TreeListBean> historyMap = new Stack<>();

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TreeListBean treeListBean = mAdapter.getItem(position);
                historyMap.push(treeListBean);
                List<TreeListBean> children = treeListBean.getChildren();
                if (children != null && !children.isEmpty()) {
                    stackMap.push(mAdapter.getData());
                    mAdapter.setNewData(children);
                } else {//下面没有节点了
                    Intent intent = new Intent(DistributionStructSelectActivity.this, DistributionActivity.class);
                    intent.putExtra("names", getHistoryRote());
                    intent.putExtra("targetId", treeListBean.getId());
                    intent.putExtras(getIntent());
                    startActivityForResult(intent, REQUEST_CODE_DO_TRANSFER);
                    historyMap.pop();
                }
            }
        });
    }

    @Override
    protected DistributionStructSelectPresenter initPresenter() {
        return new DistributionStructSelectPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String hotelId = getIntent().getStringExtra("hotelId");
        mPresenter.fetchTransferTreeList(hotelId);
    }


    @Override
    public void showData(List<TreeListBean> treeListBeans) {
        if (treeListBeans != null && !treeListBeans.isEmpty()) {
            mAdapter.setNewData(treeListBeans);
        } else {
            jumpEmpty();
        }
    }

    @Override
    public void onBackPressed() {
        if (!historyMap.empty()) {
            historyMap.pop();
        }
        if (!stackMap.empty()) {
            List<TreeListBean> pop = stackMap.pop();
            mAdapter.setNewData(pop);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DO_TRANSFER && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     *
     */
    private void jumpEmpty() {
        mAdapter.setNewData(null);
        mAdapter.setEmptyView(R.layout.empty_distruction_struct_select);
        mAdapter.getEmptyView().findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DistributionStructSelectActivity.this, DistributionActivity.class);
                intent.putExtras(getIntent());
                intent.putExtra("names", getHistoryRote());
                startActivityForResult(intent, REQUEST_CODE_DO_TRANSFER);
            }
        });
    }

    private String getHistoryRote() {
        StringBuilder sb = new StringBuilder();//层级名称
        ArrayList<TreeListBean> treeListBeans = new ArrayList<>(historyMap);
        Iterator<TreeListBean> iterator = treeListBeans.iterator();
        while (iterator.hasNext()) {
            TreeListBean next = iterator.next();
            sb.append(next.getContentName());
            if (iterator.hasNext()) {
                sb.append("/");
            }
        }
        return sb.toString();
    }
}
