package com.ayla.hotelsaas.annotation

import com.ayla.hotelsaas.vm.AbsRepository
import kotlin.reflect.KClass


/**
 *
 * @Author:         HuaZhongWei
 * @CreateDate:     2020/4/20 14:27
 *
 * @Description:    绑定Repository
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class BindRepository(val repository: KClass<out AbsRepository>)
