package com.ayla.hotelsaas.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.User;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import java.util.List;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;


/**
 * Created by fcl13761179064 on 2020/6/3.
 */
public class MyApplication extends Application {

    //用户登录成功数据
    private User userEntity;
    private List<DeviceListBean.DevicesBean> mDevicesBean;
    private static MyApplication mInstance = null;

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    public static Resources getResource() {
        return mInstance.getResources();
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.white, android.R.color.darker_gray);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initAutoSize();
    }

    public User getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(User userEntity) {
        if (null != userEntity) {
            Constance.UserIsLogin = true;
        } else {
            Constance.UserIsLogin = false;
        }
        this.userEntity = userEntity;
    }

    public List<DeviceListBean.DevicesBean> getDevicesBean() {
        return mDevicesBean;
    }

    public void setDevicesBean(List<DeviceListBean.DevicesBean> devicesBean) {
        mDevicesBean = devicesBean;
    }

    public static MyApplication getmInstance() {
        return mInstance;
    }

    public static void setmInstance(MyApplication mInstance) {
        MyApplication.mInstance = mInstance;
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance()
                .setBaseOnWidth(true)
                .getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
    }
}
