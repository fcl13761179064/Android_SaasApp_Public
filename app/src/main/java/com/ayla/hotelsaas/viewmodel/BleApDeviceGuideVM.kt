package com.ayla.hotelsaas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class BleApDeviceGuideVM : AbsViewModel() {
    fun getNetworkConfigGuide(pid: String): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance()
            .getNetworkConfigGuide(pid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ networkConfigGuideBeans ->
                val guideBean = networkConfigGuideBeans[0]
                resultLD.value = Result.success(guideBean)
            }) { throwable ->
                throwable.printStackTrace()
                resultLD.value = Result.failure(throwable)
            }
        addDisposable(subscribe)
        return resultLD
    }

}