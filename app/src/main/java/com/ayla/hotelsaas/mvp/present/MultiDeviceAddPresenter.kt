package com.ayla.hotelsaas.mvp.present

import android.os.Bundle
import com.ayla.base.ext.toReqBody
import com.ayla.base.rx.BaseException
import com.ayla.hotelsaas.api.CommonApi
import com.ayla.hotelsaas.base.BasePresenter
import com.ayla.hotelsaas.bean.A2BindInfoBean
import com.ayla.hotelsaas.data.net.RetrofitHelper
import com.ayla.hotelsaas.mvp.model.RequestModel
import com.ayla.hotelsaas.mvp.view.MultiDeviceAddView
import com.ayla.hotelsaas.protocol.BindGetwayReq
import com.ayla.hotelsaas.protocol.MultiBindRequest
import com.ayla.hotelsaas.protocol.MultiBindResultBean
import com.ayla.hotelsaas.widget.AppUtil
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ShellUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class MultiDeviceAddPresenter : BasePresenter<MultiDeviceAddView?>() {

    private val apiService = RetrofitHelper.getRetrofit().create(CommonApi::class.java)

    /**
     * 批量绑定设备
     */
    fun multiBindNodeDevice(
        gatewayCuId: Int,
        oemModel: String,
        subNode: Bundle,
        deviceIds: List<String>
    ) {
        val bindReqList = deviceIds.mapIndexed { index, deviceId ->
            BindGetwayReq(
                deviceId,
                gatewayCuId,
                scopeId = ((subNode.get("scopeId") ?: 0L) as Long),
                2,
                oemModel,
                deviceName = (subNode.get("productName") ?: "") as String,
                nickName = (subNode.get("productName")
                    ?: "" + AppUtil.getChineseNumberFromArabNumber(index + 1)) as String,
                pid = (subNode.get("pid") ?: "") as String,
            )
        }
        val subscribe =
            apiService.multiBindDevice(GsonUtils.toJson(MultiBindRequest(bindReqList)).toReqBody())
                .flatMap { bindResult ->
                    val successDeviceIds = bindResult.data.success
                    if (successDeviceIds.isNullOrEmpty()) {
                        return@flatMap Observable.error(Exception())
                    }
                    return@flatMap Observable.just(bindResult)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mView!!.showProgress() }
                .doFinally { mView!!.hideProgress() }
                .subscribe({ a2BindInfoBean -> mView?.multiBindSuccess(a2BindInfoBean.data) }
                ) { throwable -> mView?.multiBindFailure(throwable.message) }
        addSubscrebe(subscribe)
    }


    /**
     * 设置房间失败时返回默认数据
     */
    private fun transformBindResult(
        successDeviceIds: List<String>,
        bindReqList: List<BindGetwayReq>,
        subNode: Bundle,
        roomId: String = "",
        roomName: String = "全部"
    ) = successDeviceIds.mapIndexed { index, deviceId ->
        MultiBindResultBean(
            deviceId,
            bindReqList.find { bindReq -> bindReq.deviceId == deviceId }?.nickName
                ?: "${subNode.get("productName")}${AppUtil.getChineseNumberFromArabNumber(index + 1)}",
            roomId,
            roomName
        )
    }
}
