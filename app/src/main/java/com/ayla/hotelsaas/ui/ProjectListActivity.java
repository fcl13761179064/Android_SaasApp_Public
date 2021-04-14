package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListTabAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.fragment.DeviceListFragmentNew;
import com.ayla.hotelsaas.widget.AppBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的项目页面
 */
public class ProjectListActivity extends BaseMvpActivity {
    private final int REQUEST_CODE_CREATE_PROJECT = 0x10;

    @BindView(R.id.appBar)
    AppBar appBar;

    @Nullable
    @BindView(R.id.magic_inditator)
    MagicIndicator magic_inditator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<String> roomBeans;
    private FragmentStatePagerAdapter mAdapter;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageView imageView = appBar.findViewById(R.id.iv_left);
        VersionUpgradeBean versionUpgradeInfo = Constance.getVersionUpgradeInfo();
        if (versionUpgradeInfo != null) {
            imageView.setImageResource(R.drawable.person_center_tip);
        } else {
            imageView.setImageResource(R.drawable.person_center);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_project_list;
    }

    @Override
    protected void initView() {

        roomBeans = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            if (x == 0) {
                roomBeans.add("全部");
            } else if (x == 1) {
                roomBeans.add("施工中");
            } else {
                roomBeans.add("完成");
            }
        }
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(false);
        ProjectListTabAdapter adapter = new ProjectListTabAdapter(roomBeans, viewPager, magic_inditator);
        commonNavigator.setAdapter(adapter);
        magic_inditator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magic_inditator, viewPager);
        viewPager.setCurrentItem(0, false);
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
            public ProjectListFragment getItem(int position) {
                return new ProjectListFragment("0");
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
    }


    protected void appBarLeftIvClicked() {
        startActivity(new Intent(this, PersonCenterActivity.class));
    }

    @OnClick(R.id.bt_add)
    void handleAdd() {
        startActivityForResult(new Intent(this, CreateProjectActivity.class), REQUEST_CODE_CREATE_PROJECT);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
