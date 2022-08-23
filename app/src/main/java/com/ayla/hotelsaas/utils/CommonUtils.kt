package com.ayla.hotelsaas.utils

import android.content.Context
import android.location.LocationManager
import android.text.InputFilter
import com.ayla.hotelsaas.BuildConfig
import com.ayla.hotelsaas.bean.*
import com.ayla.hotelsaas.constant.ShowWay
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.SPUtils
import java.nio.charset.Charset
import java.util.*

object CommonUtils {

    fun gpsIsOpen(context: Context): Boolean {
        val locationManager =
            context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (gps || network) {
            return true
        }
        return false
    }

    private const val RANDOM_CHARSET =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    fun generateRandomToken(length: Int): String {
        val data = ByteArray(length)
        for (i in 0 until length) {
            data[i] = RANDOM_CHARSET[(Math.random() * RANDOM_CHARSET.length).toInt()].toByte()
        }
        return String(data, Charset.forName("UTF-8"))
    }

    fun getInputLengthFilter(maxLength: Int): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            var sLen = source.toString().toByteArray(Charset.forName("GBK")).size
            val dLen = dest.toString().toByteArray(Charset.forName("GBK")).size
            var newStr = source.toString()
            while (sLen + dLen > maxLength) {
                newStr = newStr.substring(0, newStr.length - 1)
                sLen = newStr.toByteArray(Charset.forName("GBK")).size
            }
            newStr
        }
    }

    fun saveVersionUpgradeInfo(versionUpgradeBean: VersionUpgradeBean?) {
        if (versionUpgradeBean != null) {
            SPUtils.getInstance()
                .put("versionUpgradeBean", GsonUtils.toJson(versionUpgradeBean), true)
        } else {
            SPUtils.getInstance().remove("versionUpgradeBean", true)
        }
    }

    fun getVersionUpgradeInfo(): VersionUpgradeBean? {
        val s = SPUtils.getInstance().getString("versionUpgradeBean")
        return try {
            GsonUtils.fromJson(
                s,
                VersionUpgradeBean::class.java
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 获取随机字符串
     */
    //length用户要求产生字符串的长度
    fun getRandomString(length: Int): String? {
        val str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random()
        val sb = StringBuffer()
        for (i in 0 until length) {
            val number = random.nextInt(62)
            sb.append(str[number])
        }
        return sb.toString()
    }

    fun isDoubleFouCurtain(pid: String): Boolean { //前面是双路窗帘面板开关，后面是4路窗帘面板开关
        return "ZBSCN-A000010" == pid || "ZBSW0-A000021" == pid
    }

    fun isOpenLog(): Boolean {
        return "debug".equals(BuildConfig.BUILD_TYPE, ignoreCase = true)
    }

    fun convertOperatorToTxt(operator: String): String {
        var oT = "等于"
        when (operator) {
            "==" -> {
                oT = "等于"
            }
            ">" -> {
                oT = "大于"
            }
            "<" -> {
                oT = "小于"
            }
        }
        return oT
    }

    fun convertTxtToOperator(txtOperator: String): String {
        var operator = "=="
        when (txtOperator) {
            "大于" -> {
                operator = ">"
            }
            "等于" -> {
                operator = "=="
            }
            "小于" -> {
                operator = "<"
            }
        }
        return operator
    }

    fun getShowWay(type: Int): ShowWay {
        return when (type) {
            1 -> ShowWay.ONLY_MAIN_DEVICE
            2 -> ShowWay.ONLY_VIRTUAL_DEVICE
            3 -> ShowWay.ALL
            else -> ShowWay.ALL
        }
    }

    fun getShowWayName(showWay: ShowWay): String {
        return when (showWay) {
            ShowWay.ALL -> "全部展示"
            ShowWay.ONLY_MAIN_DEVICE -> "各按键统一展示"
            else -> "全部展示"
        }
    }

    fun getSwitchKeyCount(deviceBean: DeviceListBean.DevicesBean): Int {
        return if (deviceBean.deviceName != null && deviceBean.deviceName.contains("智能面板")) {
            when {
                deviceBean.deviceName.contains("一路") -> 1
                deviceBean.deviceName.contains("二路") -> 2
                deviceBean.deviceName.contains("三路") -> 3
                deviceBean.deviceName.contains("四路") -> 4
                deviceBean.deviceName.contains("五路") -> 5
                deviceBean.deviceName.contains("六路") -> 6
                else -> 1
            }
        } else 0
    }

    fun getGroupDeviceId(device: BaseDevice): String {
        return when (device) {
            is DeviceItem -> device.deviceId
            is GroupItem -> device.groupId
            else -> ""
        }
    }


}