package com.ayla.hotelsaas.api;

import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.ConditionOrActionData;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.DeviceCategoryDetailBean;
import com.ayla.hotelsaas.bean.DeviceFirmwareVersionBean;
import com.ayla.hotelsaas.bean.DeviceGroupData;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.bean.FirmwareUpdateData;
import com.ayla.hotelsaas.bean.GatewayNodeBean;
import com.ayla.hotelsaas.bean.NewGroupAbility;
import com.ayla.hotelsaas.bean.GroupDetail;
import com.ayla.hotelsaas.bean.GroupDetailBean;
import com.ayla.hotelsaas.bean.GroupItem;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.bean.MarshallEntryBean;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.PropertyDataPointBean;
import com.ayla.hotelsaas.bean.PropertyNicknameBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.bean.PurposeComboBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.bean.RoomTypeShowBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.bean.User;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.bean.WorkOrderBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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

    @POST("api/v1/build/user/login")
    Observable<BaseResult<User>> login(@Body RequestBody body);

    @GET("api/v2/sso/{resourceId}")
    Observable<BaseResult<String>> authCode(@Path("resourceId") String scopeId);

    @GET("api/v2/sso/2/{resourceId}")
    Observable<BaseResult<String>> authCodetwo(@Path("resourceId") String scopeId);


    @GET("api/v1/build/hotelcontent/approom")
    Observable<BaseResult<RoomManageBean>> getcreateRoom(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    @POST("api/v1/build/hotelcontent/approom")
    Observable<BaseResult<String>> createRoom(@Body RequestBody body);

    @POST("api/v1/build/device/bingingRemove")
    Observable<BaseResult<String>> removeDeviceAllReleate(@Body RequestBody body);//移除鸿雁解绑关系接口

    @PUT("api/v1/build/user/register")
    Observable<BaseResult<Boolean>> register(@Body RequestBody body);

    @POST("api/v1/build/appuser/sendcode")
    Observable<BaseResult<Boolean>> sendSmsCode(@Body RequestBody body);

    @POST("api/v1/build/appuser/verificationcode")
    Observable<BaseResult<Boolean>> modifyForgitPassword(@Body RequestBody body);

    @POST("api/v1/build/appuser/newpassword")
    Observable<BaseResult<Boolean>> modifyOldPassword(@Body RequestBody body);

    @GET("api/v1/build/user/mybaseinfo")
    Observable<BaseResult<PersonCenter>> getUserInfo();

    @GET("api/v3/build/device/{deviceId}/{scopeId}/nodes")
    Observable<BaseResult<List<GatewayNodeBean>>> getGatewayNodes(@Path("deviceId") String deviceId, @Path("scopeId") long scopeId);

    @POST("api/v2/sso/refresh")
    Observable<BaseResult<User>> refreshToken(@Body RequestBody body);

    @PUT("api/v1/build/hotelcontent/approom/{id}")
    Observable<BaseResult<String>> roomRename(@Path("id") long roomId, @Body RequestBody body);

    @DELETE("api/v1/build/hotelcontent/approom/{id}")
    Observable<BaseResult<String>> deleteRoomNum(@Path("id") long roomId);

    @GET("/api/v3/build/spark/devicetypes/product/")
    Observable<BaseResult<List<DeviceCategoryBean>>> fetchDeviceCategory();

    @GET("/api/v3/build/spark/devicetypes/product/{pid}")
        //A2新添加的一个接口，
    Observable<BaseResult<DeviceCategoryBean.SubBean.NodeBean>> getDevicePid(@Path("pid") String pid);

    @POST("/api/v3/build/device/getDeviceActionOrCondition")
    Observable<BaseResult<List<DeviceCategoryDetailBean>>> fetchDeviceCategoryDetail(@Body RequestBody body);

    @POST("/api/v3/build/device/getDeviceActionOrCondition/{pid}")
    Observable<BaseResult<DeviceCategoryDetailBean>> fetchDeviceCategoryDetail(@Path("pid") String pid);

    /**
     * 创建项目单
     *
     * @return
     */
    @POST("/api/v1/build/constructbill")
    Observable<BaseResult<Object>> createWorkOrder(@Body RequestBody body);

    @GET("api/v1/build/billrooms")
    Observable<BaseResult<RoomOrderBean>> getRoomOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("billId") String billId);

    @POST("/api/v3/build/device/list")
        //获取所有设备列表
    Observable<BaseResult<DeviceListBean>> getDeviceList(@Body RequestBody body);

    @POST("api/v1/build/device/bind")
    Observable<BaseResult<DeviceListBean.DevicesBean>> bindDeviceWithDSN(@Body RequestBody body);

    @POST("api/v1/build/device/bind/replace")
    Observable<BaseResult<DeviceListBean.DevicesBean>> bindReplaceDeviceWithDSN(@Body RequestBody body);

    //获取候选节点
    @GET("api/v1/build/device/{deviceId}/candidates/{deviceCategory}")
    Observable<BaseResult<List<DeviceListBean.DevicesBean>>> fetchCandidateNodes(@Path("deviceId") String deviceId, @Path("deviceCategory") String deviceCategory);

    @GET("api/v1/build/scene/list/{scopeId}")
    Observable<BaseResult<List<RuleEngineBean>>> fetchRuleEngines(@Path("scopeId") long scopeId);

    @POST("api/v1/build/scene/save")
    Observable<BaseResult<Boolean>> saveRuleEngine(@Body RequestBody body);

    @POST("api/v1/build/scene/update")
    Observable<BaseResult<Boolean>> updateRuleEngine(@Body RequestBody body);

    @POST("api/v1/build/scene/delete")
    Observable<BaseResult<Boolean>> deleteRuleEngine(@Body RequestBody body);

    @POST("api/v1/build/scene/excute")
    Observable<BaseResult<Boolean>> runRuleEngine(@Body RequestBody body);

    @PUT("api/v1/build/device/{deviceId}/property")
    Observable<BaseResult<Boolean>> updateProperty(@Path("deviceId") String deviceId, @Body RequestBody body);

    /**
     * 参数换成PID
     *
     * @param pid
     * @return
     */
    @GET("api/v3/build/spark/devicetypes/{pid}/modelTemplate")
    Observable<BaseResult<DeviceTemplateBean>> fetchDeviceTemplate(@Path("pid") String pid);

    @GET("api/v1/build/device/{deviceId}")
    Observable<BaseResult<DeviceFirmwareVersionBean>> fetchDeviceDetail(@Path("deviceId") String deviceId);

    @PUT("api/v1/build/device/{deviceId}/info")
    Observable<BaseResult<Boolean>> deviceRename(@Path("deviceId") String deviceId, @Body RequestBody body);

    @POST("api/v1/build/device/unregister")
    Observable<BaseResult<Boolean>> removeDevice(@Body RequestBody body);

    @POST("/api/v1/build/device/deviceProperties")
    Observable<BaseResult<Boolean>> updatePropertyNickName(@Body RequestBody body);

    @GET("/api/v1/build/device/deviceProperties/{cuId}/{deviceId}")
    Observable<BaseResult<List<PropertyNicknameBean>>> fetchPropertyNickname(@Path("cuId") int oemModel, @Path("deviceId") String ss);

    /**
     * 房间分配，获取酒店列表
     *
     * @return
     */
    @GET("/api/v1/build/device/transfer/hotellist")
    Observable<BaseResult<HotelListBean>> fetchTransferHotelList(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize);

    /**
     * 房间分配，获取房间树形结构列表 ,返回 层+房间 的层级关系
     *
     * @return
     */
    @GET("/api/v1/build/device/transfer/treelist")
    Observable<BaseResult<List<TreeListBean>>> fetchTransferTreeList(@Query("billId") String billId, @Query("hotelId") String hotelId);

    /**
     * 房间分配，获取房间列表 ,返回酒店下面的所有房间
     *
     * @return
     */
    @GET("/api/v1/build/device/transfer/roomlist")
    Observable<BaseResult<TransferRoomListBean>> fetchTransferRoomList(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("hotelId") String hotelId);

    /**
     * 自定义房间到酒店房间的 设备转移
     *
     * @return
     */
    @POST("/api/v1/build/device/transfer/room")
    Observable<BaseResult> transferToRoom(@Body RequestBody body);

    /**
     * 自定义房间到酒店的 房间转移
     *
     * @return
     */
    @POST("/api/v1/build/device/transfer/hotel")
    Observable<BaseResult> transferToHotel(@Body RequestBody body);

    /**
     * 自定义房间到酒店楼层的 房间转移
     *
     * @return
     */
    @POST("/api/v1/build/device/transfer/struct")
    Observable<BaseResult> transferToStruct(@Body RequestBody body);

    /**
     * 获取品类的配网引导信息
     *
     * @return
     */
    @GET("/api/v1/build/spark/devicetypes/{pid}/networkGuide")
    Observable<BaseResult<List<NetworkConfigGuideBean>>> getNetworkConfigGuide(@Path("pid") String pid);

    @GET("/api/v1/build/version")
    Observable<BaseResult<VersionUpgradeBean>> getAppVersion(@Query("platform") int platform, @Query("versionCode") int versionCode);

    /**
     * 校验是房间是否绑定音响
     *
     * @param scopeId
     * @return
     */
    @GET("/api/v1/build/scene/radio/{scopeId}")
    Observable<BaseResult<Boolean>> checkRadioExists(@Path("scopeId") long scopeId);

    /**
     * 校验房间是否设置了欢迎语
     *
     * @return
     */
    @POST("/api/v1/build/scene/voiceRule")
    Observable<BaseResult<Boolean>> checkVoiceRule(@Body RequestBody body);

    @GET("/api/v1/build/device/purpose/spark/list")
    Observable<BaseResult<List<PurposeCategoryBean>>> getPurposeCategory();

    /**
     * 批量保存用途设备
     *
     * @param body
     * @return
     */
    @POST("/api/v1/build/device/purpose/batch")
    Observable<BaseResult> purposeBatchSave(@Body RequestBody body);

    /**
     * 更新用途设备
     *
     * @param body
     * @return
     */
    @PUT("/api/v1/build/device/purpose")
    Observable<BaseResult> updatePurpose(@Body RequestBody body);

    /**
     * 房间是否应用过方案
     *
     * @return
     */
    @GET("/api/v1/build/batch/check/{roomId}")
    Observable<BaseResult<Boolean>> roomPlanCheck(@Path("roomId") long roomId);

    /**
     * 房间方案导出
     *
     * @return
     */
    @GET("/api/v2/build/batch/export/{roomId}")
    Observable<BaseResult<String>> roomExport(@Path("roomId") long roomId);

    /**
     * 房间方案导出
     *
     * @return
     */
    @POST("/api/v1/build/batch/import")
    Observable<BaseResult<Object>> roomPlanImport(@Body RequestBody body);

    /**
     * 房间方案重置
     *
     * @return
     */
    @POST("/api/v1/build/device/remove/{roomId}")
    Observable<BaseResult<Object>> resetRoomPlan(@Path("roomId") long roomId);

    /**
     * 获取节点设备所属网关的dsn
     *
     * @return
     */
    @GET("/api/v1/build/device/{deviceId}/gateway")
    Observable<BaseResult<String>> getNodeGateway(@Path("deviceId") String deviceId);

    /**
     * 获取属性当前的dataPoint
     *
     * @return
     */
    @GET("/api/v1/build/device/{deviceId}/property/{propertyName}")
    Observable<BaseResult<PropertyDataPointBean>> getPropertyDataPoint(@Path("deviceId") String deviceId, @Path("propertyName") String propertyName);

    @POST("/api/v1/build/scene/getRuleListByUniqListFunction")
    Observable<BaseResult<List<RuleEngineBean>>> getRuleListByUniqListFunction(@Body RequestBody body);

    @GET("/api/v1/build/billrooms/region/{roomId}")
