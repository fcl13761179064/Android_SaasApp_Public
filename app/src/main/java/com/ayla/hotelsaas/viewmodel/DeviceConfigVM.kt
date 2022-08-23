package com.ayla.hotelsaas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class DeviceConfigVM : AbsViewModel() {

    fun updateDeviceInfo(
        deviceId: String,
        name: String,
        regionId: Long,
        regionName: String
    ): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        val subscribe =
            RequestModel.getInstance().deviceRename(deviceId, name, "", regionId, regionName)
                .subscribeOn(
                    Schedulers.io()
                ).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    resultLD.value = Result.success(it)
                }, {
                    resultLD.value = Result.failure(it)
                })
        addDisposable(subscribe)
        return resultLD
    }
}

