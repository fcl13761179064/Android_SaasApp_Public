package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.CreateProjectView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CreateProjectPresenter extends BasePresenter<CreateProjectView> {
    public void createProject(String title, int trade, int type) {
        Disposable subscribe = RequestModel.getInstance().createBill(title, trade, type)
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
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(BaseResult baseResult) throws Exception {
                        mView.onSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
