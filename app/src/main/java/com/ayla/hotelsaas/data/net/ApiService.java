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
    Observable<BaseResult<WorkOrderBean>> getWorkOrders(@Query("pageNo") int pageNO, @Query("pageSize") String pageSize);

    @GET("api/v1/construction/billrooms")
    Observable<BaseResult<RoomOrderBean>> getRoomOrders(@Query("pageNo") int pageNO, @Query("pageSize") String pageSize, @Query("billId") String billId);

    @POST("api/v1/construction/device/list")
    Observable<BaseResult<DeviceListBean>> getDeviceList(@Body RequestBody body);

    @POST("bind_device")
    Observable<BaseResult<Boolean>> bindDeviceWithDSN(@Body RequestBody body);

    @POST("unbind_device")
    Observable<BaseResult<Boolean>> unbindDeviceWithDSN(@Body RequestBody body);

    @POST("notify_gateway_config")
    Observable<BaseResult<Boolean>> notifyGatewayConfig(@Body RequestBody body);

    @POST("notify_gateway_config")
    Observable<BaseResult<Boolean>> fetchCandidateNodes();

    @GET("fetch_rule_engines")
    Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(@Query("scope_id") String scopeId);

    @POST("save_rule_engine")
    Observable<BaseResult<Boolean>> saveRuleEngines(@Body RequestBody body);


    @POST("run_rule_engine")
    Observable<BaseResult<Boolean>> runRuleEngines(@Body RequestBody body);
}
