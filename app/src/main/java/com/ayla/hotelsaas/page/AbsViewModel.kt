package com.ayla.hotelsaas.page

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayla.hotelsaas.manager.RepositoryManager
import com.ayla.hotelsaas.page.animation.BindRepository
import com.ayla.hotelsaas.page.event.CommonEvent
import com.ayla.hotelsaas.page.event.Event
import com.ayla.hotelsaas.page.vm.ICommonEvent

/**
 * @Author:         chunlei.fan
 * @CreateDate:     2021/12/1 9:42 PM
 * @Description:    APP的顶层ViewModel
 */
open class AbsViewModel : ViewModel(), ICommonEvent {
    init {
        initRepository()
    }


    private val _eventLiveData by lazy {
        MutableLiveData<CommonEvent>()
    }

    val eventLiveData: LiveData<CommonEvent> = _eventLiveData

    private fun initRepository() {
        javaClass.declaredFields.filter { filter ->
            filter.isAnnotationPresent(BindRepository::class.java)
        }.forEach { field ->
            val repository: AbsRepository =
                    RepositoryManager.getRepository(field.getAnnotation(BindRepository::class.java)?.repository)
            field.set(this, repository)
        }
    }

    override fun showLoadings(isHideOther: Boolean) {
        _eventLiveData.value = CommonEvent(
                event = Event.SHOW_LOADING,
                showLoadingHideOther = isHideOther
        )
    }

    @MainThread
    override fun hideLoadings() {
        _eventLiveData.postValue(CommonEvent(Event.HIDE_LOADING))
    }

    @MainThread
    override fun showContent() {
        _eventLiveData.postValue(CommonEvent(Event.CONTENT))
    }

    override fun showError() {
        _eventLiveData.value = CommonEvent(Event.ERROR)
    }

    @MainThread
    override fun showError(imageResId: Int, tipText: String?, retryText: String?) {
        _eventLiveData.postValue(CommonEvent(Event.ERROR))
    }

    override fun showEmpty() {
        _eventLiveData.value = CommonEvent(Event.EMPTY)
    }

    @MainThread
    override fun showEmpty(imageResId: Int, tipText: String?, retryText: String?) {
        _eventLiveData.postValue(CommonEvent(Event.EMPTY))
    }

    @MainThread
    override fun showToast(msg: String?) {
        _eventLiveData.postValue(CommonEvent(Event.TOAST, msg = msg))
    }

    @MainThread
    override fun handleSpecialEvent(code: Int) {
        _eventLiveData.postValue(CommonEvent(Event.SPECIAL, code))
    }

}