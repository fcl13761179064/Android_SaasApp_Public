package com.ayla.hotelsaas.mvp.view

import com.ayla.hotelsaas.base.BaseView

interface LocalSceneSettingView : BaseView {
    fun saveSuccess()
    fun saveFailed(throwable: Throwable?)

    /**
     * 根据条件或者动作的id 获取属性名称和value值
     */
    fun getPropertySuccess()
    fun getPropertyFail(throwable: Throwable?)

    /**
     * 删除场景
     */
    fun deleteScene(state:Boolean,throwable: Throwable?=null)

}