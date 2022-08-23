package com.ayla.hotelsaas.viewmodel

import androidx.lifecycle.MutableLiveData
import com.ayla.hotelsaas.bean.BaseResult
import com.ayla.hotelsaas.bean.GroupItem
import com.ayla.hotelsaas.data.net.SpecialException
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.vm.AbsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SwitchKeyConfigVM : AbsViewModel() {

    fun getBindGroup(deviceId: String): MutableLiveData<Result<Map<String, GroupItem>>> {
        val result = MutableLiveData<Result<Map<String, GroupItem>>>()
        addDisposable(
            RequestModel.getInstance()
                .getBindGroup(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.value = Result.success(it)
                }, {
                    result.value = Result.failure(it)
                })
        )
        return result
    }

    fun getPurposeComboList(deviceId: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        addDisposable(
            RequestModel.getInstance()
                .getPurposeComboList(deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.value = Result.success(it)
                }, {
                    result.value = Result.failure(it)
                })
        )
        return result
    }

    fun getGroupDetail(groupId: String?): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance().getGroupDetail(groupId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ groupDetail ->
                result.value = Result.success(groupDetail)
            }) { throwable ->
                result.value = Result.failure(throwable)
            }
        addDisposable(subscribe)
        return result
    }

    fun getPurposeCategory(): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        val subscribe = RequestModel.getInstance().purposeCategory
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ purposeCategoryBeans ->
                result.value = Result.success(purposeCategoryBeans)
            }
            ) {
                result.value = Result.failure(it)
            }
        addDisposable(subscribe)
        return result
    }

    fun getTemplate(pid: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        addDisposable(RequestModel.getInstance().getDeviceTemplate(pid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ template ->
                result.value = Result.success(template)
            }
            ) {
                result.value = Result.failure(it)
            })
        return result
    }

    fun saveCommonUseDevice(json: String): MutableLiveData<Result<Any>> {
        val result = MutableLiveData<Result<Any>>()
        addDisposable(RequestModel.getInstance().saveUseDevice(json)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ baseResult ->
                if (baseResult.isSuccess)
                    result.value = Result.success(true)
                else {
                    var code = -1
                    try {
                        code = baseResult.code.toInt()
                    } catch (e: Exception) {
                    }
                    result.value = Result.failure(SpecialException(code, baseResult.msg, null))
                }
            }
            ) {
                result.value = Result.failure(it)
            })
        return result
    }

    fun removeUseDevice(deviceId: String?, scopeId: Long): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        addDisposable(
            RequestModel.getInstance().unBindPurposeDevice(deviceId, scopeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: BaseResult<String?> ->
                    if (result.isSuccess)
                        resultLD.value = Result.success(true)
                    else resultLD.value = Result.failure(SpecialException(1011, "移除设备失败", null))
                }
                ) { throwable ->
                    resultLD.value = Result.failure(throwable)
                }
        )
        return resultLD
    }

    fun removeGroup(groupId: String): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        addDisposable(
            RequestModel.getInstance()
                .deleteGroup(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    resultLD.value = Result.success(true)
                }, {
                    resultLD.value = Result.failure(it)
                })
        )
        return resultLD
    }

    fun updateDeviceProperty(
        deviceId: String,
        currentIndex: String,
        switchMode: Int
    ): MutableLiveData<Result<Any>> {
        val resultLD = MutableLiveData<Result<Any>>()
        addDisposable(
            RequestModel.getInstance()
                .updateSwitchProperty(deviceId, currentIndex, switchMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.isSuccess) {
                        resultLD.value = Result.success(true)
                    } else {
                        var code = -1
                        try {
                            code = it.code.toInt()
                        } catch (e: Exception) {
                        }
                        resultLD.value = Result.failure(SpecialException(code, "更新设备属性失败", null))
                    }
                }, {
                    resultLD.value = Result.failure(it)
                })
        )
        return resultLD
    }

}