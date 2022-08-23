package com.ayla.hotelsaas.events

/**
 *
 * @Author:         HuaZhongWei
 * @CreateDate:     2020/4/20 10:31
 *
 * @Description:    通用事件，用于发送
 *
 */
data class CommonEvent(
    val event: Event,
    val code: Int = 0,
    val msg: String? = null,
    val showLoadingHideOther: Boolean = true
)