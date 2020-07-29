package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.OneKeyView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class OneKeyPresenter extends BasePresenter<OneKeyView> {

    public void runRuleEngine(int ruleId) {
        RequestModel.getInstance().runRuleEngine(ruleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                        mView.showProgress("加载中");
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.runSceneSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.runSceneFailed();
                    }
                });
    }

}
