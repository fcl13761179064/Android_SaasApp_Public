package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.fragment.DeviceListFragment;
import com.ayla.hotelsaas.fragment.RuleEngineFragment;
import com.ayla.hotelsaas.fragment.TestFragment;
import com.ayla.hotelsaas.mvp.present.MainPresenter;
import com.ayla.hotelsaas.mvp.view.MainView;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @描述 首页
 * @作者 fanchunlei
 * @时间 2020/7/20
 * removeEnable ,标记是否支持删除
 */
public class MainActivity extends BaseMvpActivity<MainView, MainPresenter> implements RadioGroup.OnCheckedChangeListener, MainView {
    private static final int REQUEST_CODE_TO_MORE = 0x10;

    public static final int RESULT_CODE_REMOVED = 0X20;
    public static final int RESULT_CODE_RENAMED = 0X21;

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

    private long mRoom_ID;
    private String mRoom_name;
    private List<Fragment> mFragments;
    private BaseMvpFragment currentFragment;
    public final static int GO_HOME_TYPE = 0;
    public final static int GO_THREE_TYPE = 2;
    public final static int GO_SECOND_TYPE = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mRoom_ID = getIntent().getLongExtra("roomId", 0);
        mRoom_name = getIntent().getStringExtra("roomName");
        appBar.setCenterText(mRoom_name);
        appBar.setRightText("更多");

        mFragments = new ArrayList<>();
        mFragments.add(new DeviceListFragment(mRoom_ID));
        mFragments.add(new RuleEngineFragment(mRoom_ID));
        mFragments.add(new TestFragment());
    }

    @Override
    protected void appBarRightTvClicked() {
        super.appBarRightTvClicked();
        Intent intent = new Intent(MainActivity.this, RoomMoreActivity.class);
        intent.putExtras(getIntent());
        startActivityForResult(intent, REQUEST_CODE_TO_MORE);
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
        drawable_news.setBounds(0, 0, 48, 48);
        //设置图片在文字的哪个方向
        main_device.setCompoundDrawables(null, drawable_news, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_live = getResources().getDrawable(R.drawable.bar_bottom_linkage);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_live.setBounds(0, 0, 48, 48);
        //设置图片在文字的哪个方向
        main_likeage.setCompoundDrawables(null, drawable_live, null, null);

        //定义底部标签图片大小和位置
        Drawable drawable_tuijian = getResources().getDrawable(R.drawable.bar_bottom_test);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_tuijian.setBounds(0, 0, 48, 48);
        //设置图片在文字的哪个方向
        main_test.setCompoundDrawables(null, drawable_tuijian, null, null);
        appBar.setRightText("更多");
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
        currentFragment = (BaseMvpFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            currentFragment = createBaseFragment(type);
            ft.add(R.id.fl_container, currentFragment, tag);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();
    }

    private BaseMvpFragment createBaseFragment(int type) {
        switch (type) {
            case GO_HOME_TYPE: {

                return new DeviceListFragment(mRoom_ID);

            }
            case GO_SECOND_TYPE: {
                return new RuleEngineFragment(mRoom_ID);
            }
            case GO_THREE_TYPE: {

                return new TestFragment();
            }
        }
        return null;
    }


    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
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
                appBar.setCenterText(mRoom_name);
                changeFragment(GO_HOME_TYPE);
                break;
            }
            case R.id.rb_main_fragment_linkage: {
                appBar.setCenterText(mRoom_name);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TO_MORE) {
            if (resultCode == RoomMoreActivity.RESULT_CODE_RENAMED) {
                mRoom_name = data.getStringExtra("newName");
                appBar.setCenterText(mRoom_name);
                setResult(RESULT_CODE_RENAMED, new Intent().putExtra("roomId", mRoom_ID).putExtra("roomName", mRoom_name));
            }
            if (resultCode == RoomMoreActivity.RESULT_CODE_REMOVED) {
                setResult(RESULT_CODE_REMOVED, new Intent().putExtra("roomId", mRoom_ID));
                finish();
            }
        }
    }
}
