package com.ayla.hotelsaas.base

import com.ayla.base.ext.request
import com.trello.rxlifecycle.LifecycleProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import rx.Observable

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

    /**
     * 简化网络请求传参
     */
    fun <V> Observable<V>.request(onSuccess:(value:V) -> Unit, onFailure:(e:Throwable?) -> Unit = {}){
        this.request(lifecycleProvider,mView!!,onSuccess,onFailure)
    }
}