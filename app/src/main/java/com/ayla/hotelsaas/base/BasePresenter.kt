package com.ayla.hotelsaas.base

import com.trello.rxlifecycle.LifecycleProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
/**
 * MVP模式
 * presenter层
 */
abstract class BasePresenter<T : BaseView?> {
    @JvmField
    var mView: T? = null
    var mCompositeDisposable = CompositeDisposable()
    fun attachView(mView: T) {
        this.mView = mView
    }

    //Dagger注入，Rx生命周期管理
    lateinit var lifecycleProvider: LifecycleProvider<*>

    fun addSubscrebe(disposable: Disposable?) {
        mCompositeDisposable.add(disposable!!)
    }

    fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }


}