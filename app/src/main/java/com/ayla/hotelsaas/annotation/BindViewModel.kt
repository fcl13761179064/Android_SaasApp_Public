package com.ayla.hotelsaas.annotation

import com.ayla.hotelsaas.vm.AbsViewModel
import kotlin.reflect.KClass


/**
 *
 * @Author:   chunlei.fan
 *
 * @Description:    绑定viewModel
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class BindViewModel(val model:KClass<out AbsViewModel>)
