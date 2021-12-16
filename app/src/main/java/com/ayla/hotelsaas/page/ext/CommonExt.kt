package com.ayla.hotelsaas.page.ext

import android.view.View
import com.ayla.base.data.protocol.BaseResp
import com.ayla.hotelsaas.page.rx.BaseSubscriber
import com.ayla.hotelsaas.page.rx.ErrorConvert
import com.ayla.hotelsaas.page.rx.RespConvert
import com.blankj.utilcode.util.*
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * 网络请求（非MVP）
 */
fun <T> Observable<T>.request(
    lifecycleProvider: RxAppCompatActivity,
    onSuccess: (value: T) -> Unit,
    onFailure: (e: Throwable?) -> Unit = {}
) {
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNext(ErrorConvert())
        .flatMap(RespConvert<T>())
        .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY))
        .subscribe(object : BaseSubscriber<T>() {
            override fun onNext(t: T) {
                super.onNext(t)
                onSuccess.invoke(t)
            }

            override fun onError(e: Throwable?) {
                super.onError(e)
                onFailure.invoke(e)
            }
        })
}


/*
    扩展点击事件
 */
fun View.onClick(listener: View.OnClickListener): View {
    setOnClickListener(listener)
    return this
}

/*
    扩展点击事件，参数为方法
 */
fun View.onClick(method: () -> Unit): View {
    setOnClickListener { method() }
    return this
}

/**
 *  扩展点击事件，防止同一时间多次点击
 */
fun View.singleClick(block: () -> Unit) {
    ClickUtils.applySingleDebouncing(this, 500) {
        block.invoke()
    }
}

/*
    扩展视图可见性
 */
fun View.setVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

/*
    扩展视图可见性
 */
fun View.setInvisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

/*
    扩展视图可见性
 */
fun View.setIns(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun BaseResp<*>.isLoginExpire(): Boolean {
    return code == 122001 || code == 121002
}

fun showToast(message:String){
    ToastUtils.showShort(message)
}


