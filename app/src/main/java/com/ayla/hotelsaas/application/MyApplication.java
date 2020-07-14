package com.ayla.hotelsaas.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.ayla.hotelsaas.bean.User;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;


/**
 * Created by fcl13761179064 on 2020/6/3.
 */
public class MyApplication extends Application {

    //用户登录成功数据
    private User userEntity;
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

    private void initAutoSize() {
        AutoSizeConfig.getInstance()
                .setBaseOnWidth(true)
                .getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
    }
}
