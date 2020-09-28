package com.ayla.hotelsaas.mvp.model;


import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.data.net.ApiService;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
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
        return RetrofitHelper.getApiService();
    }

    public Observable<BaseResult<User>> login(String account, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("account", account);
        body.addProperty("password", password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().login(1, new_body);
    }

    public Observable<BaseResult<Boolean>> register(String user_name, String account, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("userName", user_name);
        body.addProperty("account", account);
        body.addProperty("password", password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().register(new_body);
    }


    public Observable<BaseResult<Boolean>> modifyForgitPassword(String user_name, String yanzhengma) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", user_name);
        body.addProperty("code", yanzhengma);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().modifyForgitPassword(new_body);
    }


    public Observable<BaseResult<Boolean>> send_sms(String user_name) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", user_name);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().sendSmsCode(new_body);
    }


    public Observable<BaseResult<Boolean>> resert_passwoed(String phone, String new_password) {
        JsonObject body = new JsonObject();
        body.addProperty("phone", phone);
        body.addProperty("password", new_password);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().modifyOldPassword(new_body);
    }

    public Observable<BaseResult<User>> refreshToken(String refreshToken) {
        JsonObject body = new JsonObject();
        body.addProperty("refreshToken", refreshToken);
        RequestBody new_body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().refreshToken(new_body);
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
    public Observable<BaseResult<String>> roomRename(long roomId, String rename) {
        JsonObject body = new JsonObject();
        body.addProperty("roomName", rename);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().roomRename(roomId, body111);
    }


    /**
     * 删除房间
     *
     * @return
     */
    public Observable<BaseResult<String>> deleteRoomNum(long roomId) {
        return getApiService().deleteRoomNum(roomId);
    }

    /**
     * 获取authcode
     */
    public Observable<BaseResult<String>> getAuthCode(String roomId) {
        return getApiService().authCode(roomId);
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
//                .map(new Function<BaseResult<List<DeviceCategoryBean>>, BaseResult<List<DeviceCategoryBean>>>() {
//                    @Override
//                    public BaseResult<List<DeviceCategoryBean>> apply(BaseResult<List<DeviceCategoryBean>> listBaseResult) throws Exception {
//                        List<DeviceCategoryBean> data = listBaseResult.data;
//                        DeviceCategoryBean bean = new DeviceCategoryBean();
//                        bean.setId(100);
//                        bean.setName("WiFi设备");
//                        ArrayList<DeviceCategoryBean.SubBean> objects = new ArrayList<>();
//                        DeviceCategoryBean.SubBean subBean = new DeviceCategoryBean.SubBean();
//                        subBean.setId(100);
//                        subBean.setDeviceName("AY008RTK1");
//                        subBean.setName("智能插座");
//                        subBean.setDeviceConnectType(3);
//                        subBean.setNetworkType(5);
//                        subBean.setCuId(1);
//                        subBean.setOemModel("SP0-01-0-001");
//
//                        objects.add(subBean);
//                        bean.setSub(objects);
//                        data.add(bean);
//                        return listBaseResult;
//                    }
//                });
    }

    /**
     * 获取品类支持的条件、功能 项目 详情
     *
     * @return
     */
    public Observable<BaseResult<List<DeviceCategoryDetailBean>>> getDeviceCategoryDetail() {
        return getApiService().fetchDeviceCategoryDetail();
    }

    /**
     * @param deviceId
     * @param cuId
     * @param scopeId
     * @return
     */
    public Observable<BaseResult<DeviceListBean.DevicesBean>> bindDeviceWithDSN(String deviceId, long cuId, long scopeId,
                                                                                int scopeType, String deviceCategory, String deviceName, String nickName) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceId", deviceId);
        body.addProperty("scopeId", scopeId);
        body.addProperty("cuId", cuId);
        body.addProperty("scopeType", scopeType);
        body.addProperty("deviceCategory", deviceCategory);
        body.addProperty("deviceName", deviceName);
        body.addProperty("nickName", nickName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().bindDeviceWithDSN(body111);
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
    public Observable<BaseResult<List<DeviceListBean.DevicesBean>>> fetchCandidateNodes(String dsn, String deviceCategory) {
        return getApiService().fetchCandidateNodes(dsn, deviceCategory);
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
        return getApiService().fetchDeviceTemplate(oemModel)
                //特别处理：如果是智镜设备，要强行加上 场景的支持。
                .map(new Function<BaseResult<DeviceTemplateBean>, BaseResult<DeviceTemplateBean>>() {
                    @Override
                    public BaseResult<DeviceTemplateBean> apply(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        if ("a1UR1BjfznK".equals(oemModel)) {//触控面板
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
                        if ("a1dnviXyhqx".equals(oemModel)) {//六键场景开关
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
                        if ("a1009Fd5ZCJ".equals(oemModel)) {//紧急按钮设备
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
                        return deviceTemplateBeanBaseResult;
                    }
                });
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
     * 场景重新命名
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> touchPanelRenameMethod(int id, String deviceId, int cuId, String propertyName, String propertyType, String propertyValue, String deviceCategory, boolean needHandleAliService) {
        return Observable
                .fromCallable(new Callable<RequestBody>() {
                    @Override
                    public RequestBody call() throws Exception {
                        JSONObject uploadParams = new JSONObject();
                        JSONArray list = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        if (id != 0) {
                            jsonObject.put("id", id);
                        }
                        uploadParams.put("needHandleAliService", needHandleAliService);
                        jsonObject.put("deviceId", deviceId);
                        jsonObject.put("cuId", cuId);
                        jsonObject.put("propertyName", propertyName);
                        jsonObject.put("propertyType", propertyType);
                        jsonObject.put("propertyValue", propertyValue);
                        list.put(jsonObject);
                        uploadParams.put("propertyList", list);
                        return RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), uploadParams.toString());
                    }
                })
                .flatMap(new Function<RequestBody, ObservableSource<BaseResult<Boolean>>>() {
                    @Override
                    public ObservableSource<BaseResult<Boolean>> apply(RequestBody body) throws Exception {
                        return getApiService().tourchPanelRenameAndIcon(body);
                    }
                });
    }

    /**
     * 获取所有的场景按键信息
     *
     * @return
     */
    public Observable<BaseResult<List<TouchPanelDataBean>>> getALlTouchPanelDeviceInfo(int cuId, String deviceId) {
        return getApiService().touchpanelALlDevice(cuId, deviceId);
    }

    /**
     * 移除设备
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> deviceRemove(String deviceId, long scopeId, String scopeType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("scopeId", scopeId + "");
        jsonObject.addProperty("scopeType", scopeType);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().removeDevice(body111);
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    public Observable<BaseResult<PersonCenter>> getUserInfo() {
        return getApiService().getUserInfo();
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
    public Observable<BaseResult<List<TreeListBean>>> fetchTransferTreeList(String hotelId) {
        return getApiService().fetchTransferTreeList(hotelId);
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
    public Observable<BaseResult<NetworkConfigGuideBean>> getNetworkConfigGuide(String categoryId) {
        return getApiService().getNetworkConfigGuide(categoryId);
    }
}
