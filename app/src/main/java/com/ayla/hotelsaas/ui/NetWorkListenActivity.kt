package com.ayla.hotelsaas.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

class NetWorkListenActivity : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo

        // 判断网络情况
        if (networkInfo != null && networkInfo.isAvailable) {
            // 网络可用时的执行内容
        } else {
            // 网络不可用时的执行内容
            CustomToast.makeOnlyHaseText(context, "网络异常")
        }
    }
}