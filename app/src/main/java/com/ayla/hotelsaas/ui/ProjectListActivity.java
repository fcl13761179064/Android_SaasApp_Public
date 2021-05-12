package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.aliyun.iot.aep.sdk.IoTSmart;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.ProjectListTabAdapter;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.mvp.present.ProjectListPresenter;
import com.ayla.hotelsaas.mvp.view.ProjectListView;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.widget.Programe_change_AppBar;
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
        ImageView imageView = appBar.findViewById(R.id.iv_left);
        VersionUpgradeBean versionUpgradeInfo = Constance.getVersionUpgradeInfo();
        if (versionUpgradeInfo != null) {
            imageView.setImageResource(R.drawable.person_center_tip);
        } else {
            imageView.setImageResource(R.drawable.person_center);
        }
        String title_type = SharePreferenceUtils.getString(this, Constance.SP_SAAS, "1");
        if (Constance.isNetworkDebug()) {//这个判断是dev，qa环境
            if ("1".equalsIgnoreCase(title_type)) {
                appBar.setCenterText("智慧酒店");
                IoTSmart.setAuthCode("dev_saas");
            } else {
                appBar.setCenterText("地产行业");
                IoTSmart.setAuthCode("dev_miya");
            }
            IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
        } else {//这个是prod环境
            if ("1".equalsIgnoreCase(title_type)) {
                appBar.setCenterText("智慧酒店");
                IoTSmart.setAuthCode("pord_saas");
            } else {
                appBar.setCenterText("地产行业");
                IoTSmart.setAuthCode("china_production");
            }
            IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
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
                return new ProjectListFragment(appBar);
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
