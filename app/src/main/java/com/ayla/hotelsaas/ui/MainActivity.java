package com.ayla.hotelsaas.ui;


import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.DeviceListAdapter;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.fragment.DeviceListFragment;
import com.ayla.hotelsaas.widget.AppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;

/**
 * @描述 首页
 * @作者 fanchunlei
 * @时间 2020/7/20
 */
public class MainActivity extends BasicActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.float_btn)
    FloatingActionButton float_btn;
    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;
    private DeviceListAdapter mAdapter;
    private RoomOrderBean.ResultListBean mRoom_order;
    private WorkOrderBean.ResultListBean mWork_order;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void refreshUI() {
       /* mRoom_order = (RoomOrderBean.DataBean.ResultListBean) getIntent().getSerializableExtra("roomData");
        mWork_order = (WorkOrderBean.DataBean.ResultListBean) getIntent().getSerializableExtra("workOrderdata");
        appBar.setCenterText(mWork_order.getTitle());*/
        super.refreshUI();
    }


    @Override
    protected void initView() {
        //测试下gitlab—ci

    }

    @Override
    protected void initListener() {
        mNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ((ViewPager) findViewById(R.id.content)).setCurrentItem(TabFragment.from(item.getItemId()).ordinal());
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TabFragment.onDestroy();
    }

    private enum TabFragment {
        practice(R.id.navigation_practice, DeviceListShowActivity.class),
        styles(R.id.navigation_style, DeviceListShowActivity.class),
        using(R.id.navigation_example, DeviceListShowActivity.class);

        private Activity fragment;
        private final int menuId;
        private final Class<? extends Activity> clazz;

        TabFragment(@IdRes int menuId, Class<? extends Activity> clazz) {
            this.menuId = menuId;
            this.clazz = clazz;
        }

        @NonNull
        public Activity fragment() {
            if (fragment == null) {
                try {
                    fragment = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    fragment = new Activity();
                }
            }
            return fragment;
        }

        public static TabFragment from(int itemId) {
            for (TabFragment fragment : values()) {
                if (fragment.menuId == itemId) {
                    return fragment;
                }
            }
            return styles;
        }

        public static void onDestroy() {
            for (TabFragment fragment : values()) {
                fragment.fragment = null;
            }
        }
    }
}
