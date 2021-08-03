package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.AutoRunRuleEngineAdapter;
import com.ayla.hotelsaas.adapter.HelpCenterAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.fragment.RuleEngineFragment;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.present.AutoRunFragmentPresenter;
import com.ayla.hotelsaas.mvp.view.AutoRunView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 自动化联动页面
 */
public class HelpCenterActivity extends BaseMvpActivity {

    @BindView(R.id.device_recyclerview)
    RecyclerView mRecyclerView;
    HelpCenterAdapter mAdapter;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_helpcenter_list_show;
    }

    @Override
    protected void initView() {
        List<String> list = new ArrayList<>();
        list.add("在清晖上怎么管理施工单？");
        list.add("施工 APP 在哪里下载登录？？");
        list.add("怎么选择施工单？");
        list.add("怎么添加设备？");
        list.add("设备信息怎么编辑？");
        list.add("一键联动怎么创建？");
        list.add("双控联动怎么创建？");
        list.add("怎么编辑联动？？");
        list.add("展厅/展箱施工单怎么创建？");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new HelpCenterAdapter();
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_scene_page);
        mAdapter.addData(list);
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
