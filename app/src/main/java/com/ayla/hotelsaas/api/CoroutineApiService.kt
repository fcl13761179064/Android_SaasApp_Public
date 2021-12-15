package com.ayla.hotelsaas.api

import com.ayla.base.data.protocol.BaseResp
import com.ayla.hotelsaas.bean.DeviceListBean
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

/**
 * @ClassName:  CoroutineApiService
 * @Description:API接口-协程
 * @Author: vi1zen
 * @CreateDate: 2021/11/24 18:32
 */
interface CoroutineApiService {
    /**
     * 设置设备属性
     */
    @PUT("api/v1/miya/device/{deviceId}/property")
    suspend fun updateProperty(@Path("deviceId") deviceId: String, @Body body: RequestBody): BaseResp<Any>

    /**
     * 获取候选节点
     *
     * @param deviceId            网关dsn
     * @param deviceCategory 需要绑定节点设备的oemModel
     * @return
     */
    @GET("api/v1/miya/device/{deviceId}/candidates/{deviceCategory}")
    suspend fun fetchCandidateNodes(@Path("deviceId") deviceId: String,
                            @Path("deviceCategory") deviceCategory: String): BaseResp<List<DeviceListBean.DevicesBean>>
}