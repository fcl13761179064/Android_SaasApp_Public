package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListTabAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 我的项目页面
 */
public class ProjectListActivity extends BaseMvpActivity<ProjectListView, ProjectListPresenter> implements ProjectListView {


    @Nullable
    @BindView(R.id.magic_inditator)
    MagicIndicator magic_inditator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.change_left_ll)
    LinearLayout change_left_ll;
    @BindView(R.id.change_iv_left)
    ImageView change_iv_left;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.change_center_title)
    LinearLayout change_center_title;

    private List<String> roomBeans;
    private FragmentStatePagerAdapter mAdapter;
    private WorkOrderBean data;

    @Override
    protected ProjectListPresenter initPresenter() {
        return new ProjectListPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("upgrade")) {
            VersionUpgradeBean versionUpgradeBean = (VersionUpgradeBean) getIntent().getSerializableExtra("upgrade");
            Constance.saveVersionUpgradeInfo(versionUpgradeBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (change_iv_left != null) {
            VersionUpgradeBean versionUpgradeInfo = Constance.getVersionUpgradeInfo();
            if (versionUpgradeInfo != null) {
                change_iv_left.setImageResource(R.drawable.person_center_tip);
            } else {
                change_iv_left.setImageResource(R.drawable.person_center);
            }
            Log.d(TAG, "onResume: netDebug:" + Constance.isNetworkDebug());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_project_list;
    }

    @Override
    protected void initView() {
        roomBeans = new ArrayList<>();
        for (int x = 0; x < 1; x++) {
            roomBeans.add("施工中");
            roomBeans.add("历史项目");
        }
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        ProjectListTabAdapter adapter = new ProjectListTabAdapter(roomBeans, viewPager, magic_inditator);
        commonNavigator.setAdapter(adapter);
        magic_inditator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magic_inditator, viewPager);
        viewPager.setCurrentItem(0, false);
        String title_type = SharePreferenceUtils.getString(this, Constance.SP_SAAS, "1");
        if ("1".equalsIgnoreCase(title_type)) {
            tv_title.setText("智慧酒店");
        } else {
            tv_title.setText("地产行业");
        }
    }


    @Override
    protected void initListener() {
        mAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return new ProjectListFragment();
                } else {
                    return new HistroyProjectListFragment();
                }
            }

            @Override
            public int getCount() {
                return roomBeans.size() == 0 ? 0 : roomBeans.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return roomBeans.get(position);
            }
        };
        viewPager.setAdapter(mAdapter);

        change_left_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectListActivity.this, PersonCenterActivity.class));
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void showData(WorkOrderBean data) {
        this.data = data;
    }

    @Override
    public void onRequestFailed(Throwable throwable) {

    }

}
