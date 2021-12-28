package com.ayla.hotelsaas.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class SelectBean<T>(var isSelected:Boolean, val data:T)
@Parcelize
data class WeekdayBean(val isSelected: Boolean,val weekday:String) : Parcelable

data class MusicPlayStatusBean<T>(val data:T,var isPlay:Boolean,var progress:Int = 0)