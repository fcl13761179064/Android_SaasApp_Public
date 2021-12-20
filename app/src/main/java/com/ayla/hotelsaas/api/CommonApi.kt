package com.ayla.hotelsaas.api


import com.ayla.hotelsaas.bean.RoomBean
import com.ayla.hotelsaas.protocol.BaseResp
import com.ayla.hotelsaas.protocol.MultiBindResp
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable


/*
    获取数据接口
 */
interface CommonApi {

    /**
     * 批量绑定设备
     */
    @POST("/api/v1/build/device/bind/batch")
    fun multiBindDevice(@Body body: RequestBody): Observable<BaseResp<MultiBindResp>>


    /**
     * 获取房间列表
     */
    @GET("/api/v1/build/device/transfer/roomlist")
    fun getExistRoom(@Path("homeId") homeId: String): Observable<BaseResp<ArrayList<RoomBean>>>

    /**
     * 批量添加设备到区域位置里
     */
    @PUT("/api/v1/build/device/info/batch")
    fun addDevicesForRoom(@Body body: RequestBody): Observable<BaseResp<Any>>

}
