package com.ayla.base.ext

import android.view.View
import com.ayla.hotelsaas.base.BaseView
import com.ayla.hotelsaas.rx.BaseObserver
import com.ayla.hotelsaas.rx.BaseSubscriber
import com.ayla.hotelsaas.rx.ErrorConvert
import com.ayla.hotelsaas.rx.RespConvert
import com.blankj.utilcode.util.*
import com.trello.rxlifecycle.LifecycleProvider
import com.trello.rxlifecycle.android.ActivityEvent
import com.trello.rxlifecycle.components.support.RxAppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


/**
 * 网络请求（MVP）
 */
fun <T> Observable<T>.request(
    lifecycleProvider: LifecycleProvider<*>,
    view: BaseView,
    onSuccess: (value: T) -> Unit,
    onFailure: (e: Throwable?) -> Unit = {}
) {
    this.subscribeOn(Schedulers.io())
        .unsubscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .flatMap(RespConvert<T>())
        .onErrorResumeNext(ErrorConvert())
        .compose(lifecycleProvider.bindToLifecycle())
        .subscribe(object : BaseSubscriber<T>(view) {
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
        .subscribe(object : BaseObserver<T>() {
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

/**
 *  dp转px
 */
fun Number.dp() = ConvertUtils.dp2px(this.toFloat())


/**
 * string转requestBody
 */
fun String.toReqBody(): RequestBody {
    return this.toRequestBody("application/json; charset=UTF-8".toMediaType())
}

