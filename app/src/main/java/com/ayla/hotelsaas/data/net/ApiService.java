package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.Device;
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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("device_add_category")
    Observable<BaseResult<List<DeviceCategoryBean>>> fetchDeviceCategory();


    @GET("api/v1/construction/constructbill")
    Observable<BaseResult<List<WorkOrderBean>>> getWorkOrders(@Query("pageNo") int pageNO, @Query("pageSize") String pageSize);

    @GET("api/v1/construction/billrooms")
    Observable<BaseResult<List<RoomOrderBean>>> getRoomOrders(@Query("pageNo") int pageNO, @Query("pageSize") String pageSize, @Query("billId") String billId);

    @GET("device_list")
    Observable<BaseResult<List<DeviceListBean>>> getDeviceList(@Query("pageNo") int pageNO, @Query("pageSize") String pageSize, @Query("roomId") String billId);

    @POST("bind_device")
    Observable<BaseResult<Boolean>> bindDeviceWithDSN(@Body RequestBody body);

    @POST("unbind_device")
    Observable<BaseResult<Boolean>> unbindDeviceWithDSN(@Body RequestBody body);

    @POST("notify_gateway_config_enter")
    Observable<BaseResult<Boolean>> notifyGatewayConfigEnter(@Body RequestBody body);

    @POST("notify_gateway_config_exit")
    Observable<BaseResult<Boolean>> notifyGatewayConfigExit(@Body RequestBody body);

    @GET("fetch_gateway_candidates")
    Observable<BaseResult<List<Device>>> fetchCandidateNodes(@Query("device_id") String deviceId, @Query("cuid") int cuid);

    @GET("fetch_rule_engines")
    Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(@Query("scope_id") String scopeId);

    @POST("api/v1/construction/scene/save")
    Observable<BaseResult<Boolean>> saveRuleEngine(@Body RequestBody body);

    @PUT("update_rule_engine")
    Observable<BaseResult<Boolean>> updateRuleEngine(@Body RequestBody body);

    @POST("run_rule_engine")
    Observable<BaseResult<Boolean>> runRuleEngine(@Body RequestBody body);

    @HTTP(method = "DELETE",path = "delete_rule_engine",hasBody = true)
    Observable<BaseResult<Boolean>> deleteRuleEngine(@Body RequestBody body);
}
