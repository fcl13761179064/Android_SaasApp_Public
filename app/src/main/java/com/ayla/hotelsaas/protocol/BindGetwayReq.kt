package com.ayla.hotelsaas.protocol

/*
    品类和绑定注入实体
 */
data class BindGetwayReq(
    val deviceId: String,
    val cuId: Int,
    val scopeId: Long,
    val scopeType: Int,
    val deviceCategory: String,
    val deviceName: String,
    val nickName: String,
    val pid: String?=null
)

data class BindA6DeviceRequest (
    val bindParamMap:Map<String,Any>,
    val deviceId:String,
    val regToken:String,
    val tempToken:String
)

data class MultiBindRequest(val deviceInfoMaps:List<BindGetwayReq>)