package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @描述 Retrofit 需要的
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("rest")
    Observable<String> BaseRequest(@FieldMap Map<String, String> params);

    @POST("api/v2/sso/login")
    Observable<BaseResult<User>> login(@Body RequestBody body);

    @POST("api/v2/sso/refresh")
    Observable<BaseResult<User>> refreshToken(@Body RequestBody body);

    @GET("/api/v1/construction/devicetypes")
    Observable<BaseResult<List<DeviceCategoryBean>>> fetchDeviceCategory();

    @GET("api/v1/construction/constructbill")
    Observable<BaseResult<WorkOrderBean>> getWorkOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    @GET("api/v1/construction/billrooms")
    Observable<BaseResult<RoomOrderBean>> getRoomOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("billId") String billId);

    @POST("api/v1/construction/device/list")
    Observable<BaseResult<DeviceListBean>> getDeviceList(@Body RequestBody body);

    @POST("api/v1/construction/device/bind")
    Observable<BaseResult> bindDeviceWithDSN(@Body RequestBody body);

    @POST("unbind_device")
    Observable<BaseResult<Boolean>> unbindDeviceWithDSN(@Body RequestBody body);

    @GET("api/v1/construction/device/{deviceId}/candidates")
    Observable<BaseResult<List<DeviceListBean.DevicesBean>>> fetchCandidateNodes(@Path("deviceId") String deviceId);

    @POST("notify_gateway_config_exit")
    Observable<BaseResult<Boolean>> notifyGatewayConfigExit(@Body RequestBody body);

    @GET("api/v1/construction/scene/list/{scopeId}")
    Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(@Path("scopeId") long scopeId);

    @POST("api/v1/construction/scene/save")
    Observable<BaseResult<Boolean>> saveRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/update")
    Observable<BaseResult<Boolean>> updateRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/delete")
    Observable<BaseResult<Boolean>> deleteRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/excute")
    Observable<BaseResult<Boolean>> runRuleEngine(@Body RequestBody body);

    @PUT("api/v1/construction/device/{deviceId}/property")
    Observable<BaseResult<Boolean>> updateProperty(@Path("deviceId") String deviceId, @Body RequestBody body);


    @PUT("api/v1/device/{deviceId}/info")
    Observable<BaseResult<Boolean>> deviceRename(@Path("deviceId") String deviceId, @Body RequestBody body);
}
