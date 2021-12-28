package com.ayla.hotelsaas.api


import com.ayla.hotelsaas.protocol.BaseResp
import com.ayla.hotelsaas.protocol.MultiBindResp
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.*


/*
    获取数据接口
 */
interface CommonApi {

    /**
     * 批量绑定设备
     */
    @POST("/api/v1/build/device/bind/batch")
    fun multiBindDevice(@Body body: RequestBody): Observable<BaseResp<MultiBindResp>>


}
