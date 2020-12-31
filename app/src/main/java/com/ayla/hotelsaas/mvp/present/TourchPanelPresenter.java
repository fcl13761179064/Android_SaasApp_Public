package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.TourchPanelView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class TourchPanelPresenter extends BasePresenter<TourchPanelView> {

    public void TourchPanelRenameInsertMethod(int id, String deviceId, int cuId, String propertyName, String propertyType, String propertyValue, String deviceCategory) {
        Disposable subscribe = RequestModel.getInstance().touchPanelRenameMethod(id, deviceId, cuId, propertyName, propertyType, propertyValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("修改中...");
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mView.operateSuccess(aBoolean);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {
                    }

                    @Override
                    public String getLocalErrorMsg(Throwable throwable) {
                        return super.getLocalErrorMsg("修改失败", throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

}
