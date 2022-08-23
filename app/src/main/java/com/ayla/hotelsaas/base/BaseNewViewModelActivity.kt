package com.ayla.hotelsaas.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.utils.CustomToast
import com.ayla.hotelsaas.widget.common_dialog.LoadingDialog
import java.lang.reflect.ParameterizedType

abstract class BaseNewViewModelActivity<VB : ViewBinding, VM : ViewModel> : BaseActivity<VB>() {
    private var progressDialog: LoadingDialog?=null

    protected val viewModel: VM by lazy {
        ViewModelProvider(this)[getGenericClass<VM>(this::class.java, ViewModel::class.java)!!]
    }

    override fun initSpecialEvent() {
        super.initSpecialEvent()
    }

    fun <T> getGenericClass(klass: Class<*>, filterClass: Class<*>): Class<T>? {
        val s = getSupperClass<T>(klass, filterClass)
        return s ?: getSupperClass(klass.superclass, filterClass)
    }

    fun <T> getSupperClass(klass: Class<*>, filterClass: Class<*>): Class<T>? {
        val type = klass.genericSuperclass
        if (type == null || type !is ParameterizedType) return null
        val types = type.actualTypeArguments
        for (t in types) {
            val tClass = t as Class<T>
            if (filterClass.isAssignableFrom(t)) {
                return tClass
            }
        }
        return null
    }

    fun showWarnToast(message: String) {
        CustomToast.makeText(this, message, R.drawable.ic_toast_warning)
    }
    fun showSuccessToast(message: String) {
        CustomToast.makeText(this, message, R.drawable.ic_success)
    }

    open fun showProgress(msg: String?) {
        if (isFinishing || isDestroyed) {
            return
        }
        if (null != progressDialog) {
            return
        }
        progressDialog = LoadingDialog.newInstance(msg)
        progressDialog?.show(supportFragmentManager, "loading")
    }

    open fun showProgress() {
        showProgress("加载中...")
    }

    open fun hideProgress() {
        if (null != progressDialog) {
            progressDialog?.dismissAllowingStateLoss()
        }
        progressDialog = null
    }
}