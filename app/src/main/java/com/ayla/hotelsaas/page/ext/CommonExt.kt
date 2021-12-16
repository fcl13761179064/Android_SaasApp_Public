package com.ayla.hotelsaas.page.ext

import android.view.View
import com.ayla.base.data.protocol.BaseResp
import com.blankj.utilcode.util.*


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


