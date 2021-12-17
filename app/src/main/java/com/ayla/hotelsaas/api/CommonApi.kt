package com.ayla.hotelsaas.api


import com.ayla.base.data.protocol.BaseResp
import com.ayla.hotelsaas.bean.RoomBean
import com.ayla.hotelsaas.common.Keys
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
    fun getExistRoom(@Path("homeId") homeId: String = Keys.RoomId): Observable<BaseResp<ArrayList<RoomBean>>>

    /**
     * 添加设备到房间
     */
    @POST("/api/v1/miya/room/addRoomDevice")
    fun addDevicesForRoom(@Body body: RequestBody): Observable<BaseResp<Any>>

}
