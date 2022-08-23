package com.ayla.hotelsaas.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayla.hotelsaas.data.net.SelfMsgException
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirmwareUpdateVM : AbsViewModel() {

    val repeatUpdateResult = MutableLiveData<String>()

    fun update(deviceJobId: String, dsn: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance().updateFirmwareVersion(deviceJobId, dsn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (TextUtils.equals(it.code, "0")) {
                    result.value = Result.success(true)
                } else result.value = Result.failure(SelfMsgException("升级失败", null))
            }, {
                result.value = Result.failure(it)
            })
        addDisposable(subscribe)
        return result
    }

    fun retry(jobId: String, dsn: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance().retryFirmwareVersion(jobId, dsn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (TextUtils.equals(it.code, "0")) {
                    result.value = Result.success(true)
                } else result.value = Result.failure(SelfMsgException("重试失败", null))
            }, {
                result.value = Result.failure(it)
            })
        addDisposable(subscribe)
        return result
    }

    fun getStatus(jobId: Int, dsn: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance().getFirmwareUpdateStatus(jobId, dsn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.value = Result.success(it)
            }, {
                result.value = Result.failure(it)
            })
        addDisposable(subscribe)
        return result
    }

    val testLD = MutableLiveData<String>()
    fun test() {
        viewModelScope.launch {
//            testLD.value = Result.success("111")
            repeat(10) {

            }
        }
    }

    fun repeatGetUpdateResult(jobId: Int, dsn: String): Job {
        return viewModelScope.launch {
            repeat(Int.MAX_VALUE) {
                delay(5000)
                val subscribe = RequestModel.getInstance().getFirmwareUpdateStatus(jobId, dsn)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.code == "0") {
                            repeatUpdateResult.postValue(it.data)
                        } else {
                            repeatUpdateResult.postValue("error")
                        }
                    }, {
                        repeatUpdateResult.postValue("error")
                    })
                addDisposable(subscribe)
            }
        }
    }
}