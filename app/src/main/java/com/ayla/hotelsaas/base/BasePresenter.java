package com.ayla.hotelsaas.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * MVP模式
 * presenter层
 */

public abstract class BasePresenter<T extends BaseView> {
    public T mView;
    CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public void attachView(T mView) {
        this.mView = mView;
    }

    public void addSubscrebe(Disposable disposable) {
        mCompositeDisposable.add(disposable);
    }

    public void detachView() {
        mCompositeDisposable.clear();
        mView = null;
    }
}
