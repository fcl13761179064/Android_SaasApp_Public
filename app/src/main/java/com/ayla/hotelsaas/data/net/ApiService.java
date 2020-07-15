package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.User;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
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

    @GET("login")
    Observable<BaseResult<User>> login(@Query("username") String username, @Query("password") String password);
}