//获取设备位置
    Observable<BaseResult<List<DeviceLocationBean>>> getAllDeviceLocation(@Path("roomId") long roomId);


    @GET("/api/v1/build/device/getDeviceBindStatus/{deviceId}")
    Observable<BaseResult<A2BindInfoBean>> getA2BindInfo(@Path("deviceId") String pid);

    @GET("/api/v1/build/device/{deviceId}/connected")
    Observable<BaseResult<Boolean>> ApNetwork(@Path("deviceId") String deviceId, @Query("cuId") long cuId, @Query("setupToken") String setupToken);


    @GET("api/v1/build/constructbill")
    Observable<BaseResult<WorkOrderBean>> getWorkOrders(@Query("pageNo") int pageNO, @Query("pageSize") int pageSize, @Query("tradeId") String tradeId, @Query("processStatus") String processStatus);

    @GET("api/v1/build/billrooms/roomtype/mapping/{scopeId}")
    Observable<BaseResult<RoomTypeShowBean>> showRoomType(@Path("scopeId") long scopeId);

    @GET("/api/v2/build/constructbill/{billId}/{scopeId}")
    Observable<BaseResult<MoveWallBean>> getMoveWallData(@Path("billId") String billId, @Path("scopeId") long scopeId);

    @POST("api/v1/build/content/linkroom")
    Observable<BaseResult> getRelationXiaodu(@Body RequestBody body);

    /**
     * 编组白名单
     */
    @POST("/api/v1/build/mpg/group")
    Observable<BaseResult> getDeviceMarShallApi();

    /**
     * 编组白名单
     */
    @GET("/api/v1/build/group/device/pids")
    Observable<BaseResult<List<String>>> getDeviceMarShallPidData();

    /**
     * 编组列表
     */
    @GET("/api/v1/build/group/{gatewayDeviceId}/{scopeId}/devices")
    Observable<BaseResult<List<MarshallEntryBean>>> getGatewayDeviceId(@Path("gatewayDeviceId") String gatewayDeviceId, @Path("scopeId") Long scopeId);

    /**
     * 创建编组
     */
    @POST("/api/v1/build/group/")
    Observable<BaseResult> createGroup(@Body RequestBody body);

    /**
     * 获取设备和编组的列表
     */
    @GET("api/v2/build/group/combo/page")
    Observable<BaseResult<List<DeviceGroupData>>> getDeviceGroupData(@Query("pageSize") int pageSize, @Query("scopeId") long scopeId, @Query("maxDevicePrimaryId") Long maxDeviceId, @Query("maxGroupPrimaryId") Long maxGroupId, @Query("regionId") Long regionId);

    /**
     * 查看子设备详情
     */
    @GET("/api/v1/build/group/{groupId}")
    Observable<BaseResult<GroupDetailBean>> CheckSubSetDevice(@Path("groupId") String groupId);

    /**
     * 获取编组详情
     *
     * @param groupId
     * @return
     */
    @GET(" /api/v1/build/group/{groupId}")
    Observable<BaseResult<GroupDetail>> getGroupDetail(@Path("groupId") String groupId);

    @DELETE("/api/v1/build/group/{groupId}")
    Observable<BaseResult<Boolean>> deleteGroup(@Path("groupId") String groupId);

    @PUT("/api/v1/build/group/")
    Observable<BaseResult<Boolean>> updateGroup(@Body RequestBody requestBody);

    /**
     * 获取可作为条件或动作的设备和编组
     *
     * @param body
     * @return
     */
    @POST("/api/v4/build/deviceCombo/getDeviceActionOrCondition")
    Observable<BaseResult<ConditionOrActionData>> getComboDeviceActionOrCondition(@Body RequestBody body);

    /**
     * 获取所有编组
     *
     * @return
     */
    @GET("/api/v4/build/deviceCombo/group/all")
    Observable<BaseResult<List<GroupItem>>> getAllGroup(@Query("resourceId") long id);

    /**
     * 获取编组能力
     */
    @GET("/api/v1/build/group/ability/{groupId}")
    Observable<BaseResult<List<NewGroupAbility>>> getGroupAbility(@Path("groupId") String groupId);


    /**
     * 移交房间到智家
     */
    @POST("/api/v1/build/a6/transfer")
    Observable<BaseResult> transferRoomToZj(@Body RequestBody requestBody);


    /**
     * 更新A6设备
     */
    @PUT("/api/v1/build/a6/transfer/update")
    Observable<BaseResult> transferUpdateRoomToZj(@Body RequestBody requestBody);

    /**
     * 完成施工单移交
     */
    @POST("/api/v1/build/a6/transfer/complete/{billId}/{scopeId}")
    Observable<BaseResult> CompletetransferUpdateRoomToZj(@Path("scopeId") long scopeId, @Path("billId") long billId);


    /**
     * 获取固件版本
     */
    @GET("/api/v1/build/device/job")
    Observable<BaseResult<FirmwareUpdateData>> getDeviceFirmwareVersion(@Query("dsn") String dsn, @Query("oemModel") String oemModel);

    /**
     * 升级
     */
    @POST("/api/v1/build/device/job/upgrade")
    Observable<BaseResult<String>> updateFirmware(@Body RequestBody requestBody);

    /**
     * 重试升级
     */
    @POST("/api/v1/build/device/job/retry")
    Observable<BaseResult<String>> retryUpdateFirmware(@Body RequestBody requestBody);

    /**
     * 获取升级状态
     */
    @GET("/api/v1/build/device/job/status")
    Observable<BaseResult<String>> getFirmwareUpdateStatus(@Query("dsn") String dsn, @Query("jobId") int jobId);


    @GET("/api/v1/build/device/{deviceId}")
    Observable<BaseResult<DeviceListBean.DevicesBean>> getDeviceDetail(@Path("deviceId") String deviceId);

    @POST("/api/v1/build/device/display")
    Observable<BaseResult<String>> saveDeviceDisplay(@Query("displayType") int type, @Query("dsn") String dsn, @Query("scopeId") long scopeId);

    @GET("/api/v1/build/device/display/{scopeId}/{deviceId}")
    Observable<BaseResult<Integer>> getDeviceDisplay(@Path("scopeId") long scopeId, @Path("deviceId") String deviceId);

    @GET("/api/v3/build/group/supportedCombo")
    Observable<BaseResult<List<DeviceGroupData>>> getSupportCombineGroupAndDevice(@Query("gatewayDeviceId") String gatewayId, @Query("productLabels") String productLabel, @Query("scopeId") String scopeId);

    @GET("/api/v1/build/group/dsn/{deviceId}")
    Observable<BaseResult<Map<String, GroupItem>>> getBindGroup(@Path("deviceId") String deviceId);

    @GET("/api/v1/build/device/purpose/{deviceId}")
    Observable<BaseResult<String>> getBindDevice(@Path("deviceId") String deviceId);

    @GET("/api/v1/build/group/getSubDeviceComboInfo/{deviceId}")
    Observable<BaseResult<List<PurposeComboBean>>> getPurposeComboList(@Path("deviceId") String deviceId);

    @POST("/api/v1/build/device/unbindPurpose")
    Observable<BaseResult<String>> unBindPurposeDevice(@Body RequestBody body);

    @POST("/api/v1/build/device/batch/deviceProperties")
    Observable<BaseResult<Boolean>> batchUpdateProperty(@Body RequestBody body);

    @GET("/api/v1/build/device/{deviceId}/platform")
    Observable<BaseResult<String>> deviceIsExitNet(@Path("deviceId") String deviceId);

}