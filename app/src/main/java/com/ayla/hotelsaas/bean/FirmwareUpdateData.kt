package com.ayla.hotelsaas.bean

import android.os.Parcel
import android.os.Parcelable

class FirmwareUpdateData() : Parcelable {
    var dsn = ""
    var currentVersion = ""
    var deviceJobId = ""
    var jobId = 0
    var jobName = ""
    var jobStatus = ""
    var otaType = 0
    var startedAt = 0
    var upgradeType = 0
    var version = ""
    var deviceName = ""
    var iconUrl = ""

    constructor(parcel: Parcel) : this() {
        dsn = parcel.readString() ?: ""
        currentVersion = parcel.readString() ?: ""
        deviceJobId = parcel.readString() ?: ""
        jobId = parcel.readInt()
        jobName = parcel.readString() ?: ""
        jobStatus = parcel.readString() ?: ""
        otaType = parcel.readInt()
        startedAt = parcel.readInt()
        upgradeType = parcel.readInt()
        version = parcel.readString() ?: ""
        deviceName = parcel.readString() ?: ""
        iconUrl = parcel.readString() ?: ""

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dsn)
        parcel.writeString(currentVersion)
        parcel.writeString(deviceJobId)
        parcel.writeInt(jobId)
        parcel.writeString(jobName)
        parcel.writeString(jobStatus)
        parcel.writeInt(otaType)
        parcel.writeInt(startedAt)
        parcel.writeInt(upgradeType)
        parcel.writeString(version)
        parcel.writeString(deviceName)
        parcel.writeString(iconUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FirmwareUpdateData> {
        override fun createFromParcel(parcel: Parcel): FirmwareUpdateData {
            return FirmwareUpdateData(parcel)
        }

        override fun newArray(size: Int): Array<FirmwareUpdateData?> {
            return arrayOfNulls(size)
        }
    }
}