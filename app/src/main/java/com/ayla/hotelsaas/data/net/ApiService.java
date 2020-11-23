package com.ayla.hotelsaas.data.net;

import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.ConstructionBillListBean;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.GatewayNodeBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @描述 Retrofit 需要的
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public interface ApiService {

    @POST("api/v2/sso/login")
    Observable<BaseResult<User>> login(@Header("loginSource") int loginSource, @Body RequestBody body);

    @GET("api/v2/sso/{resourceId}")
    Observable<BaseResult<String>> authCode(@Path("resourceId") String scopeId);

    @GET("api/v1/construction/hotelcontent/approom")
    Observable<BaseResult<RoomManageBean>> getcreateRoom(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    @POST("api/v1/construction/hotelcontent/approom")
    Observable<BaseResult<String>> createRoom(@Body RequestBody body);

    @POST("api/v1/construction/device/bingingRemove")
    Observable<BaseResult<String>> removeDeviceAllReleate(@Body RequestBody body);//移除鸿雁解绑关系接口

    @PUT("api/v1/construction/user/register")
    Observable<BaseResult<Boolean>> register(@Body RequestBody body);

    @POST("api/v1/construction/appuser/sendcode")
    Observable<BaseResult<Boolean>> sendSmsCode(@Body RequestBody body);

    @POST("api/v1/construction/appuser/verificationcode")
    Observable<BaseResult<Boolean>> modifyForgitPassword(@Body RequestBody body);

    @POST("api/v1/construction/appuser/newpassword")
    Observable<BaseResult<Boolean>> modifyOldPassword(@Body RequestBody body);

    @GET("api/v1/construction/user/mybaseinfo")
    Observable<BaseResult<PersonCenter>> getUserInfo();

    @GET("api/v1/construction/device/{deviceId}/{scopeId}/nodes")
    Observable<BaseResult<List<GatewayNodeBean>>> getGatewayNodes(@Path("deviceId") String deviceId, @Path("scopeId") long scopeId);

    @POST("api/v2/sso/refresh")
    Observable<BaseResult<User>> refreshToken(@Body RequestBody body);

    @PUT("api/v1/construction/hotelcontent/approom/{id}")
    Observable<BaseResult<String>> roomRename(@Path("id") long roomId, @Body RequestBody body);

    @DELETE("api/v1/construction/hotelcontent/approom/{id}")
    Observable<BaseResult<String>> deleteRoomNum(@Path("id") long roomId);

    @GET("/api/v1/construction/devicetypes")
    Observable<BaseResult<List<DeviceCategoryBean>>> fetchDeviceCategory();

    @GET("/api/v1/construction/devicetypes/list")
    Observable<BaseResult<List<DeviceCategoryDetailBean>>> fetchDeviceCategoryDetail();

    @GET("api/v1/construction/constructbill")
    Observable<BaseResult<WorkOrderBean>> getWorkOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    @GET("api/v1/construction/billrooms")
    Observable<BaseResult<RoomOrderBean>> getRoomOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("billId") String billId);

    @POST("api/v1/construction/device/list")
    Observable<BaseResult<DeviceListBean>> getDeviceList(@Body RequestBody body);

    @POST("api/v1/construction/device/bind")
    Observable<BaseResult<DeviceListBean.DevicesBean>> bindDeviceWithDSN(@Body RequestBody body);

    @POST("unbind_device")
    Observable<BaseResult<Boolean>> unbindDeviceWithDSN(@Body RequestBody body);

    @GET("api/v1/construction/device/{deviceId}/candidates/{deviceCategory}")
    Observable<BaseResult<List<DeviceListBean.DevicesBean>>> fetchCandidateNodes(@Path("deviceId") String deviceId, @Path("deviceCategory") String deviceCategory);

    @POST("notify_gateway_config_exit")
    Observable<BaseResult<Boolean>> notifyGatewayConfigExit(@Body RequestBody body);

    @GET("api/v1/construction/scene/list/{scopeId}")
    Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(@Path("scopeId") long scopeId);

    @POST("api/v1/construction/scene/save")
    Observable<BaseResult<Boolean>> saveRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/update")
    Observable<BaseResult<Boolean>> updateRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/delete")
    Observable<BaseResult<Boolean>> deleteRuleEngine(@Body RequestBody body);

    @POST("api/v1/construction/scene/excute")
    Observable<BaseResult<Boolean>> runRuleEngine(@Body RequestBody body);

    @PUT("api/v1/construction/device/{deviceId}/property")
    Observable<BaseResult<Boolean>> updateProperty(@Path("deviceId") String deviceId, @Body RequestBody body);

    @GET("api/v1/construction/device/queryModelTemplate/{oemModel}")
    Observable<BaseResult<DeviceTemplateBean>> fetchDeviceTemplate(@Path("oemModel") String oemModel);

    @GET("api/v1/construction/device/{deviceId}")
    Observable<BaseResult<DeviceFirmwareVersionBean>> fetchDeviceDetail(@Path("deviceId") String deviceId);

    @PUT("api/v1/construction/device/{deviceId}/info")
    Observable<BaseResult<Boolean>> deviceRename(@Path("deviceId") String deviceId, @Body RequestBody body);

    @POST("api/v1/construction/device/unregister")
    Observable<BaseResult<Boolean>> removeDevice(@Body RequestBody body);

    @POST("/api/v1/construction/device/deviceProperties")
    Observable<BaseResult<Boolean>> tourchPanelRenameAndIcon(@Body RequestBody body);

    @GET("/api/v1/construction/device/deviceProperties/{cuId}/{deviceId}")
    Observable<BaseResult<List<TouchPanelDataBean>>> touchpanelALlDevice(@Path("cuId") int oemModel, @Path("deviceId") String ss);

    /**
     * 房间分配，获取酒店列表
     *
     * @return
     */
    @GET("/api/v1/construction/device/transfer/hotellist")
    Observable<BaseResult<HotelListBean>> fetchTransferHotelList(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    /**
     * 房间分配，获取房间树形结构列表 ,返回 层+房间 的层级关系
     *
     * @return
     */
    @GET("/api/v1/construction/device/transfer/treelist")
    Observable<BaseResult<List<TreeListBean>>> fetchTransferTreeList(@Query("billId") String billId, @Query("hotelId") String hotelId);

    /**
     * 房间分配，获取房间列表 ,返回酒店下面的所有房间
     *
     * @return
     */
    @GET("/api/v1/construction/device/transfer/roomlist")
    Observable<BaseResult<TransferRoomListBean>> fetchTransferRoomList(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("hotelId") String hotelId);

    /**
     * 自定义房间到酒店房间的 设备转移
     *
     * @return
     */
    @POST("/api/v1/construction/device/transfer/room")
    Observable<BaseResult> transferToRoom(@Body RequestBody body);

    /**
     * 自定义房间到酒店的 房间转移
     *
     * @return
     */
    @POST("/api/v1/construction/device/transfer/hotel")
    Observable<BaseResult> transferToHotel(@Body RequestBody body);

    /**
     * 自定义房间到酒店楼层的 房间转移
     *
     * @return
     */
    @POST("/api/v1/construction/device/transfer/struct")
    Observable<BaseResult> transferToStruct(@Body RequestBody body);

    /**
     * 获取品类的配网引导信息
     *
     * @return
     */
    @GET("/api/v1/construction/devicetypes/{categoryId}/networkguide")
    Observable<BaseResult<NetworkConfigGuideBean>> getNetworkConfigGuide(@Path("categoryId") String categoryId);

    /**
     * 获取项目单列表
     *
     * @return
     */
    @GET("/api/v1/construction/constructbill")
    Observable<BaseResult<ConstructionBillListBean>> getConstructionBillList(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    /**
     * 创建项目单
     *
     * @return
     */
    @POST("/api/v1/construction/constructbill")
    Observable<BaseResult> createBill(@Body RequestBody body);
}
