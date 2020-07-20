package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * @描述 Retrofit 需要的
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("rest")
    Observable<String> BaseRequest(@FieldMap Map<String, String> params);

    @POST("login")
    @FormUrlEncoded
    Observable<BaseResult<User>> login(@Field("username") String username, @Field("password") String password);

    @GET("device_add_category")
    Observable<BaseResult<List<DeviceCategoryBean>>> fetchDeviceCategory();


    @POST("work_order")
    Observable<BaseResult<List<WorkOrderBean>>> getWorkOrders();

    @POST("room_order")
    Observable<BaseResult<List<RoomOrderBean>>> getRoomOrders(@Body RequestBody body);

    @POST("room_order")
    Observable<BaseResult<List<DeviceListBean>>> getDeviceList(@Body RequestBody body);

    @POST("bind_device")
    Observable<BaseResult<Boolean>> bindDeviceWithDSN(@Body RequestBody body);


    @POST("unbind_device")
    Observable<BaseResult<Boolean>> unbindDeviceWithDSN(@Body RequestBody body);

}
