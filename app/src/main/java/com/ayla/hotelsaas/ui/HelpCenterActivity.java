package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.HelpCenterAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.chad.library.adapter.base.BaseQuickAdapter;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * 樊春雷
 * 帮助中心页面
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
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, 3, R.color.all_bg_color));
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
                Intent intent = new Intent(HelpCenterActivity.this, H5BaseActivity.class);
                int current_positon= position + 1;
                intent.putExtra("url", Constance.getAssistantBaseUrl() + "/constructionPage#/details/"+current_positon);
                startActivity(intent);
            }
        });
    }
}
