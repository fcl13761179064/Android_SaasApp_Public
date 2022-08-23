package com.ayla.hotelsaas.mvp.view

import com.ayla.hotelsaas.base.BaseView
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean

interface RemoteSceneSettingView : BaseView {
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

    /**
     * 欢迎语
     */
    fun getWelcomeYuSuccess(code :Int)

    /**
     * 欢迎语失败
     */
    fun getWelcomeYuFail(throwable: Throwable?=null)

    fun getGroupActionDetail(action:BaseSceneBean.Action)

}