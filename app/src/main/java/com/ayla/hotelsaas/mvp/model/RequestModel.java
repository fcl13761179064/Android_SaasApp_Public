package com.ayla.hotelsaas.mvp.model;


import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitDebugHelper;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

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
        return getApiService().login(account, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取工作订单的条数
     *
     * @param pageNum 页码 从1开始
     * @param maxNum  每页加载量
     * @return
     */
    public Observable<BaseResult<List<WorkOrderBean>>> getWorkOrderList(String type, int pageNum, String maxNum) {
        Map<String, String> map = new HashMap<>(8);
        //不同的
        //待办事项获取数量
         final String TODO_ITEM_COUNT = "com.gewara.gptbs.vrm.pendingCount";
        map.put("method", TODO_ITEM_COUNT);
        if (!TextUtils.isEmpty(type)) {
            map.put("type", type);
        }
        return getApiService().getWorkOrders()
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<BaseResult<List<DeviceCategoryBean>>> getDeviceCategory() {
//        List<DeviceCategoryBean> result = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            DeviceCategoryBean deviceCategoryBean = new DeviceCategoryBean();
//            result.add(deviceCategoryBean);
//            deviceCategoryBean.name = "一级列表" + i;
//            deviceCategoryBean.subBeans = new ArrayList<>();
//            for (int j = 0; j < 20; j++) {
//                DeviceCategoryBean.SubBean subBean = new DeviceCategoryBean.SubBean();
//                subBean.name = "二级列表" + i + "_" + j;
//                subBean.mode = 1;
//                deviceCategoryBean.subBeans.add(subBean);
//            }
//        }
//        return Observable.just(result);
        return getApiService().fetchDeviceCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResult<Boolean>> bindDeviceWithDSN(String dsn, String cuid, String scopeId, String scopeType) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        body.addProperty("cuid", cuid);
        body.addProperty("scope_id", scopeId);
        body.addProperty("scope_type", scopeType);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().bindDeviceWithDSN(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResult<Boolean>> unbindDeviceWithDSN(String dsn, String scopeId, String scopeType) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        body.addProperty("scope_id", scopeId);
        body.addProperty("scope_type", scopeType);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().unbindDeviceWithDSN(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
