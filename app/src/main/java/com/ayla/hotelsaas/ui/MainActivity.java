package com.ayla.hotelsaas.ui;

import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BasicActivity;
import com.ayla.hotelsaas.base.BasicFragment;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.fragment.DeviceListFragment;
import com.ayla.hotelsaas.fragment.SceneLikeageFragment;
import com.ayla.hotelsaas.fragment.TestFragment;
import com.ayla.hotelsaas.utils.AppManager;
import com.ayla.hotelsaas.widget.AppBar;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @描述 首页
 * @作者 fanchunlei
 * @时间 2020/7/20
 */
public class MainActivity extends BasicActivity implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rg_main_fragment)
    RadioGroup rgIndicators;
    @BindView(R.id.fl_container)
    FrameLayout fl_container;
    @BindView(R.id.rb_main_fragment_device)
    RadioButton main_device;
    @BindView(R.id.rb_main_fragment_linkage)
    RadioButton main_likeage;
    @BindView(R.id.rb_main_fragment_test)
    RadioButton main_test;

    private RoomOrderBean.ResultListBean mRoom_order;
    private WorkOrderBean.ResultListBean mWork_order;
    private List<Fragment> mFragments;
    private BasicFragment currentFragment;
    public final static int GO_HOME_TYPE = 0;
    public final static int GO_THREE_TYPE = 2;
    public final static int GO_SECOND_TYPE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void refreshUI() {
        mRoom_order = (RoomOrderBean.ResultListBean) getIntent().getSerializableExtra("roomData");
        mWork_order = (WorkOrderBean.ResultListBean) getIntent().getSerializableExtra("workOrderdata");
        appBar.setCenterText(mRoom_order.getRoomName());
        super.refreshUI();
    }


    @Override
    protected void initView() {
        mFragments = new ArrayList<>();
        mFragments.add(new DeviceListFragment(mRoom_order));
        mFragments.add(new SceneLikeageFragment(mRoom_order));
        mFragments.add(new TestFragment());

    }

    @Override
    protected void initListener() {
        rgIndicators.check(R.id.rb_main_fragment_device);
        rgIndicators.setOnCheckedChangeListener(this);
        //默认选择加载首页
        changeFragment(GO_HOME_TYPE);

        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(R.drawable.bar_bottom_device);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_news.setBounds(0, 0, 40, 40);
        //设置图片在文字的哪个方向
        main_device.setCompoundDrawables(null, drawable_news, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_live = getResources().getDrawable(R.drawable.bar_bottom_linkage);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_live.setBounds(0, 0, 40, 40);
        //设置图片在文字的哪个方向
        main_likeage.setCompoundDrawables(null, drawable_live, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_tuijian = getResources().getDrawable(R.drawable.bar_bottom_test);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_tuijian.setBounds(0, 0, 40, 40);
        //设置图片在文字的哪个方向
        main_test.setCompoundDrawables(null, drawable_tuijian, null, null);

    }


    private void changeFragment(int type) {

        //做fragment的切换
        try {
            switch (type) {
                case GO_HOME_TYPE: {
                    changeState(main_device);
                    showBaseFragment("main", type);
                    break;
                }
                case GO_SECOND_TYPE: {
                    changeState(main_likeage);
                    showBaseFragment("linkage", type);
                    break;
                }
                case GO_THREE_TYPE: {
                    changeState(main_test);
                    showBaseFragment("test", type);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeState(TextView textView) {
        main_device.setSelected(false);
        main_likeage.setSelected(false);
        main_test.setSelected(false);
        textView.setSelected(true);
    }

    private void showBaseFragment(String tag, int type) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        currentFragment = (BasicFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            currentFragment = createBaseFragment(type);
            ft.add(R.id.fl_container, currentFragment, tag).addToBackStack(null);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

    private BasicFragment createBaseFragment(int type) {
        switch (type) {
            case GO_HOME_TYPE: {

                return new DeviceListFragment(mRoom_order);

            }
            case GO_SECOND_TYPE: {

                return new SceneLikeageFragment(mRoom_order);
            }
            case GO_THREE_TYPE: {

                return new TestFragment();
            }
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_main_fragment_device: {
                appBar.setCenterText(mRoom_order.getRoomName());
                changeFragment(GO_HOME_TYPE);
                break;
            }
            case R.id.rb_main_fragment_linkage: {
                appBar.setCenterText(mRoom_order.getRoomName());
                changeFragment(GO_SECOND_TYPE);
                break;
            }
            case R.id.rb_main_fragment_test: {
                appBar.setCenterText("测试");
                changeFragment(GO_THREE_TYPE);
                break;
            }
        }
    }

}
