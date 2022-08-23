package com.ayla.hotelsaas.manager

import com.ayla.hotelsaas.vm.AbsRepository
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 *
 * @Author:         HuaZhongWei
 * @CreateDate:     2020/12/15 17:50
 *
 * @Description:
 *
 */
object RepositoryManager {

    private val listThreadLocal =
            object : ThreadLocal<MutableMap<KClass<out AbsRepository>, AbsRepository>>() {
                override fun initialValue(): MutableMap<KClass<out AbsRepository>, AbsRepository>? {
                    return mutableMapOf()
                }
            }


    fun <T> getRepository(clazz: KClass<out AbsRepository>?): T {
        if (clazz == null) {
            throw IllegalArgumentException("获取AbsRepository的参数不能为 null ")
        }
        var repository = listThreadLocal.get()?.get(clazz)
        if (repository == null) {
            val temp = clazz.createInstance()
            listThreadLocal.get()?.put(clazz, temp)
            repository = temp
        }
        return repository as T
    }
}

