package com.ayla.hotelsaas.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.AppUtils

/**
 * @ClassName:  AppUtil
 * @Description:APP工具类
 * @Author: vi1zen
 * @CreateDate: 2021/2/2 17:19
 */
object AppUtil {
    /**
     * 跳转到通知设置界面
     */
    fun jumpToPushSetting() {
        try {
            val intent = Intent()
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, AppUtils.getAppPackageName())
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, AppUtils.getAppUid())
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra("app_package", AppUtils.getAppPackageName())
                    intent.putExtra("app_uid", AppUtils.getAppUid())
                }
                else -> {
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", AppUtils.getAppPackageName(), null)
                }
            }
            ActivityUtils.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            AppUtils.launchAppDetailsSettings()
        }
    }

    /**
     * 跳转到WiFi页面
     */
    fun toConfigWiFi() {
        val intent = Intent()
        intent.action = "android.net.wifi.PICK_WIFI_NETWORK"
        ActivityUtils.startActivity(intent)
    }

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    fun isWiFI5GHz(context: Context): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val freq = wifiManager.connectionInfo.frequency
        return freq > 4900 && freq < 5900
    }

    /**
     * 阿拉伯数字转中文
     */
    fun getChineseNumberFromArabNumber(number:Int) : String {
        return if(number < 10){
            "零${formatNumber(number.toString().toCharArray().first())}"
        }else{
            val numberChars = number.toString().toCharArray()
            val sb = StringBuilder()
            numberChars.forEach { sb.append(formatNumber(it)) }
            sb.toString()
        }
    }

    /**
     * 数字转中文
     */
    private fun formatNumber(number: Char) : String {
        return when(number) {
            '0' -> "零"
            '1' -> "一"
            '2' -> "二"
            '3' -> "三"
            '4' -> "四"
            '5' -> "五"
            '6' -> "六"
            '7' -> "七"
            '8' -> "八"
            '9' -> "九"
            else -> "零"
        }
    }
}