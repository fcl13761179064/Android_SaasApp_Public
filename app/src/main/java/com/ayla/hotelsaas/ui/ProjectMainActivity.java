package com.ayla.hotelsaas.ui;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.ConstructionBillListBean;
import com.ayla.hotelsaas.widget.AppBar;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;

/**
 * 酒店页面
 * 进入时带入参数
 * bean： {@link ConstructionBillListBean.ResultListBean}
 */
public class ProjectMainActivity extends BaseMvpActivity {
    @BindView(R.id.tl_tabs)
    TabLayout mTabLayout;
    @BindView(R.id.vp_content)
    ViewPager mViewPager;
    @BindView(R.id.appBar)
    AppBar mAppBar;

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_project_main;
    }

    @Override
    protected void initView() {
        ConstructionBillListBean.ResultListBean bean = (ConstructionBillListBean.ResultListBean) getIntent().getSerializableExtra("bean");

        mAppBar.setCenterText(bean.getTitle());

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return ProjectRoomsFragment.newInstance(bean);
                }
                if (position == 1) {
                    return ProjectDetailFragment.newInstance(bean);
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if(position == 0){
                    return "智能设备";
                }
                if(position == 1){
                    return "项目信息";
                }
                return super.getPageTitle(position);
            }
        });

        mTabLayout.setupWithViewPager(mViewPager, true);
        mTabLayout.setTabTextColors(Color.parseColor("#333333"), ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
    }

    @Override
    protected void initListener() {

    }
}
