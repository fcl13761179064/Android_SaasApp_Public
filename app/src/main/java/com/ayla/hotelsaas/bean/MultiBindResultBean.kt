package com.ayla.hotelsaas.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
    品类和绑定注入实体
 */
data class BindSuccessReq(val deviceId: String)

@Parcelize
data class MultiBindResp(val failed: List<String>, val success: List<MultiPositionSite>) :
    Parcelable

@Parcelize
data class MultiPositionSite(
    val deviceId: String,
    var regionId: Long,
    val regionName: String,
    var pointName: String
) : Parcelable

@Parcelize
data class MultiBindResultBean(
    val deviceId: String,
    var nickName: String,
    val roomId: String,
    var roomName: String
) : Parcelable