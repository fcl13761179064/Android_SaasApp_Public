package com.ayla.hotelsaas.mvp.model;


import android.text.TextUtils;

import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.GatewayNodeBean;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.PropertyDataPointBean;
import com.ayla.hotelsaas.bean.PropertyNicknameBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.BaseResultTransformer;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @描述 网络请求Model
 * @作者 fanchunlei
 * @时间 2020/6/3
 */
public class RequestModel {

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
        return RetrofitHelper.getApiService();
    }

    public Observable<User> login(String account, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("account", account);
        body.addProperty("password", password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().login(new_body).compose(new BaseResultTransformer<BaseResult<User>, User>() {
        });
    }

    public Observable<Boolean> register(String user_name, String account, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("userName", user_name);
        body.addProperty("account", account);
        body.addProperty("password", password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().register(new_body).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    public Observable<Boolean> modifyForgitPassword(String user_name, String yanzhengma) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", user_name);
        body.addProperty("code", yanzhengma);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().modifyForgitPassword(new_body).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    public Observable<Boolean> send_sms(String user_name) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", user_name);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().sendSmsCode(new_body).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    public Observable<Boolean> resert_passwoed(String phone, String new_password) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", phone);
        body.addProperty("password", new_password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().modifyOldPassword(new_body).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<User> refreshToken(String refreshToken) {
        JsonObject body = new JsonObject();
        body.addProperty("refreshToken", refreshToken);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().refreshToken(new_body).compose(new BaseResultTransformer<BaseResult<User>, User>() {
        });
    }

    /**
     * 获取工作订单的条数
     *
     * @param pageNum 页码 从1开始
     * @param maxNum  每页加载量
     * @param tradeId
     * @return
     */
    public Observable<WorkOrderBean> getWorkOrderList(int pageNum, int maxNum, String tradeId) {
        return getApiService().getWorkOrders(pageNum, maxNum,tradeId)
                .compose(new BaseResultTransformer<BaseResult<WorkOrderBean>, WorkOrderBean>() {
                });
    }


    /**
     * 创建房间订单
     *
     * @param pageNum 页码 从1开始
     * @param maxNum  每页加载量
     * @return
     */
    public Observable<BaseResult<RoomManageBean>> getCreateRoomOrder(int pageNum, int maxNum) {
        return getApiService().getcreateRoom(pageNum, maxNum);
    }

    /**
     * 创建房间订单
     *
     * @return
     */
    public Observable<BaseResult<String>> createRoomOrder(String roomName) {
        JsonObject body = new JsonObject();
        body.addProperty("roomName", roomName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().createRoom(body111);
    }

    /**
     * 重新命名房间号
     *
     * @return
     */
    public Observable<String> roomRename(long roomId, String rename) {
        JsonObject body = new JsonObject();
        body.addProperty("roomName", rename);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().roomRename(roomId, body111).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }


    /**
     * 删除房间
     *
     * @return
     */
    public Observable<String> deleteRoomNum(long roomId) {
        return getApiService().deleteRoomNum(roomId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }

    /**
     * 获取authcode
     */
    public Observable<String> getAuthCode(String roomId) {
            return getApiService().authCodetwo(roomId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
            });

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
    public Observable<DeviceListBean> getAllDeviceList(long roomId, int pageNum, int maxNum) {
        JsonObject body = new JsonObject();
        body.addProperty("roomId", roomId);
        body.addProperty("pageNo", pageNum);
        body.addProperty("pageSize", maxNum);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().getDeviceList(body111).compose(new BaseResultTransformer<BaseResult<DeviceListBean>, DeviceListBean>() {
        });
    }

    /**
     * 获取设备列表
     *
     * @param //页码    从1开始
     * @param //每页加载量
     * @return
     */
    public Observable<DeviceListBean> getDeviceList(long roomId, int pageNum, int maxNum, long regionId) {
        JsonObject body = new JsonObject();
        body.addProperty("roomId", roomId);
        body.addProperty("pageNo", pageNum);
        body.addProperty("pageSize", maxNum);
        body.addProperty("regionId", regionId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().getDeviceList(body111).compose(new BaseResultTransformer<BaseResult<DeviceListBean>, DeviceListBean>() {
        });
    }

    public Observable<List<DeviceCategoryBean>> getDeviceCategory() {
        return getApiService().fetchDeviceCategory().compose(new BaseResultTransformer<BaseResult<List<DeviceCategoryBean>>, List<DeviceCategoryBean>>() {
        });
    }

    /**
     * 获取品类支持的条件、功能 项目 详情
     *
     * @return
     */
    public Observable<List<DeviceCategoryDetailBean>> getDeviceCategoryDetail(long roomId) {
        JsonObject body = new JsonObject();
        body.addProperty("resourceId", roomId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().fetchDeviceCategoryDetail(body111).compose(new BaseResultTransformer<BaseResult<List<DeviceCategoryDetailBean>>, List<DeviceCategoryDetailBean>>() {
        });
    }

    /**
     * 获取品类支持的条件、功能 项目 详情
     *
     * @return
     */
    public Observable<DeviceCategoryDetailBean> getDeviceCategoryDetail(String pid) {
        return getApiService().fetchDeviceCategoryDetail(pid).compose(new BaseResultTransformer<BaseResult<DeviceCategoryDetailBean>, DeviceCategoryDetailBean>() {
        });
    }

    /**
     * 绑定 或者 替换 一个设备。
     *
     * @param deviceId
     * @param cuId
     * @param scopeId
     * @return
     */
    public Observable<DeviceListBean.DevicesBean> bindOrReplaceDeviceWithDSN(String deviceId, String waitBindDeviceId,
                                                                             String replaceDeviceId, long cuId, long scopeId,
                                                                             int scopeType, String deviceCategory, String pid, String nickName) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceId", deviceId);
        body.addProperty("waitBindDeviceId", waitBindDeviceId);
        body.addProperty("replaceDeviceId", replaceDeviceId);
        body.addProperty("scopeId", scopeId);
        body.addProperty("cuId", cuId);
        body.addProperty("scopeType", scopeType);
        body.addProperty("deviceCategory", deviceCategory);
        body.addProperty("pid", pid);
        body.addProperty("nickName", nickName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());

        Observable<BaseResult<DeviceListBean.DevicesBean>> observable;
        if (TextUtils.isEmpty(replaceDeviceId)) {
            observable = getApiService().bindDeviceWithDSN(body111);
        } else {
            observable = getApiService().bindReplaceDeviceWithDSN(body111);
        }
        return observable.compose(new BaseResultTransformer<BaseResult<DeviceListBean.DevicesBean>, DeviceListBean.DevicesBean>() {
        }).doOnNext(new Consumer<DeviceListBean.DevicesBean>() {
            @Override
            public void accept(DeviceListBean.DevicesBean devicesBean) throws Exception {
                devicesBean.setNickname(nickName);
            }
        });
    }


    /**
     * @param mHongyanproductKey
     * @param mHongyandeviceName
     * @return
     */
    public Observable<BaseResult<String>> removeDeviceAllReleate(String mHongyanproductKey, String mHongyandeviceName) {
        JsonObject body = new JsonObject();
        body.addProperty("productKey", mHongyanproductKey);
        body.addProperty("deviceName", mHongyandeviceName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().removeDeviceAllReleate(body111);
    }

    /**
     * 获取候选节点
     *
     * @param dsn            网关dsn
     * @param deviceCategory 需要绑定节点设备的oemModel
     * @return
     */
    public Observable<List<DeviceListBean.DevicesBean>> fetchCandidateNodes(String dsn, String deviceCategory) {
        return getApiService().fetchCandidateNodes(dsn, deviceCategory).compose(new BaseResultTransformer<BaseResult<List<DeviceListBean.DevicesBean>>, List<DeviceListBean.DevicesBean>>() {
        });
    }

    /**
     * 通过房间号获取下属的RuleEngines。
     *
     * @param scopeId
     * @return
     */
    public Observable<List<RuleEngineBean>> fetchRuleEngines(long scopeId) {
        return getApiService().fetchRuleEngines(scopeId).compose(new BaseResultTransformer<BaseResult<List<RuleEngineBean>>, List<RuleEngineBean>>() {
        });
    }


    public Observable<BaseResult<DeviceFirmwareVersionBean>> fetchDeviceDetail(String deviceId) {
        return getApiService().fetchDeviceDetail(deviceId);
    }

    public Observable<BaseResult<List<GatewayNodeBean>>> getGatewayNodes(String deviceId, long scopeId) {
        return getApiService().getGatewayNodes(deviceId, scopeId);
    }

    /**
     * 保存RuleEngine
     *
     * @param ruleEngineBean
     * @return
     */
    public Observable<Boolean> saveRuleEngine(RuleEngineBean ruleEngineBean) {
        String json = new Gson().toJson(ruleEngineBean);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().saveRuleEngine(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    /**
     * 更新RuleEngine
     *
     * @param ruleEngineBean
     * @return
     */
    public Observable<Boolean> updateRuleEngine(RuleEngineBean ruleEngineBean) {
        String json = new Gson().toJson(ruleEngineBean);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().updateRuleEngine(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    /**
     * 设置属性
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> updateProperty(String deviceId, String propertyName, String propertyValue) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("propertyCode", propertyName);
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
    public Observable<Boolean> runRuleEngine(long ruleId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ruleId", ruleId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().runRuleEngine(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<Boolean> deleteRuleEngine(long ruleId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ruleId", ruleId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deleteRuleEngine(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    /**
     * 查询设备物模板信息
     *
     * @param pid
     * @return
     */
    public Observable<BaseResult<DeviceTemplateBean>> fetchDeviceTemplate(String pid) {
        return getApiService().fetchDeviceTemplate(pid)
                .doOnNext(new Consumer<BaseResult<DeviceTemplateBean>>() {
                    @Override
                    public void accept(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        DeviceTemplateBean deviceTemplateBean = deviceTemplateBeanBaseResult.data;
                        List<DeviceTemplateBean.AttributesBean> extendAttributes = deviceTemplateBean.getExtendAttributes();
                        List<DeviceTemplateBean.AttributesBean> attributes = deviceTemplateBean.getAttributes();
                        if (extendAttributes != null) {
                            if (attributes == null) {
                                attributes = new ArrayList<>();
                                deviceTemplateBean.setAttributes(attributes);
                            }
                            attributes.addAll(extendAttributes);
                        }
                    }
                })//特别处理：红外家电设备自学习的功能在物模板里面定义在extendAttributes 字段中，需要把它融合到字段attributes 里面。
                .doOnNext(new Consumer<BaseResult<DeviceTemplateBean>>() {
                    @Override
                    public void accept(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        if ("a1UR1BjfznK".equals(pid)) {//触控面板
                            DeviceTemplateBean data = deviceTemplateBeanBaseResult.data;
                            if (data != null) {
                                List<DeviceTemplateBean.AttributesBean> attributes = data.getAttributes();
                                if (attributes != null) {
                                    DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                    attributesBean.setCode("KeyValueNotification.KeyValue");
                                    attributesBean.setDisplayName("场景模式");
                                    attributesBean.setDataType(1);
                                    attributes.add(attributesBean);
                                    {
                                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("回家");
                                            valueBean.setValue("1");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("离店模式");
                                            valueBean.setValue("2");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("睡眠模式");
                                            valueBean.setValue("3");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("影音");
                                            valueBean.setValue("4");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("全开");
                                            valueBean.setValue("5");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("全关");
                                            valueBean.setValue("6");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("开窗帘");
                                            valueBean.setValue("7");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("关窗帘");
                                            valueBean.setValue("8");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("暂停");
                                            valueBean.setValue("9");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("阅读");
                                            valueBean.setValue("10");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("睡眠");
                                            valueBean.setValue("11");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("起床");
                                            valueBean.setValue("12");
                                            valueBeans.add(valueBean);
                                        }
                                        attributesBean.setValue(valueBeans);
                                    }
                                }
                            }
                        }
                        if ("a1dnviXyhqx".equals(pid)) {//六键场景开关
                            DeviceTemplateBean data = deviceTemplateBeanBaseResult.data;
                            if (data != null) {
                                List<DeviceTemplateBean.AttributesBean> attributes = data.getAttributes();
                                if (attributes != null) {
                                    DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                    attributesBean.setCode("KeyValueNotification.KeyValue");
                                    attributesBean.setDisplayName("场景模式");
                                    attributesBean.setDataType(1);
                                    attributes.add(attributesBean);
                                    {
                                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("回家");
                                            valueBean.setValue("1");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("会客");
                                            valueBean.setValue("2");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("全开");
                                            valueBean.setValue("3");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("离家");
                                            valueBean.setValue("4");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("影音");
                                            valueBean.setValue("5");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("全关");
                                            valueBean.setValue("6");
                                            valueBeans.add(valueBean);
                                        }
                                        attributesBean.setValue(valueBeans);
                                    }
                                }
                            }
                        }
                        if ("a1009Fd5ZCJ".equals(pid)) {//紧急按钮设备
                            DeviceTemplateBean data = deviceTemplateBeanBaseResult.data;
                            if (data != null) {
                                List<DeviceTemplateBean.AttributesBean> attributes = data.getAttributes();
                                if (attributes != null) {
                                    DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                    attributesBean.setCode("EmergencyTriggerAlarm.AlarmType");
                                    attributesBean.setDisplayName("紧急触发报警");
                                    attributesBean.setDataType(1);
                                    attributes.add(attributesBean);
                                    {
                                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("紧急报警");
                                            valueBean.setValue("1");
                                            valueBeans.add(valueBean);
                                        }
                                        attributesBean.setValue(valueBeans);
                                    }
                                }
                            }
                        }
                    }
                })//特别处理：如果是智镜设备，要强行加上 场景的支持。
                ;
    }

    /**
     * 设置重新命名
     *
     * @return
     */
    public Observable<Boolean> deviceRename(String deviceId, String nickName, String pointName, long regionId, String regionName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickName", nickName);
        jsonObject.addProperty("pointName", pointName);
        jsonObject.addProperty("regionId", regionId);
        jsonObject.addProperty("regionName", regionName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deviceRename(deviceId, body111)
                .compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
                });
    }

    /**
     * 设置设备属性的别名
     *
     * @param nickNameId
     * @param deviceId
     * @param cuId
     * @param propertyName
     * @return
     */
    public Observable<Boolean> updatePropertyNickName(String nickNameId, String deviceId, int cuId, String propertyName, String propertyNickName) {
        return Observable
                .fromCallable(new Callable<RequestBody>() {
                    @Override
                    public RequestBody call() throws Exception {
                        JSONObject uploadParams = new JSONObject();
                        JSONArray list = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        if (!TextUtils.isEmpty(nickNameId)) {
                            jsonObject.put("id", nickNameId);
                        }
                        uploadParams.put("needHandleAliService", false);
                        jsonObject.put("deviceId", deviceId);
                        jsonObject.put("cuId", cuId);
                        jsonObject.put("propertyType", "nickName");
                        jsonObject.put("propertyName", propertyName);
                        jsonObject.put("propertyValue", propertyNickName);
                        list.put(jsonObject);
                        uploadParams.put("propertyList", list);
                        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), uploadParams.toString());
                    }
                })
                .flatMap(new Function<RequestBody, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(RequestBody body) throws Exception {
                        return getApiService().updatePropertyNickName(body).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
                        });
                    }
                });
    }

    /**
     * 获取设备属性的别名
     *
     * @return
     */
    public Observable<List<PropertyNicknameBean>> fetchPropertyNickname(int cuId, String deviceId) {
        return getApiService().fetchPropertyNickname(cuId, deviceId)
                .compose(new BaseResultTransformer<BaseResult<List<PropertyNicknameBean>>, List<PropertyNicknameBean>>() {
                });
    }

    /**
     * 移除设备
     *
     * @return
     */
    public Observable<Boolean> deviceRemove(String deviceId, long scopeId, String scopeType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("scopeId", scopeId + "");
        jsonObject.addProperty("scopeType", scopeType);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().removeDevice(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public Observable<PersonCenter> getUserInfo() {
        return getApiService().getUserInfo().compose(new BaseResultTransformer<BaseResult<PersonCenter>, PersonCenter>() {
        });
    }

    /**
     * @return
     */
    public Observable<BaseResult<HotelListBean>> fetchTransferHotelList() {
        return getApiService().fetchTransferHotelList(1, Integer.MAX_VALUE);
    }

    /**
     * @return
     */
    public Observable<BaseResult<List<TreeListBean>>> fetchTransferTreeList(String billId, String hotelId) {
        return getApiService().fetchTransferTreeList(billId, hotelId);
    }

    /**
     * @return
     */
    public Observable<BaseResult<TransferRoomListBean>> fetchTransferRoomList(String hotelId) {
        return getApiService().fetchTransferRoomList(1, Integer.MAX_VALUE, hotelId);
    }

    /**
     * @return
     */
    public Observable<BaseResult> transferToHotel(String hotelId, String[] roomIdList) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hotelId", hotelId);
        JsonArray roomIdListJsonArray = new JsonArray();
        for (String s : roomIdList) {
            roomIdListJsonArray.add(s);
        }
        jsonObject.add("roomIdList", roomIdListJsonArray);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().transferToHotel(body111);
    }

    /**
     * @return
     */
    public Observable<BaseResult> transferToStruct(String hotelId, String structId, String[] roomIdList) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hotelId", hotelId);
        jsonObject.addProperty("structId", structId);
        JsonArray roomIdListJsonArray = new JsonArray();
        for (String s : roomIdList) {
            roomIdListJsonArray.add(s);
        }
        jsonObject.add("roomIdList", roomIdListJsonArray);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().transferToStruct(body111);
    }

    /**
     * @return
     */
    public Observable<BaseResult> transferToRoom(String hotelId, String sourceRoomId, String targetRoomId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("hotelId", hotelId);
        jsonObject.addProperty("sourceRoomId", sourceRoomId);
        jsonObject.addProperty("targetRoomId", targetRoomId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().transferToRoom(body111);
    }

    /**
     * @return
     */
    public Observable<List<NetworkConfigGuideBean>> getNetworkConfigGuide(String pid) {
        return getApiService().getNetworkConfigGuide(pid).compose(new BaseResultTransformer<BaseResult<List<NetworkConfigGuideBean>>, List<NetworkConfigGuideBean>>() {
        });
    }

    /**
     * @return
     */
    public Observable<Object> createBill(String title, int trade, int type) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("trade", trade);
        jsonObject.addProperty("type", type);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().createWorkOrder(body111).compose(new BaseResultTransformer<BaseResult<Object>, Object>() {
        });
    }

    /**
     * 当没有新版本信息时，data = null 。
     *
     * @return
     */
    public Observable<VersionUpgradeBean> getAppVersion(int versionCode) {
        return getApiService().getAppVersion(0, versionCode).compose(new BaseResultTransformer<BaseResult<VersionUpgradeBean>, VersionUpgradeBean>() {
        });
    }

    public Observable<Boolean> checkRadioExists(long scopeId) {
        return getApiService().checkRadioExists(scopeId).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<Boolean> checkVoiceRule(long scopeId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("scopeId", scopeId);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().checkVoiceRule(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<List<PurposeCategoryBean>> getPurposeCategory() {
        return getApiService().getPurposeCategory().compose(new BaseResultTransformer<BaseResult<List<PurposeCategoryBean>>, List<PurposeCategoryBean>>() {
        });
    }

    public Observable<BaseResult> purposeBatchSave(long scopeId, String deviceId, String[] purposeName, String[] purposeId, int[] purposeCategory) throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray purPoseInfoListJsonArray = new JSONArray();
        for (int i = 0; i < purposeName.length; i++) {
            JSONObject purPoseInfoJsonObject = new JSONObject();
            purPoseInfoJsonObject.put("deviceId", deviceId);
            purPoseInfoJsonObject.put("purposeCategory", purposeCategory[i]);
            purPoseInfoJsonObject.put("purposeId", purposeId[i]);
            purPoseInfoJsonObject.put("purposeName", purposeName[i]);
            purPoseInfoJsonObject.put("purposeSourceDeviceType", 0);
            purPoseInfoJsonObject.put("scopeId", scopeId);
            purPoseInfoJsonObject.put("scopeType", 2);
            purPoseInfoListJsonArray.put(purPoseInfoJsonObject);
        }
        jsonObject.put("purPoseInfoList", purPoseInfoListJsonArray);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().purposeBatchSave(body111);
    }

    public Observable updatePurpose(String deviceId, int purposeCategory) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("purposeCategory", purposeCategory);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().updatePurpose(body111);
    }

    /**
     * 根据设置的功能别名，篡改物模板里面的displayname。
     *
     * @param deviceId
     */
    public ObservableTransformer<DeviceTemplateBean, DeviceTemplateBean> modifyTemplateDisplayName(String deviceId) {
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        return new ObservableTransformer<DeviceTemplateBean, DeviceTemplateBean>() {
            @NonNull
            @Override
            public ObservableSource<DeviceTemplateBean> apply(@NonNull Observable<DeviceTemplateBean> upstream) {
                return upstream.zipWith(RequestModel.getInstance()
                                .fetchPropertyNickname(devicesBean.getCuId(), deviceId),
                        new BiFunction<DeviceTemplateBean, List<PropertyNicknameBean>, DeviceTemplateBean>() {
                            @Override
                            public DeviceTemplateBean apply(DeviceTemplateBean attributesBeans, List<PropertyNicknameBean> propertyNicknameBeans) throws Exception {
                                for (DeviceTemplateBean.AttributesBean attributesBean : attributesBeans.getAttributes()) {
                                    for (PropertyNicknameBean propertyNicknameBean : propertyNicknameBeans) {
                                        if ("nickName".equals(propertyNicknameBean.getPropertyType()) &&
                                                TextUtils.equals(attributesBean.getCode(), propertyNicknameBean.getPropertyName())) {
                                            attributesBean.setDisplayName(propertyNicknameBean.getPropertyValue());
                                        }
                                        if ("Words".equals(propertyNicknameBean.getPropertyType())) {
                                            if ("KeyValueNotification.KeyValue".equals(attributesBean.getCode())) {//如果是触控面板的按键名称
                                                if (attributesBean.getValue() != null) {
                                                    for (DeviceTemplateBean.AttributesBean.ValueBean valueBean : attributesBean.getValue()) {
                                                        if (TextUtils.equals(valueBean.getValue(), propertyNicknameBean.getPropertyName())) {
                                                            valueBean.setDisplayName(propertyNicknameBean.getPropertyValue());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                return attributesBeans;
                            }
                        });
            }
        };
    }

    public Observable<Boolean> roomPlanCheck(long scopeId) {
        return getApiService().roomPlanCheck(scopeId).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<String> roomPlanExport(long scopeId) {
        return getApiService().roomExport(scopeId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }

    public Observable<Object> roomPlanImport(long scopeId, String shareCode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("scopeId", scopeId);
        jsonObject.addProperty("shareCode", shareCode);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().roomPlanImport(body111).compose(new BaseResultTransformer<BaseResult<Object>, Object>() {
        });
    }

    public Observable<Object> resetRoomPlan(long scopeId) {
        return getApiService().resetRoomPlan(scopeId).compose(new BaseResultTransformer<BaseResult<Object>, Object>() {
        });
    }

    public Observable<String> getNodeGateway(String deviceId) {
        return getApiService().getNodeGateway(deviceId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }

    public Observable<PropertyDataPointBean> getPropertyDataPoint(String deviceId, String propertyName) {
        return getApiService().getPropertyDataPoint(deviceId, propertyName).compose(new BaseResultTransformer<BaseResult<PropertyDataPointBean>, PropertyDataPointBean>() {
        });
    }

    public Observable<List<RuleEngineBean>> getRuleListByUniqListFunction(long scopeId, JSONArray uniqList) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("scopeId", scopeId);
        jsonObject.put("uniqList", uniqList);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().getRuleListByUniqListFunction(body111)
                .compose(new BaseResultTransformer<BaseResult<List<RuleEngineBean>>, List<RuleEngineBean>>() {
                })
                .onErrorReturn(new Function<Throwable, List<RuleEngineBean>>() {
                    @Override
                    public List<RuleEngineBean> apply(@NonNull Throwable throwable) throws Exception {
                        if (throwable instanceof ServerBadException && ((ServerBadException) throwable).isSuccess()) {
                            return new ArrayList<>();
                        }
                        throw new Exception(throwable);
                    }
                });
    }

    /**
     * 获取所有设备位置，全屋还是 卧室
     *
     * @return
     * @param room_id
     */
    public Observable<List<DeviceLocationBean>> getAllDeviceLocation(Long room_id) {
        return getApiService().getAllDeviceLocation(room_id)
                .compose(new BaseResultTransformer<BaseResult<List<DeviceLocationBean>>,List<DeviceLocationBean>>() {
                });
    }
}
