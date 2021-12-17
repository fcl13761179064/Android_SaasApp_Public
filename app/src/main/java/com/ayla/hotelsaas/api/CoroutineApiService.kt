package com.ayla.hotelsaas.api

import com.ayla.base.data.protocol.BaseResp
import com.ayla.hotelsaas.bean.BaseResult
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.bean.RoomBean
import com.ayla.hotelsaas.common.Keys
import com.ayla.hotelsaas.protocol.MultiBindResp
import okhttp3.RequestBody
import retrofit2.http.*
import rx.Observable

/**
 * @ClassName:  CoroutineApiService
 * @Description:API接口-协程
 * @Author: chunlei.fan
 * @CreateDate: 2021/12/15 18:32
 */
interface CoroutineApiService {
    /**
     * 设置设备属性
     */
    @PUT("api/v1/build/device/{deviceId}/property")
    suspend fun updateProperty(
        @Path("deviceId") deviceId: String,
        @Body body: RequestBody
    ): BaseResp<Any>

    /**
     * 获取候选节点
     *
     * @param deviceId            网关dsn
     * @param deviceCategory 需要绑定节点设备的oemModel
     * @return
     */
    @GET("api/v1/build/device/{deviceId}/candidates/{deviceCategory}")
    suspend fun fetchCandidateNodes(
        @Path("deviceId") deviceId: String,
        @Path("deviceCategory") deviceCategory: String
    ): BaseResp<List<DeviceListBean.DevicesBean>>

}