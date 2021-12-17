package com.ayla.hotelsaas.rx

import com.ayla.base.ext.showToast
import com.ayla.base.rx.BaseException
import com.ayla.hotelsaas.page.ext.showToast
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import rx.Subscriber

/**
  *  Rx订阅者默认实现(非MVP)
  */
open class BaseObserver<T>:Subscriber<T>() {

    override fun onStart() {
        super.onStart()
        if(!NetworkUtils.isConnected()){
            unsubscribe()
            onError(BaseException(-1,"网络不可用"))
        }
    }

    override fun onCompleted() {
    }

    override fun onNext(t: T) {
    }

    override fun onError(e: Throwable?) {
        if (e is BaseException){
            showToast(e.msg)
        }else{
            showToast(e?.message ?: "服务异常")
        }
    }
}
