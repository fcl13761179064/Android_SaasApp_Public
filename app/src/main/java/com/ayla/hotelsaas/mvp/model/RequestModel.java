package com.ayla.hotelsaas.mvp.model;


import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitDebugHelper;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
        JsonObject body = new JsonObject();
        body.addProperty("account", account);
        body.addProperty("password", password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().login(new_body)
                .delay(2, TimeUnit.SECONDS)
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
    public Observable<BaseResult<WorkOrderBean>> getWorkOrderList(int pageNum, String maxNum) {
        return getApiService().getWorkOrders(pageNum, maxNum)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }


    /**
     * 获取房间号的条数
     *
     * @param //页码    从1开始
     * @param //每页加载量
     * @return
     */

    public Observable<BaseResult<RoomOrderBean>> getRoomOrderList(String billId, int pageNum, String maxNum) {
        return getApiService().getRoomOrders(pageNum, maxNum, billId)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    /**
     * 获取设备列表
     *
     * @param //页码    从1开始
     * @param //每页加载量
     * @return
     */
    public Observable<BaseResult<DeviceListBean>> getDeviceList(String roomId, int pageNum, String maxNum) {
        JsonObject body = new JsonObject();
        body.addProperty("roomId", roomId);
        body.addProperty("pageNo", pageNum);
        body.addProperty("pageSize", maxNum);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().getDeviceList(body111)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<BaseResult<List<DeviceCategoryBean>>> getDeviceCategory() {
        return getApiService().fetchDeviceCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResult<Boolean>> bindDeviceWithDSN(String dsn, String scopeId) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        body.addProperty("scope_id", scopeId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().bindDeviceWithDSN(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResult<Boolean>> unbindDeviceWithDSN(String dsn, String scopeId) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        body.addProperty("scope_id", scopeId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().unbindDeviceWithDSN(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<BaseResult<Boolean>> notifyGatewayBeginConfig(String dsn) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().notifyGatewayConfig(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取候选节点
     *
     * @param dsn 网关dsn
     * @return
     */
    public Observable<BaseResult<Boolean>> fetchCandidateNodes(String dsn) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().fetchCandidateNodes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 通过房间号获取下属的RuleEngines。
     *
     * @param scopeId
     * @return
     */
    public Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(String scopeId) {
        return getApiService().fetchRuleEngines(scopeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存RuleEngine
     *
     * @param ruleEngineBean
     * @return
     */
    public Observable<BaseResult<Boolean>> saveRuleEngine(RuleEngineBean ruleEngineBean) {
        String json = new Gson().toJson(ruleEngineBean);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().saveRuleEngine(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /**
     * 运行RuleEngine
     *
     * @param ruleId
     * @return
     */
    public Observable<BaseResult<Boolean>> runRuleEngines(Integer ruleId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ruleId", ruleId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().runRuleEngine(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
