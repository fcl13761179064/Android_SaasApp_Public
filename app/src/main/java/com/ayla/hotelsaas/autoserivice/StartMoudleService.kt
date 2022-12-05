package com.ayla.hotelsaas.autoserivice

import java.util.*


object StartMoudleService {
    /**
     * 接口单注册（@AutoService(S::class)接口被多注册时 只生效第一个）
     * @param clazz Class<S>
     * @return S?
     */
    fun <S> load(clazz: Class<S>): S? {
        val service = ServiceLoader.load(clazz).iterator()
        try {
            if (service.hasNext()) {
                return service.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }



    /**
     * 圈子模块
     * @return ICycleModuleService?
     */
    fun loadCycleModuleServices(): IAppModuleService? {
        return load(IAppModuleService::class.java)
    }
}