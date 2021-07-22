package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.AutoRunRuleEngineAdapter;
import com.ayla.hotelsaas.adapter.HelpCenterDetailAdapter;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.widget.AppBar;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import wendu.dsbridge.DWebView;

import static com.ayla.hotelsaas.application.MyApplication.getContext;

/**
 * H5页面
 * pageTitle 标题
 * url 页面地址
 */
public class HelpCenterDetailActivity extends BasicActivity {

    @BindView(R.id.ry_recycleview)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty_layout)
    View emptyView;
    @BindView(R.id.appBar)
    AppBar mAppBar;
    private HelpCenterDetailAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_help_center_two;
    }

    @Override
    protected View getLayoutView() {
        return null;
    }

    @Override
    protected void initView() {
        mAppBar.setCenterText(getIntent().getStringExtra("pageTitle"));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });

        ArrayList<String> strings = new ArrayList<>();
        strings.add("在清辉上怎么管理施工单？");
        strings.add("施工app在哪里下载登陆？");
        strings.add("怎么选择施工单？");
        strings.add("怎么添加设备？");
        strings.add("设备信息怎么编辑？");
        strings.add("一键联动怎么创建？");
        strings.add("双控联动怎么创建？");
        strings.add("怎么编辑联动？");
        strings.add("展厅/展箱施工单怎么创建？");
        mAdapter = new HelpCenterDetailAdapter(strings);
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_scene_page);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
    }

}


