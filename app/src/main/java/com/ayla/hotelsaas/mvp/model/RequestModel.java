package com.ayla.hotelsaas.mvp.model;


import static com.ayla.hotelsaas.application.MyApplication.getContext;

import android.text.TextUtils;
import android.util.Log;

import com.ayla.hotelsaas.api.ApiService;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.ConditionOrActionData;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.bean.DeviceGroupData;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.FirmwareUpdateData;
import com.ayla.hotelsaas.bean.GatewayNodeBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;
import com.ayla.hotelsaas.bean.GroupDetail;
import com.ayla.hotelsaas.bean.GroupDetailBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.bean.GroupRequestBean;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.MarshallEntryBean;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.PropertyDataPointBean;
import com.ayla.hotelsaas.bean.PropertyNicknameBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.bean.PurposeComboBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RoomTypeShowBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.constant.ShowWay;
import com.ayla.hotelsaas.constant.SwitchWorkMode;
import com.ayla.hotelsaas.data.net.BaseResultTransformer;
import com.ayla.hotelsaas.data.net.RetrofitHelper;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
 * @?????? ????????????Model
 * @?????? fanchunlei
 * @?????? 2020/6/3
 */
public class RequestModel {

    private volatile static RequestModel instance = null;

    private RequestModel() {
    }

    /*  1.??????????????????
      2.?????????????????????????????????????????????synchronized??????????????????????????????
      ?????????????????????????????????????????????????????????????????????synchronized????????????????????????????????????????????????????????????????????????volatile???*/
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
     * ??????????????????
     *
     * @param pageNum ?????? ???1??????
     * @param maxNum  ???????????????
     * @return
     */
    public Observable<BaseResult<RoomManageBean>> getCreateRoomOrder(int pageNum, int maxNum) {
        return getApiService().getcreateRoom(pageNum, maxNum);
    }

