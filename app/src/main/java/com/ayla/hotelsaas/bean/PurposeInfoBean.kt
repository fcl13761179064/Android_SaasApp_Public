package com.ayla.hotelsaas.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PurPoseInfoBean(val purPoseInfoList:ArrayList<PurposeItemBean> = arrayListOf(),
                           val deviceLocation:ArrayList<UseDeviceLocationBean> = arrayListOf())

data class PurposeItemBean(
    var scopeId: Long,// 源设备所属域id
    var purposeCategory: String,//用途码表中唯一标识，当作deviceCategory使用
    var purposeId: String,//propertyCode
    var purposeName: String,//用途描述
    val deviceId: String,//源设备的deviceId
    val purposeSourceDeviceType: Int = 0, //源设备类型，【0：开关，1：插座，2：红外设备】
    val scopeType: Int = 2//源设备所属域类型
)

//用途设备
data class UseDeviceLocationBean(
    val propertyName: String,
    val roomId: Long?,
    val roomName:String
)

@Parcelize
data class PurposeComboBean(
    val deviceId: String?,
    val purposeId:String?,
    val groupId: String?,
    val groupName: String?,
    val nickname: String?,
    val propertyCode: String,
    val iconUrl:String?
) : Parcelable