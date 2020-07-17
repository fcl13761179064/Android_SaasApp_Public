package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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
    Observable<String> getWorkOrder();

    @POST("work_order")
    Observable<BaseResult<List<WorkOrderBean>>> getWorkOrders();

}