    /**
     * ??????????????????
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
     * ?????????????????????
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
     * ????????????
     *
     * @return
     */
    public Observable<String> deleteRoomNum(long roomId) {
        return getApiService().deleteRoomNum(roomId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }

    /**
     * ??????authcode
     */
    public Observable<String> getAuthCode(String roomId) {
        String type = SharePreferenceUtils.getString(getContext(), ConstantValue.SP_SAAS, "1");
        if ("1".equals(type)) {
            return getApiService().authCode(roomId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
            });
        } else {
            return getApiService().authCodetwo(roomId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
            });
        }
    }

    /**
     * ????????????????????????
     *
     * @param //??????    ???1??????
     * @param //???????????????
     * @return
     */
    public Observable<BaseResult<RoomOrderBean>> getRoomOrderList(String billId, int pageNum, int maxNum) {
        return getApiService().getRoomOrders(pageNum, maxNum, billId);

    }

    /**
     * ??????????????????
     *
     * @param //??????    ???1??????
     * @param //???????????????
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
     * ????????????????????????
     *
     * @param //??????    ???1??????
     * @param //???????????????
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


    public Observable<DeviceCategoryBean.SubBean.NodeBean> getDevicePid(String pid) {
        return getApiService().getDevicePid(pid).compose(new BaseResultTransformer<BaseResult<DeviceCategoryBean.SubBean.NodeBean>, DeviceCategoryBean.SubBean.NodeBean>() {
        });
    }

    /**
     * ???????????????????????????????????? ?????? ??????
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
     * ???????????????????????????????????? ?????? ??????
     *
     * @return
     */
    public Observable<DeviceCategoryDetailBean> getDeviceCategoryDetail(String pid) {
        return getApiService().fetchDeviceCategoryDetail(pid).compose(new BaseResultTransformer<BaseResult<DeviceCategoryDetailBean>, DeviceCategoryDetailBean>() {
        });
    }

    /**
     * ?????? ?????? ?????? ???????????????
     *
     * @param deviceId
     * @param cuId
     * @param scopeId
     * @return
     */
    public Observable<DeviceListBean.DevicesBean> bindOrReplaceDeviceWithDSN(String deviceId, String waitBindDeviceId,
                                                                             String replaceDeviceId, long cuId, long scopeId,
                                                                             int scopeType, String deviceCategory, String pid, String nickName, String regToken, String tempToken) {
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
        if (!TextUtils.isEmpty(regToken) && !TextUtils.isEmpty(tempToken)) {
            body.addProperty("regToken", regToken);
            body.addProperty("tempToken", tempToken);
        }
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
     * ??????????????????
     *
     * @param dsn            ??????dsn
     * @param deviceCategory ???????????????????????????oemModel
     * @return
     */
    public Observable<List<DeviceListBean.DevicesBean>> fetchCandidateNodes(String dsn, String deviceCategory) {
        return getApiService().fetchCandidateNodes(dsn, deviceCategory).compose(new BaseResultTransformer<BaseResult<List<DeviceListBean.DevicesBean>>, List<DeviceListBean.DevicesBean>>() {
        });
    }

    /**
     * ??????????????????????????????RuleEngines???
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
     * ??????RuleEngine
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
     * ??????RuleEngine
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
     * ????????????
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
     * ??????RuleEngine
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
     * ???????????????????????????
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
                        if (attributes != null) {
                            deviceTemplateBean.setAttributes(attributes);
                        } else {
                            deviceTemplateBean.setAttributes(extendAttributes);
                        }
                    }
                })//??????????????????????????????????????????????????????????????????????????????extendAttributes ???????????????????????????????????????attributes ?????????
                .doOnNext(new Consumer<BaseResult<DeviceTemplateBean>>() {
                    @Override
                    public void accept(BaseResult<DeviceTemplateBean> deviceTemplateBeanBaseResult) throws Exception {
                        if ("ZBSCN-A000004".equals(pid)) {//????????????
                            DeviceTemplateBean data = deviceTemplateBeanBaseResult.data;
                            if (data != null) {
                                List<DeviceTemplateBean.AttributesBean> attributes = data.getAttributes();
                                if (attributes != null) {
                                    DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                    attributesBean.setCode("KeyValueNotification.KeyValue");
                                    attributesBean.setDisplayName("????????????");
                                    attributesBean.setDataType(1);
                                    attributes.add(attributesBean);
                                    {
                                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("1");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("????????????");
                                            valueBean.setValue("2");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("????????????");
                                            valueBean.setValue("3");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("4");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("5");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("6");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("?????????");
                                            valueBean.setValue("7");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("?????????");
                                            valueBean.setValue("8");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("9");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("10");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("11");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("12");
                                            valueBeans.add(valueBean);
                                        }
                                        attributesBean.setValue(valueBeans);
                                    }
                                }
                            }
                        }
                        if ("ZBSCN-A000007".equals(pid)) {//??????????????????
                            DeviceTemplateBean data = deviceTemplateBeanBaseResult.data;
                            if (data != null) {
                                List<DeviceTemplateBean.AttributesBean> attributes = data.getAttributes();
                                if (attributes != null) {
                                    DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                                    attributesBean.setCode("KeyValueNotification.KeyValue");
                                    attributesBean.setDisplayName("????????????");
                                    attributesBean.setDataType(1);
                                    attributes.add(attributesBean);
                                    {
                                        List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("1");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("2");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("3");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("4");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("5");
                                            valueBeans.add(valueBean);
                                        }
                                        {
                                            DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                            valueBean.setDataType(1);
                                            valueBean.setDisplayName("??????");
                                            valueBean.setValue("6");
                                            valueBeans.add(valueBean);
                                        }
                                        attributesBean.setValue(valueBeans);
                                    }
                                }
                            }
                        }
                    }
                });//?????????????????????????????????????????????????????? ??????????????????

    }

    /**
     * ??????????????????
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
     * ??????????????????
     *
     * @return
     */
    public Observable<Boolean> deviceSigleRename(String deviceId, String nickName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nickName", nickName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deviceRename(deviceId, body111)
                .compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
                });
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public Observable<Boolean> devicePositionSite(String deviceId, long regionId, String regionName) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("regionId", regionId);
        jsonObject.addProperty("regionName", regionName);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().deviceRename(deviceId, body111)
                .compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
                });
    }

    /**
     * ???????????????????????????
     *
     * @param nickNameId
     * @param deviceId
     * @param cuId
     * @param propertyName
     * @param is_curtain_switch
     * @param position
     * @return
     */
    public Observable<Boolean> updatePropertyNickName(String nickNameId, String deviceId, int cuId, String propertyName, String propertyNickName, boolean is_curtain_switch, int position) {
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
                        if (is_curtain_switch) {
                            jsonObject.put("propertyName", "CurtainOperation_" + position);
                        } else {
                            jsonObject.put("propertyName", propertyName);
                        }
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
     * ???????????????????????????
     *
     * @return
     */
    public Observable<List<PropertyNicknameBean>> fetchPropertyNickname(int cuId, String deviceId) {
        return getApiService().fetchPropertyNickname(cuId, deviceId)
                .compose(new BaseResultTransformer<BaseResult<List<PropertyNicknameBean>>, List<PropertyNicknameBean>>() {
                });
    }

    /**
     * ????????????
     *
     * @return
     */
    public Observable<Boolean> deviceRemove(String deviceId, long scopeId, String scopeType, String pid) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("scopeId", scopeId + "");
        jsonObject.addProperty("scopeType", scopeType);
        jsonObject.addProperty("pid", pid);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().removeDevice(body111).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    /**
     * ??????????????????
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
     * ??????????????????????????????data = null ???
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


    public Observable MultiupdatePosition(String deviceId, Long purposeCategory) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("purposeCategory", purposeCategory);

        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().updatePurpose(body111);
    }

    /**
     * ??????????????????????????????????????????????????????displayname???
     *
     * @param deviceId
     */
    public ObservableTransformer<DeviceTemplateBean, DeviceTemplateBean> modifyTemplateDisplayName(String deviceId) {
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        return new ObservableTransformer<DeviceTemplateBean, DeviceTemplateBean>() {
            @NonNull
            @Override
            public ObservableSource<DeviceTemplateBean> apply(@NonNull Observable<DeviceTemplateBean> upstream) {
                return upstream.zipWith(RequestModel.getInstance().fetchPropertyNickname(devicesBean.getCuId(), deviceId), new BiFunction<DeviceTemplateBean, List<PropertyNicknameBean>, DeviceTemplateBean>() {
                    @Override
                    public DeviceTemplateBean apply(DeviceTemplateBean attributesBeans, List<PropertyNicknameBean> propertyNicknameBeans) throws Exception {
                        for (DeviceTemplateBean.AttributesBean attributesBean : attributesBeans.getAttributes()) {
                            attributesBeans.setDeviceId(deviceId);
                            for (PropertyNicknameBean propertyNicknameBean : propertyNicknameBeans) {
                                if ("nickName".equals(propertyNicknameBean.getPropertyType()) && TextUtils.equals(attributesBean.getCode(), propertyNicknameBean.getPropertyName())) {
                                    attributesBean.setDisplayName(propertyNicknameBean.getPropertyValue());
                                }
                                if ("Words".equals(propertyNicknameBean.getPropertyType())) {
                                    if ("KeyValueNotification.KeyValue".equals(attributesBean.getCode())) {//????????????????????????????????????
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
                        if (attributesBeans.getAttributes().size() == 0 && attributesBeans.getEvents().size() > 0) {
                            attributesBeans.setDeviceId(deviceId);
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
     * ??????????????????????????????????????? ??????
     *
     * @param room_id
     * @return
     */
    public Observable<List<DeviceLocationBean>> getAllDeviceLocation(Long room_id) {
        return getApiService().getAllDeviceLocation(room_id)
                .compose(new BaseResultTransformer<BaseResult<List<DeviceLocationBean>>, List<DeviceLocationBean>>() {
                });
    }

    /**
     * A2??????????????????????????????
     *
     * @return
     */
    public Observable<A2BindInfoBean> getA2BindInfo(String deviceId) {
        return getApiService().getA2BindInfo(deviceId).compose(new BaseResultTransformer<BaseResult<A2BindInfoBean>, A2BindInfoBean>() {
        });
    }


    /**
     * ap??????
     *
     * @return
     */
    public Observable<Boolean> Apnetwork(String deviceId, long cuId, String setupToken) {
        return getApiService().ApNetwork(deviceId, cuId, setupToken).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }


    /**
     * ??????????????????
     *
     * @param pageNum ?????? ???1??????
     * @param maxNum  ???????????????
     * @param tradeId
     * @return
     */
    public Observable<WorkOrderBean> getHistoryData(int pageNum, int maxNum, String tradeId, String processStatus) {
        return getApiService().getWorkOrders(pageNum, maxNum, tradeId, processStatus)
                .compose(new BaseResultTransformer<BaseResult<WorkOrderBean>, WorkOrderBean>() {
                });
    }

    /**
     * ???????????????????????????
     *
     * @param pageNum ?????? ???1??????
     * @param maxNum  ???????????????
     * @param tradeId
     * @return
     */
    public Observable<WorkOrderBean> getWorkOrderList(int pageNum, int maxNum, String tradeId, String processStatus) {
        return getApiService().getWorkOrders(pageNum, maxNum, tradeId, processStatus)
                .compose(new BaseResultTransformer<BaseResult<WorkOrderBean>, WorkOrderBean>() {
                });
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    public Observable<RoomTypeShowBean> showRoomType(long roomId) {
        return getApiService().showRoomType(roomId)
                .compose(new BaseResultTransformer<BaseResult<RoomTypeShowBean>, RoomTypeShowBean>() {
                });
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public Observable<MoveWallBean> getMoveWallData(String billId, Long roomId) {
        return getApiService().getMoveWallData(billId, roomId)
                .compose(new BaseResultTransformer<BaseResult<MoveWallBean>, MoveWallBean>() {
                });
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    public Observable<BaseResult> getRelationXiaodu(long roomId, String roomName, String hotelId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("roomId", roomId);
        jsonObject.addProperty("roomName", roomName);
        jsonObject.addProperty("hotelId", hotelId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().getRelationXiaodu(body111);

    }

    /**
     * ???????????????
     * @return
     */

  /*  public Observable<BaseResult> getDeviceMarShall() {
        return getApiService().getDeviceMarShallApi();
    }*/

    /**
     * ??????????????????pids
     *
     * @return
     */
    public Observable<List<String>> getDeviceMarShallPidData() {
        return getApiService().getDeviceMarShallPidData().compose(new BaseResultTransformer<BaseResult<List<String>>, List<String>>() {
        });
    }

    /**
     * ??????????????????????????????id
     *
     * @return
     */

    public Observable<List<MarshallEntryBean>> getGatewayDeviceId(String gatewayDeviceId, Long ScopeId) {
        return getApiService().getGatewayDeviceId(gatewayDeviceId, ScopeId).compose(new BaseResultTransformer<BaseResult<List<MarshallEntryBean>>, List<MarshallEntryBean>>() {
        });
    }

    /**
     * ????????????
     */
    public Observable<BaseResult> createGroup(GroupRequestBean groupRequestBean) {
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), GsonUtils.toJson(groupRequestBean));
        return getApiService().createGroup(body111);
    }

    public Observable<List<DeviceGroupData>> getDeviceGroupData(long scopeId, Long maxDeviceId, Long maxGroupId, Long regionId) {
        Log.d("RequestModel", "getDeviceGroupData: " + "??????????????????" + maxDeviceId + "--" + maxGroupId + "--" + regionId);
        return getApiService().getDeviceGroupData(20, scopeId, maxDeviceId, maxGroupId, regionId).compose(new BaseResultTransformer<BaseResult<List<DeviceGroupData>>, List<DeviceGroupData>>() {
        });
    }

    /**
     * ?????????????????????
     */
    public Observable<GroupDetailBean> checkMarshallSub(String groupIsd) {
        return getApiService().CheckSubSetDevice(groupIsd).compose(new BaseResultTransformer<BaseResult<GroupDetailBean>, GroupDetailBean>() {
        });
    }

    public Observable<GroupDetail> getGroupDetail(String groupId) {
        return getApiService().getGroupDetail(groupId).compose(new BaseResultTransformer<BaseResult<GroupDetail>, GroupDetail>() {
        });
    }

    public Observable<Boolean> deleteGroup(String groupId) {
        return getApiService().deleteGroup(groupId).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<Boolean> updateGroup(String json) {
        RequestBody groupBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().updateGroup(groupBody).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });

    }

    /**
     * ???????????????????????????????????????????????????
     * type 1 ?????? 2 ??????
     *
     * @return
     */
    public Observable<ConditionOrActionData> getConditionOrActionDeviceAndGroup(long roomId, int type) {
        JsonObject body = new JsonObject();
        body.addProperty("resourceId", roomId);
        body.addProperty("deviceType", type);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().getComboDeviceActionOrCondition(body111).compose(new BaseResultTransformer<BaseResult<ConditionOrActionData>, ConditionOrActionData>() {
        });
    }

    public Observable<List<GroupItem>> getAllGroup(long roomId) {
        return getApiService().getAllGroup(roomId).compose(new BaseResultTransformer<BaseResult<List<GroupItem>>, List<GroupItem>>() {
        });
    }

    public Observable<List<NewGroupAbility>> getGroupAbility(String groupId) {
        return getApiService().getGroupAbility(groupId).compose(new BaseResultTransformer<BaseResult<List<NewGroupAbility>>, List<NewGroupAbility>>() {
        });
    }

    /**
     * ??????????????????
     *
     * @return
     */
    public Observable<Boolean> getConnectResult(String deviceId, long cuId, String setupToken) {
        return getApiService().ApNetwork(deviceId, cuId, setupToken).compose(new BaseResultTransformer<BaseResult<Boolean>, Boolean>() {
        });
    }

    public Observable<DeviceListBean.DevicesBean> bindDevice(int cuId, long scopeId, String pid, String deviceId, String name, String dc, String waitBindDeviceId, String replaceDeviceId) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceId", deviceId);
        body.addProperty("scopeId", scopeId);
        body.addProperty("cuId", cuId);
        body.addProperty("scopeType", 2);
        body.addProperty("deviceCategory", dc);
        body.addProperty("pid", pid);
        body.addProperty("nickName", name);
        body.addProperty("waitBindDeviceId", waitBindDeviceId);
        body.addProperty("replaceDeviceId", replaceDeviceId);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());

        Observable<BaseResult<DeviceListBean.DevicesBean>> observable;
        if (TextUtils.isEmpty(replaceDeviceId)) {
            observable = getApiService().bindDeviceWithDSN(body111);
        } else {
            observable = getApiService().bindReplaceDeviceWithDSN(body111);
        }
        return observable.compose(new BaseResultTransformer<BaseResult<DeviceListBean.DevicesBean>, DeviceListBean.DevicesBean>() {
        }).doOnNext(devicesBean -> devicesBean.setNickname(name));
    }

    /*
     * A6?????????????????????
     */
    public Observable<BaseResult> transferRoomToZj(String deviceId, long scopeId, long businessId, String mRoom_name, String regToken, String tempToken) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceId", deviceId);
        body.addProperty("homeId", scopeId);
        body.addProperty("businessId", businessId);
        body.addProperty("homeName", mRoom_name);
        body.addProperty("regToken", regToken);
        body.addProperty("tempToken", tempToken);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().transferRoomToZj(body111);
    }


    /*
     * A6????????????
     */
    public Observable<BaseResult> transferUpdateRoomToZj(long scopeId, long businessId, String mRoom_name) {
        JsonObject body = new JsonObject();
        body.addProperty("homeId", scopeId);
        body.addProperty("businessId", businessId);
        body.addProperty("homeName", mRoom_name);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().transferUpdateRoomToZj(body111);
    }

    /*
     * ????????????
     */
    public Observable<BaseResult> CompletetransferUpdateRoomToZj(long scopeId, int billId) {
        return getApiService().CompletetransferUpdateRoomToZj(scopeId, billId);
    }


    public Observable<BaseResult<FirmwareUpdateData>> getDeviceFirmwareVersion(String dsn, String pid) {
        return getApiService().getDeviceFirmwareVersion(dsn, pid);
    }

    public Observable<BaseResult<String>> updateFirmwareVersion(String deviceJobId, String dsn) {
        JsonObject body = new JsonObject();
        body.addProperty("deviceJobId", deviceJobId);
        body.addProperty("dsn", dsn);
        body.addProperty("scheduleTimestamp", 0);
        RequestBody bodyParam = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().updateFirmware(bodyParam);
    }

    public Observable<BaseResult<String>> retryFirmwareVersion(String jobId, String dsn) {
        JsonObject body = new JsonObject();
        body.addProperty("jobId", jobId);
        body.addProperty("dsn", dsn);
        RequestBody bodyParam = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), body.toString());
        return getApiService().retryUpdateFirmware(bodyParam);
    }

    public Observable<BaseResult<String>> getFirmwareUpdateStatus(int jobId, String dsn) {
        return getApiService().getFirmwareUpdateStatus(dsn, jobId);
    }

    public Observable<DeviceListBean.DevicesBean> getDeviceDetail(String deviceId) {
        return getApiService().getDeviceDetail(deviceId).compose(new BaseResultTransformer<BaseResult<DeviceListBean.DevicesBean>, DeviceListBean.DevicesBean>() {
        });
    }

    public Observable<BaseResult<String>> saveDeviceDisplay(ShowWay showWay, String deviceId, long scopeId) {
        return getApiService().saveDeviceDisplay(showWay.getType(), deviceId, scopeId);
    }

    public Observable<Integer> getDeviceDisplay(long scopeId, String deviceId) {
        return getApiService().getDeviceDisplay(scopeId, deviceId).compose(new BaseResultTransformer<BaseResult<Integer>, Integer>() {
        });
    }

    public Observable<List<DeviceGroupData>> getSupportCombineGroupAndDevice(String gatewayId, String productLabel, String scopeId) {
        return getApiService().getSupportCombineGroupAndDevice(gatewayId, productLabel, scopeId).compose(new BaseResultTransformer<BaseResult<List<DeviceGroupData>>, List<DeviceGroupData>>() {
        });
    }

    public Observable<Map<String, GroupItem>> getBindGroup(String deviceId) {
        return getApiService().getBindGroup(deviceId).compose(new BaseResultTransformer<BaseResult<Map<String, GroupItem>>, Map<String, GroupItem>>() {
        });
    }

    public Observable<String> getBindDevice(String deviceId) {
        return getApiService().getBindDevice(deviceId).compose(new BaseResultTransformer<BaseResult<String>, String>() {
        });
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> updateSwitchProperty(String deviceId, String currentIndex, int switchMode) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("propertyCode", String.format(Locale.CHINA, "%s%s", currentIndex, ":0x0006:WorkMode"));
        jsonObject.addProperty("propertyValue", switchMode);
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().updateProperty(deviceId, body111);
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    public Observable<BaseResult<Boolean>> batchUpdateSwitchProperty(String json) {
        RequestBody body111 = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().batchUpdateProperty(body111);
    }

    public Observable<DeviceTemplateBean> getDeviceTemplate(String pid) {
        return getApiService().fetchDeviceTemplate(pid)
                .doOnNext(deviceTemplateBeanBaseResult -> {
                    DeviceTemplateBean deviceTemplateBean = deviceTemplateBeanBaseResult.data;
                    List<DeviceTemplateBean.AttributesBean> extendAttributes = deviceTemplateBean.getExtendAttributes();
                    List<DeviceTemplateBean.AttributesBean> attributes = deviceTemplateBean.getAttributes();
                    if (attributes != null) {
                        //?????????????????????????????????????????????????????? ??????????????????
                        if ("ZBSCN-A000004".equals(pid)) {//????????????

                            DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                            attributesBean.setCode("KeyValueNotification.KeyValue");
                            attributesBean.setDisplayName("????????????");
                            attributesBean.setDataType(1);
                            attributes.add(attributesBean);
                            {
                                List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("1");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("????????????");
                                    valueBean.setValue("2");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("????????????");
                                    valueBean.setValue("3");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("4");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("5");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("6");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("?????????");
                                    valueBean.setValue("7");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("?????????");
                                    valueBean.setValue("8");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("9");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("10");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("11");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("12");
                                    valueBeans.add(valueBean);
                                }
                                attributesBean.setValue(valueBeans);
                            }
                        }

                        if ("ZBSCN-A000007".equals(pid)) {//??????????????????
                            DeviceTemplateBean.AttributesBean attributesBean = new DeviceTemplateBean.AttributesBean();
                            attributesBean.setCode("KeyValueNotification.KeyValue");
                            attributesBean.setDisplayName("????????????");
                            attributesBean.setDataType(1);
                            attributes.add(attributesBean);
                            {
                                List<DeviceTemplateBean.AttributesBean.ValueBean> valueBeans = new ArrayList<>();
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("1");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("2");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("3");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("4");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("5");
                                    valueBeans.add(valueBean);
                                }
                                {
                                    DeviceTemplateBean.AttributesBean.ValueBean valueBean = new DeviceTemplateBean.AttributesBean.ValueBean();
                                    valueBean.setDataType(1);
                                    valueBean.setDisplayName("??????");
                                    valueBean.setValue("6");
                                    valueBeans.add(valueBean);
                                }
                                attributesBean.setValue(valueBeans);
                            }
                        }

                        deviceTemplateBean.setAttributes(attributes);
                    } else {
                        deviceTemplateBean.setAttributes(extendAttributes);
                    }
                }).compose(new BaseResultTransformer<BaseResult<DeviceTemplateBean>, DeviceTemplateBean>() {
                });//??????????????????????????????????????????????????????????????????????????????extendAttributes ???????????????????????????????????????attributes ??????
    }

    public Observable<BaseResult> saveUseDevice(String json) {
        RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), json);
        return getApiService().purposeBatchSave(requestBody);
    }

    public Observable<List<PurposeComboBean>> getPurposeComboList(String deviceId) {
        return getApiService().getPurposeComboList(deviceId).compose(new BaseResultTransformer<BaseResult<List<PurposeComboBean>>, List<PurposeComboBean>>() {
        });
    }

    public Observable<BaseResult<String>> unBindPurposeDevice(String deviceId, long scopeId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("deviceId", deviceId);
        jsonObject.addProperty("scopeId", scopeId);
        jsonObject.addProperty("scopeType", 2);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=UTF-8"), jsonObject.toString());
        return getApiService().unBindPurposeDevice(body);
    }

    public Observable<BaseResult<String>> deviceIsExitNet(String deviceId) {
        return getApiService().deviceIsExitNet(deviceId);
    }
}
