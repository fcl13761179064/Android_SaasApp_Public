package com.ayla.hotelsaas.vm

/**
 *
 * @Author:         HuaZhongWei
 * @CreateDate:     2020/4/20 10:12
 *
 * @Description:    各个组件的公共事件
 *
 */
interface ICommonEvent {

    fun showLoadings(isHideOther: Boolean)

    fun hideLoadings()

    fun showContent()

    fun showEmpty()

    fun showEmpty(imageResId: Int, tipText: String?, retryText: String?)

    fun showError()

    fun showError(imageResId: Int, tipText: String?, retryText: String?)

    fun showToast(msg: String?) {}

    /**
     * 处理特殊事件，比如登录异常
     */
    fun handleSpecialEvent(code: Int)
}