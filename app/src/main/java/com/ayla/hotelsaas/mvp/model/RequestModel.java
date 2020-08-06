package com.ayla.hotelsaas.mvp.model;


import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitDebugHelper;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.List;

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
        return getApiService().login(new_body);
    }

    /**
     * 获取工作订单的条数
     *
     * @param pageNum 页码 从1开始
     * @param maxNum  每页加载量
     * @return
     */
    public Observable<BaseResult<WorkOrderBean>> getWorkOrderList(int pageNum, int maxNum) {
        return getApiService().getWorkOrders(pageNum, maxNum);
    }

    /**
     * 获取房间号的条数
     *
     * @param //页码    从1开始
     * @param //每页加载量
     * @return
     */
    public Observable<BaseResult<RoomOrderBean>> getRoomOrderList(String billId, int pageNum, int maxNum) {
        return getApiService().getRoomOrders(pageNum, maxNum, billId);

    }

    /**
     * 获取设备列表
     *
     * @param //页码    从1开始
     * @param //每页加载量
     * @return
     */
    public Observable<BaseResult<DeviceListBean>> getDeviceList(long roomId, int pageNum, int maxNum) {
        JsonObject body = new JsonObject();
        body.addProperty("roomId", roomId);
        body.addProperty("pageNo", pageNum);
        body.addProperty("pageSize", maxNum);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().getDeviceList(body111);

    }

    public Observable<BaseResult<List<DeviceCategoryBean>>> getDeviceCategory() {
        return getApiService().fetchDeviceCategory();
    }

    public Observable<BaseResult<List<DeviceCategoryDetailBean>>> getDeviceCategoryDetail() {
        return getApiService().fetchDeviceCategoryDetail();
    }

    /**
     * @param deviceId
     * @param cuId
     * @param scopeId
     * @return
     */
    public Observable<BaseResult> bindDeviceWithDSN(String deviceId, long cuId, long scopeId,
                                                    int scopeType, String deviceName, String nickName) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceId", deviceId);
        body.addProperty("scopeId", scopeId);
        body.addProperty("cuId", cuId);
        body.addProperty("scopeType", scopeType);
        body.addProperty("deviceName", deviceName);
        body.addProperty("nickName", nickName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().bindDeviceWithDSN(body111);
    }

    public Observable<BaseResult<Boolean>> unbindDeviceWithDSN(String dsn, int scopeId) {
        JsonObject body = new JsonObject();
        body.addProperty("device_id", dsn);
        body.addProperty("scope_id", scopeId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().unbindDeviceWithDSN(body111)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取候选节点
     *
     * @param dsn 网关dsn
     * @return
     */
    public Observable<BaseResult<List<DeviceListBean.DevicesBean>>> fetchCandidateNodes(String dsn) {
        return getApiService().fetchCandidateNodes(dsn);
    }

    /**
     * 通过房间号获取下属的RuleEngines。
     *
     * @param scopeId
     * @return
     */
    public Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(long scopeId) {
        return getApiService().fetchRuleEngines(scopeId);
    }

    /**
     * 保存RuleEngine
     *
     * @param ruleEngineBean
     * @return
     */
    public Observable<BaseResult> saveRuleEngine(RuleEngineBean ruleEngineBean) {
        String json = new Gson().toJson(ruleEngineBean);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().saveRuleEngine(body111);
    }


    /**
     * 更新RuleEngine
     *
     * @param ruleEngineBean
     * @return
     */
    public Observable<BaseResult> updateRuleEngine(RuleEngineBean ruleEngineBean) {
        String json = new Gson().toJson(ruleEngineBean);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().updateRuleEngine(body111);
    }

    /**
     * 设置属性
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> updateProperty(String deviceId, String propertyName, String propertyValue) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("propertyName", propertyName);
        jsonObject.addProperty("propertyValue", propertyValue);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().updateProperty(deviceId, body111);
    }

    /**
     * 运行RuleEngine
     *
     * @param ruleId
     * @return
     */
    public Observable<BaseResult<Boolean>> runRuleEngine(long ruleId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ruleId", ruleId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().runRuleEngine(body111);
    }

    public Observable<BaseResult<Boolean>> deleteRuleEngine(long ruleId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ruleId", ruleId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deleteRuleEngine(body111);
    }

    /**
     * 查询设备物模板信息
     *
     * @param oemModel
     * @return
     */
    public Observable<BaseResult<DeviceTemplateBean>> fetchDeviceTemplate(String oemModel) {
//        String sss = "{\"code\":0,\"msg\":\"success\",\"data\":{\"id\":4,\"version\":\"1596617919129\",\"deviceCategory\":\"ZB-NODE-LC1-008\",\"deviceType\":0,\"deviceNodeType\":0,\"attributes\":[{\"code\":\"8:0x0000:app-version\",\"value\":null,\"custom\":2,\"dataType\":1,\"direction\":1,\"description\":\"APP版本\",\"displayName\":\"APP版本\",\"readWriteMode\":1},{\"code\":\"8:0x0000:version\",\"value\":null,\"custom\":2,\"dataType\":1,\"direction\":1,\"description\":\"版本\",\"displayName\":\"版本\",\"readWriteMode\":1},{\"code\":\"8:0x0102:Mode\",\"value\":null,\"custom\":2,\"dataType\":2,\"direction\":2,\"description\":\"Mode\",\"displayName\":\"Mode\",\"readWriteMode\":2},{\"code\":\"8:0x0102:MotorControl\",\"value\":[{\"code\":null,\"dataType\":7,\"displayName\":\"关\",\"description\":null,\"value\":0},{\"code\":null,\"dataType\":7,\"displayName\":\"开\",\"description\":null,\"value\":1},{\"code\":null,\"dataType\":7,\"displayName\":\"停止\",\"description\":null,\"value\":2},{\"code\":null,\"dataType\":7,\"displayName\":\"正转\",\"description\":null,\"value\":3},{\"code\":null,\"dataType\":7,\"displayName\":\"反转\",\"description\":null,\"value\":4}],\"custom\":2,\"dataType\":7,\"direction\":2,\"description\":\"MotorControl\",\"displayName\":\"MotorControl\",\"readWriteMode\":2},{\"code\":\"8:0x0102:OpenPercent\",\"value\":null,\"custom\":2,\"dataType\":2,\"direction\":2,\"description\":\"窗帘开的百分比\",\"displayName\":\"窗帘开的百分比\",\"readWriteMode\":2},{\"code\":\"oem_host_version\",\"value\":null,\"custom\":1,\"dataType\":1,\"direction\":1,\"description\":\"oem_host_version\",\"displayName\":\"oem_host_version\",\"readWriteMode\":1}]}}";
//        BaseResult<DeviceTemplateBean> o = new Gson().fromJson(sss, new TypeToken<BaseResult<DeviceTemplateBean>>() {
//        }.getType());
//        return Observable.just(o);
        return getApiService().fetchDeviceTemplate(oemModel);
    }


    /**
     * 设置重新命名
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> deviceRename(String deviceId, String nickName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickName", nickName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deviceRename(deviceId, body111);
    }


    /**
     * 移除设备
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> deviceRemove(String deviceId, String scopeId, String scopeType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("scopeId", scopeId);
        jsonObject.addProperty("scopeType", scopeType);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().removeDevice(body111);
    }
}
