package com.ayla.hotelsaas.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayla.hotelsaas.data.net.ExceptionCode
import com.ayla.hotelsaas.data.net.SpecialException
import com.ayla.hotelsaas.vm.AbsViewModel
import com.ayla.hotelsaas.data.net.DisposableObservable
import com.ayla.hotelsaas.data.net.DisposableObservableOnSubscribe
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetup
import com.ayla.ng.lib.bootstrap.AylaBLEWiFiSetupDevice
import com.ayla.ng.lib.bootstrap.common.AylaCallback
import io.reactivex.ObservableEmitter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BleSearchVM : AbsViewModel() {
    val timeLD = MutableLiveData<Int>()
    val bleScanResult: MutableLiveData<Result<Array<AylaBLEWiFiSetupDevice>>> =
        MutableLiveData()//蓝牙搜索页面数据

    fun startTimer(): Job {
        return viewModelScope.launch {
            repeat(121) {
                timeLD.postValue(120 - it)
                delay(1000)
            }
        }
    }

    /**
     * 扫描周围的BLE设备
     */

    fun searchForBleDevice(context: Context) {
        val aylaBLEWiFiSetup = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            bleScanResult.value = Result.failure(
                SpecialException(
                    ExceptionCode.PERMISSIONNOTGRANT.code,
                    "权限未允许",
                    null
                )
            )
            return
        } else
            AylaBLEWiFiSetup(context)
        val disposable = DisposableObservable
            .create(object : DisposableObservableOnSubscribe<Array<AylaBLEWiFiSetupDevice>>() {
                override fun subscribe(emitter: ObservableEmitter<Array<AylaBLEWiFiSetupDevice>>) {
                    val disposable = aylaBLEWiFiSetup.scanDevices(5, {
                        true
                    }, object : AylaCallback<Array<AylaBLEWiFiSetupDevice>> {
                        override fun onSuccess(result: Array<AylaBLEWiFiSetupDevice>) {
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
            .doFinally {
                aylaBLEWiFiSetup.exitSetup()
            }
            .doOnDispose {
                bleScanResult.value = Result.failure(RuntimeException("主动取消"))
            }
            .subscribe({
                bleScanResult.value = Result.success(it)
            }, {
                bleScanResult.value = Result.failure(it)
            })
        addDisposable(disposable)
    }
}