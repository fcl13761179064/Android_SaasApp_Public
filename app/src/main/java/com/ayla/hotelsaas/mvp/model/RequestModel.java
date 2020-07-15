package com.ayla.hotelsaas.mvp.model;


import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitDebugHelper;
import com.ayla.hotelsaas.data.net.RetrofitHelper;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述 网络请求Model
 * @作者 fanchunlei
 * @时间 2020/6/3
 */
public class RequestModel {

    public static final String APP_key = "vrmandroid";
    public static final String APP_SECRET = "92bAH6hNF4Q9RHymVGqYCdn58Zr3FPTU";
    //登录参数
    private static final String LOGIN_METHOD = "com.gewara.gptbs.vrm.vrmLogin";

    private volatile static RequestModel instance = null;

    private RequestModel() {
    }

    /*  1.存在共享数据
      2.多线程共同操作共享数据。关键字synchronized可以保证在同一时刻，
      只有一个线程可以执行某个方法或某个代码块，同时synchronized可以保证一个线程的变化可见（可见性），即可以代替volatile。*/
    public static RequestModel getInstance() {
        if (instance == null) {
            synchronized (RequestModel.class) {
                if (instance == null) {
                    instance = new RequestModel();
                }
            }
        }
        return instance;
    }

    private ApiService getApiService() {
        if (Constance.isNetworkDebug) {
            return RetrofitDebugHelper.getInstance().getApiService();
        }
        return RetrofitHelper.getInstance().getApiService();
    }

    public Observable<User> login(String account, String password) {
        return getApiService().login(account, password)
                .map(new Function<BaseResult<User>, User>() {
                    @Override
                    public User apply(BaseResult<User> baseResult) throws Exception {
                        return baseResult.data;
                    }
                })
                .onErrorReturn(new Function<Throwable, User>() {
                    @Override
                    public User apply(Throwable throwable) throws Exception {
                        User user = new User();
                        user.setGroupName("1");
                        user.setToken("1");
                        user.setUserId("1");
                        user.setUserName("1");
                        return user;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
