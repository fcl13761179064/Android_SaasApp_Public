package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.ayla.hotelsaas.popmenu.PopMenu;
import com.ayla.hotelsaas.popmenu.UserMenu;
import com.ayla.hotelsaas.widget.Programe_change_AppBar;
import com.blankj.utilcode.util.ToastUtils;

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
public class ProjectListActivity extends BaseMvpActivity<ProjectListView, ProjectListPresenter> implements ProjectListView {
    private final int REQUEST_CODE_CREATE_PROJECT = 0x10;

    @BindView(R.id.appBar)
    Programe_change_AppBar appBar;

    @Nullable
    @BindView(R.id.magic_inditator)
    MagicIndicator magic_inditator;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private List<String> roomBeans;
    private FragmentStatePagerAdapter mAdapter;
    private WorkOrderBean data;
    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;
    private UserMenu mMenu;

    @Override
    protected ProjectListPresenter initPresenter() {
        return new ProjectListPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("upgrade")) {
            VersionUpgradeBean versionUpgradeBean = (VersionUpgradeBean)getIntent().getSerializableExtra("upgrade");
            Constance.saveVersionUpgradeInfo(versionUpgradeBean);
        }
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
       LinearLayout tv_title_change = appBar.getTitleLayoutView();
        tv_title_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMenu(v);
            }
        });
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


    private void initMenu(View view) {
        mMenu = new UserMenu(this);
        mMenu.addItem("智慧酒店", USER_SEARCH);
        mMenu.addItem("地产行业", USER_ADD);
        mMenu.setOnItemSelectedListener(new PopMenu.OnItemSelectedListener() {
            @Override
            public void selected(View view, PopMenu.Item item, int position) {
                switch (item.id) {
                    case USER_SEARCH:

                        ToastUtils.showShort("rwerqwerewq");
                        break;
                    case USER_ADD:

                        ToastUtils.showShort("rwerqwerew呃呃我惹我去q");

                        break;
                }

            }
        });
        mMenu.showAsDropDown(view);

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
