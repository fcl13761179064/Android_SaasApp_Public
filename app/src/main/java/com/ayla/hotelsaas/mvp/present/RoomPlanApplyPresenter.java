package com.ayla.hotelsaas.mvp.present;


import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RoomPlanApplyView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RoomPlanApplyPresenter extends BasePresenter<RoomPlanApplyView> {
    public void applyPlan(long scopeId, String linkage) {
        Disposable subscribe = RequestModel.getInstance()
                .roomPlanImport(scopeId, linkage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress();
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.importPlanSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.importPlanFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);

    }
}
