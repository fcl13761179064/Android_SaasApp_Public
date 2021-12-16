package com.ayla.hotelsaas.api

import com.ayla.base.data.protocol.BaseResp
import com.ayla.hotelsaas.bean.BaseResult
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean
import com.ayla.hotelsaas.bean.DeviceListBean
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
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


    /**
     * 获取各种设备的详情通过deviceId
     *
     * @param deviceId      网关dsn
     * @return
     */
    @GET("api/v1/build/device/{deviceId}")
    fun fetchDeviceDetail(@Path("deviceId") deviceId: String?):BaseResp<DeviceFirmwareVersionBean>
}