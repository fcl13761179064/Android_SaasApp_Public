package com.ayla.hotelsaas.base

import com.ayla.hotelsaas.widget.ProgressLoading
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.*

abstract class BaseCameraActivity : BasicActivity() {

    private var mLoadingDialog: ProgressLoading? = null

    var netConnected = true
    open fun onNetworkStatus(connected: Boolean) {}

    private val netListener = object : NetworkUtils.OnNetworkStatusChangedListener {
        override fun onDisconnected() {
            netConnected = false
            onNetworkStatus(false)
        }

        override fun onConnected(networkType: NetworkUtils.NetworkType?) {
            netConnected = true
            onNetworkStatus(true)
        }
    }

    override fun onResume() {
        super.onResume()
        NetworkUtils.registerNetworkStatusChangedListener(netListener)
    }

    override fun onPause() {
        super.onPause()
        NetworkUtils.unregisterNetworkStatusChangedListener(netListener)
    }

    fun showLoading(msg: String? = null) {
        mLoadingDialog = ProgressLoading.create(this)
        mLoadingDialog?.showLoading()
    }

    fun hideLoading() {
        mLoadingDialog?.hideLoading()
        mLoadingDialog = null
    }
}