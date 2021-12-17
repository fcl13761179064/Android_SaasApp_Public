package com.ayla.hotelsaas.protocol

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
    品类和绑定注入实体
 */
data class BindSuccessReq(val deviceId: String)

data class MultiBindResp(val failed:List<String>, val success:List<String>)

@Parcelize
data class MultiBindResultBean(val deviceId:String,
                               var nickName:String,
                               val roomId:String,
                               var roomName:String) : Parcelable