package com.ayla.hotelsaas.vm

import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayla.hotelsaas.manager.RepositoryManager
import com.ayla.hotelsaas.annotation.BindRepository
import com.ayla.hotelsaas.events.CommonEvent
import com.ayla.hotelsaas.events.Event
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @Author:         chunlei.fan
 * @CreateDate:     2021/12/1 9:42 PM
 * @Description:    APP的顶层ViewModel
 */
open class AbsViewModel : ViewModel(), ICommonEvent {
    init {
        initRepository()
    }

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    private val _eventLiveData by lazy {
        MutableLiveData<CommonEvent>()
    }

    val eventLiveData: MutableLiveData<CommonEvent> = _eventLiveData

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
        _eventLiveData.value = CommonEvent(Event.SHOW_LOADING)
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


    override fun onCleared() {
        super.onCleared()
        mCompositeDisposable.clear()
    }
}