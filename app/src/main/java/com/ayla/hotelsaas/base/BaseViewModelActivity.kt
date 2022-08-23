package com.ayla.hotelsaas.base

import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.ayla.hotelsaas.annotation.BindViewModel
import com.ayla.hotelsaas.vm.AbsViewModel
import com.ayla.hotelsaas.events.Event

abstract class BaseViewModelActivity<VB : ViewBinding> : BaseActivity<VB>() {

    override fun initSpecialEvent() {
        super.initSpecialEvent()
        initViewModel()
    }


    private fun initViewModel() {
        val list = javaClass.declaredFields
            .filter { field ->
                field.isAnnotationPresent(BindViewModel::class.java)
            }
        list.forEach { field ->
            val viewModel: AbsViewModel = ViewModelProvider(this).get(field.getAnnotation(
                BindViewModel::class.java)!!.model.java)
            initCommonEvent(viewModel)
            field.set(this, viewModel)
        }
    }

    private fun initCommonEvent(viewModel: AbsViewModel) {
        viewModel.eventLiveData.observe(this, { commonEvent ->
            when (commonEvent.event) {
                Event.SHOW_LOADING -> showLoadings(commonEvent.showLoadingHideOther)
                Event.HIDE_LOADING -> hideLoadings()
                Event.CONTENT -> showContent()
                Event.ERROR -> showError()
                Event.EMPTY -> showEmpty()
                Event.TOAST -> showToast(commonEvent.msg)
                Event.SPECIAL -> handleSpecialEvent(commonEvent.code)
            }
        })
    }
}