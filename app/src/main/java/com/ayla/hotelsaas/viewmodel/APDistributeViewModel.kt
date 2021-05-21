package com.ayla.aylahome.viewmodel

import android.content.Context
import android.net.wifi.ScanResult
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayla.base.rx.BaseException
import com.ayla.hotelsaas.application.Constance
import com.ayla.ng.lib.bootstrap.AylaSetupDevice
import com.ayla.ng.lib.bootstrap.AylaWiFiSetup
import com.ayla.ng.lib.bootstrap.common.AylaCallback
import com.ayla.ng.lib.bootstrap.connectivity.AylaConnectivityManager
import com.blankj.utilcode.util.NetworkUtils
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * @ClassName:  APDistributeViewModel
 * @Description:
 * @Author: vi1zen
 * @CreateDate: 2021/5/13 10:30
 */
class APDistributeViewModel : ViewModel() {

    private var subscription: Subscription? = null
    var apSSid = ""
    val apConfigResult = MutableLiveData<Result<String>>()

    fun connectToApDevice(context: Context, inputDsn: String, homeWiFiSSid: String, homeWiFiPwd: String) {
        var isNeedExit = true
        val aylaWiFiSetup = try {
            AylaWiFiSetup(context, AylaConnectivityManager.from(context, false))
        } catch (e: Exception) {
            apConfigResult.value = Result.failure(e)
            return
        }
        val gatewayIp = NetworkUtils.getGatewayByWifi()
        Observable.create<String> { emitter ->
            aylaWiFiSetup.scanDevices(5, { it.SSID.matches(Constance.DEFAULT_SSID_REGEX.toRegex()) }, object : AylaCallback<Array<ScanResult>> {
                override fun onSuccess(result: Array<ScanResult>) {
                    this@APDistributeViewModel.apSSid = result.first().SSID
                    emitter.onNext(this@APDistributeViewModel.apSSid)
                    emitter.onCompleted()
                }

                override fun onFailed(throwable: Throwable) {
                    emitter.onError(BaseException(-1, "找不到指定的网关设备"))
                }
            })

        }.flatMap { apSSid ->
            Observable.create<AylaSetupDevice> { emitter ->
                aylaWiFiSetup.connectToNewDevice(apSSid, 20, object : AylaCallback<AylaSetupDevice> {
                    override fun onSuccess(result: AylaSetupDevice) {
                        result.setLanIp(gatewayIp)
                        emitter.onNext(result)
                        emitter.onCompleted()
                    }

                    override fun onFailed(throwable: Throwable) {
                        emitter.onError(throwable)
                    }
                })
            }
        }.flatMap {
            if (it.dsn != inputDsn) {
                Observable.error(BaseException(-1, "网关连接不匹配"))
            } else {
                Observable.just(it)
            }
        }.flatMap { aylaSetupDevice ->
            Observable.create<AylaSetupDevice> { emitter ->
                aylaWiFiSetup.connectDeviceToService(homeWiFiSSid, homeWiFiPwd, "fregwergweaaa", 20, object : AylaCallback<Any> {
                    override fun onSuccess(result: Any) {
                        emitter.onNext(aylaSetupDevice)
                        emitter.onCompleted()
                    }

                    override fun onFailed(throwable: Throwable) {
                        emitter.onError(throwable) //此处正常处理
                    }
                })
            }
        }.doAfterTerminate {
            if (isNeedExit) {
                isNeedExit = false
                aylaWiFiSetup.exitSetup()
            }
        }.doOnUnsubscribe {
            if (isNeedExit) {
                isNeedExit = false
                aylaWiFiSetup.exitSetup()
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    apConfigResult.value = Result.success(it.dsn)
                }, {
                    apConfigResult.value = Result.failure(it)
                })
    }


    override fun onCleared() {
        super.onCleared()
        if (subscription?.isUnsubscribed == true) {
            subscription?.unsubscribe()
        }
    }
}