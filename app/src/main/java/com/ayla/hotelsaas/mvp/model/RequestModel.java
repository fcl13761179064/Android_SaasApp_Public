package com.ayla.hotelsaas.mvp.model;


import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitDebugHelper;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
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


    public Observable<BaseResult<User>> login(String account, String password) {
        Map<String, String> map = new HashMap<>(8);
        //不同的
        map.put("method", LOGIN_METHOD);
        map.put("username", account);
        map.put("pwd", password);
        return getApiService().BaseRequest(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, BaseResult<User>>() {
                    @Override
                    public BaseResult<User> apply(String s) throws Exception {
                        return new Gson().fromJson(s, new TypeToken<BaseResult<User>>() {
                        }.getType());
                    }
                });

    }

    /**
     * 获取待办事项列表
     *
     * @param pageNum 页码 从1开始
     * @param maxNum  每页加载量
     * @return
     */
    public Observable<BaseResult<ArrayList<WorkOrderBean>>> getWorkOrderList(String type, int pageNum, String maxNum) {
        Map<String, String> map = new HashMap<>(8);
        //不同的
        map.put("from", String.valueOf(pageNum));
        map.put("maxnum", maxNum);
        if (!TextUtils.isEmpty(type)) {
            map.put("type", type);
        }
        return getApiService().BaseRequest(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<String, BaseResult<ArrayList<WorkOrderBean>>>() {
                    @Override
                    public BaseResult<ArrayList<WorkOrderBean>> apply(@NonNull String s) throws Exception {
                        return new Gson().fromJson(s, new TypeToken<BaseResult<ArrayList<WorkOrderBean>>>() {
                        }.getType());
                    }
                });
    }

}
