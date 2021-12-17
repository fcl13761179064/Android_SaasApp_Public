package com.ayla.hotelsaas.rx

import com.ayla.base.rx.BaseException
import com.ayla.hotelsaas.base.BaseView
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import rx.Subscriber

/*
    Rx订阅者默认实现
 */
open class BaseSubscriber<T>(private val baseView: BaseView):Subscriber<T>() {

    override fun onStart() {
        super.onStart()
        if(!NetworkUtils.isConnected()){
            unsubscribe()
            onError(BaseException(-1,"网络不可用"))
        }
    }

    override fun onCompleted() {
        baseView.hideProgress()
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable?) {
        baseView.hideProgress()
        if (e is BaseException){
            baseView.showError(e)
        }
    }
}
