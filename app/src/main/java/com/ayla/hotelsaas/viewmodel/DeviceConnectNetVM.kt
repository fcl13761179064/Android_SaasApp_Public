package com.ayla.hotelsaas.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.ayla.hotelsaas.data.net.SelfMsgException
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import com.ayla.hotelsaas.data.net.DisposableObservable
import com.ayla.hotelsaas.data.net.DisposableObservableOnSubscribe
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetup
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice
import com.ayla.ng.lib.bootstrap.AylaWifiScanResult
import com.ayla.ng.lib.bootstrap.WifiSecurityType
import com.ayla.ng.lib.bootstrap.common.AylaCallback
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class DeviceConnectNetVM : AbsViewModel() {
    private var connectTime = 60L
    private var pageState = MutableLiveData<ConfigStatusWrapper>()

    private val timer: CountDownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            connectTime = millisUntilFinished / 1000
        }

        override fun onFinish() {
            pageState.postValue(ConfigStatusWrapper(ConfigStatus.CLOUD_CHECK_FAILED))
        }
    }

    fun bindDevice(
        cuId: Int,
        scopeId: Long,
        pid: String,
        deviceId: String,
        name: String,
        dc: String,
        waitBindDeviceId: String?,
        replaceDeviceId: String?
    ): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        val subscribe =
            RequestModel.getInstance().bindDevice(
                cuId,
                scopeId,
                pid,
                deviceId,
                name,
                dc,
                waitBindDeviceId,
                replaceDeviceId
            )
                .subscribeOn(
                    Schedulers.io()
                ).observeOn(AndroidSchedulers.mainThread()).subscribe({
                    resultLD.value = Result.success(it)
                }, {
                    resultLD.value = Result.failure(it)
                })
        addDisposable(subscribe)
        return resultLD
    }

    /**
     * ????????????
     */
    enum class ConfigStatus {
        HARD_WARE_CONFIG_SUCCESS,//??????????????????
        CLOUD_CHECK_SUCCESS,//?????????????????????
        HARD_WARE_CONFIG_FAILED,//??????????????????
        CLOUD_CHECK_FAILED,//?????????????????????
        NET_ERROR
    }

    class ConfigStatusWrapper constructor(val configStatus: ConfigStatus) {
        var deviceId: String? = null // ?????? HARD_WARE_CONFIG_FAILED ???????????????????????????

        var throwable: Throwable? = null//????????????????????????
    }


    /**
     * ???????????????????????????
     */
    @SuppressLint("MissingPermission")
    fun configBleDevice(
        context: Context,
        ssid: String,
        pwd: String,
        bleName: String,
        setupToken: String,
        cuId: Long
    ): MutableLiveData<ConfigStatusWrapper> {
        pageState = MutableLiveData<ConfigStatusWrapper>()
        timer.start()
        val aylaBLEWiFiSetup = AylaBLEWiFiSetup(context)
        var deviceId: String? = null
        val disposable = DisposableObservable
            .create(object : DisposableObservableOnSubscribe<AylaBLEWiFiSetupDevice>() {
                override fun subscribe(emitter: ObservableEmitter<AylaBLEWiFiSetupDevice>) {
                    val disposable = aylaBLEWiFiSetup.scanDevices(
                        5,
                        { t -> TextUtils.equals(t.localName, bleName) },
                        object : AylaCallback<Array<AylaBLEWiFiSetupDevice>> {
                            override fun onSuccess(result: Array<AylaBLEWiFiSetupDevice>) {
                                if (!emitter.isDisposed) {
                                    if (result.isNotEmpty()) {
                                        emitter.onNext(result[0])
                                    } else {
                                        onFailed(SelfMsgException("??????????????????BLE??????:${bleName}", null))
                                    }
                                }
                            }

                            override fun onFailed(throwable: Throwable) {
                                if (!emitter.isDisposed) {
                                    emitter.onError(
                                        SelfMsgException(
                                            "??????????????????BLE??????:${bleName}", Exception(throwable)
                                        )
                                    )
                                }
                            }
                        })
                    addDisposable(disposable)
                }
            })//???????????????????????????
            .delay(1, TimeUnit.SECONDS)
            .flatMap { device ->
                DisposableObservable.create(object : DisposableObservableOnSubscribe<Any>() {
                    override fun subscribe(emitter: ObservableEmitter<Any>) {
                        val disposable =
                            aylaBLEWiFiSetup.connectToBLEDevice(device, object : AylaCallback<Any> {
                                override fun onSuccess(result: Any) {
                                    if (!emitter.isDisposed) {
                                        emitter.onNext(1)
                                    }
                                }

                                override fun onFailed(throwable: Throwable) {
                                    if (!emitter.isDisposed) {
                                        emitter.onError(
                                            RuntimeException(
                                                "????????????????????????BLE??????:${bleName}", Exception(throwable)
                                            )
                                        )
                                    }
                                }
                            })
                        addDisposable(disposable)
                    }
                })
            }//??????????????????
            .flatMap {
                DisposableObservable.create(object :
                    DisposableObservableOnSubscribe<AylaWifiScanResult>() {
                    override fun subscribe(emitter: ObservableEmitter<AylaWifiScanResult>) {
                        val disposable = aylaBLEWiFiSetup.scanForAccessPoints(
                            10,
                            object : AylaCallback<Array<AylaWifiScanResult>> {
                                override fun onSuccess(result: Array<AylaWifiScanResult>) {
                                    if (!emitter.isDisposed) {
                                        for (aylaWifiScanResult in result) {
                                            if (TextUtils.equals(aylaWifiScanResult.ssid, ssid)) {
                                                emitter.onNext(aylaWifiScanResult)
                                                return
                                            }
                                        }
                                        onFailed(RuntimeException("???????????????????????????wifi:${ssid}"))
                                    }
                                }

                                override fun onFailed(throwable: Throwable) {
                                    if (!emitter.isDisposed) {
                                        emitter.onError(throwable)
                                    }
                                }
                            })
                        addDisposable(disposable)
                    }
                })
            }//????????????????????????
            .flatMap { wifi ->
                DisposableObservable.create(object :
                    DisposableObservableOnSubscribe<AylaWifiScanResult>() {
                    override fun subscribe(emitter: ObservableEmitter<AylaWifiScanResult>) {
                        val disposable =
                            aylaBLEWiFiSetup.sendSetupToken(setupToken, object : AylaCallback<Any> {
                                override fun onSuccess(result: Any) {
                                    if (!emitter.isDisposed) {
                                        emitter.onNext(wifi)
                                    }
                                }

                                override fun onFailed(throwable: Throwable) {
                                    if (!emitter.isDisposed) {
                                        emitter.onError(throwable)
                                    }
                                }
                            })
                        addDisposable(disposable)
                    }
                })
            }//??????setuptoken???????????????
            .flatMap { wifi ->
                DisposableObservable.create(object : DisposableObservableOnSubscribe<String>() {
                    override fun subscribe(emitter: ObservableEmitter<String>) {
                        val disposable = aylaBLEWiFiSetup.connectDeviceToAP(
                            ssid,
                            pwd,
                            WifiSecurityType(wifi.security),
                            45,
                            object : AylaCallback<String> {
                                override fun onSuccess(result: String) {
                                    if (!emitter.isDisposed) {
                                        emitter.onNext(result)
                                        emitter.onComplete()
                                    }
                                }

                                override fun onFailed(throwable: Throwable) {
                                    if (!emitter.isDisposed) {
                                        emitter.onError(throwable)
                                    }
                                }
                            })
                        addDisposable(disposable)
                    }
                })
            }//??????????????????????????????WiFi
            .doOnNext {
                deviceId = it
                pageState.postValue(ConfigStatusWrapper(ConfigStatus.HARD_WARE_CONFIG_SUCCESS).apply {
                    this.deviceId = deviceId
                })
            }
            .doOnError {
                pageState.postValue(ConfigStatusWrapper(ConfigStatus.HARD_WARE_CONFIG_FAILED).apply {
                    this.throwable = it
                })
            }
            .flatMap { deviceId ->
                DisposableObservable.create(object : DisposableObservableOnSubscribe<String>() {
                    override fun subscribe(emitter: ObservableEmitter<String>) {
                        val subscribe =
                            RequestModel.getInstance().getConnectResult(deviceId, cuId, setupToken)
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    if (it) {
                                        emitter.onNext(deviceId)
                                        emitter.onComplete()
                                    } else emitter.onError(RuntimeException())
                                }, {
                                    emitter.onError(it)
                                })
                        addDisposable(subscribe)
                    }
                }).retryWhen {
                    it.flatMap {
                        if (connectTime <= 1) {
                            Observable.error(it)
                        } else {
                            Observable.timer(2, TimeUnit.SECONDS)
                        }
                    }
                }.doOnError {
                    if (it is ConnectException || it is UnknownHostException) {
                        pageState.postValue(ConfigStatusWrapper(ConfigStatus.NET_ERROR))
                    } else {
                        pageState.postValue(ConfigStatusWrapper(ConfigStatus.CLOUD_CHECK_FAILED).apply {
                            this.deviceId = deviceId
                        })
                    }
                }
            }//????????????????????????
            .doOnNext {
                pageState.postValue(ConfigStatusWrapper(ConfigStatus.CLOUD_CHECK_SUCCESS).apply {
                    this.deviceId = deviceId
                })
            }
            .doFinally {
                aylaBLEWiFiSetup.exitSetup()
                timer.cancel()
            }
            .subscribe({}, {})
        addDisposable(disposable)
        return pageState
    }

}