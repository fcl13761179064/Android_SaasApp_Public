package com.ayla.base.ext

import android.view.View
import com.ayla.hotelsaas.base.BaseView
import com.ayla.hotelsaas.rx.BaseSubscriber
import com.ayla.hotelsaas.rx.ErrorConvert
import com.ayla.hotelsaas.rx.RespConvert
import com.blankj.utilcode.util.*
import com.trello.rxlifecycle.LifecycleProvider
import io.reactivex.Observable
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


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

