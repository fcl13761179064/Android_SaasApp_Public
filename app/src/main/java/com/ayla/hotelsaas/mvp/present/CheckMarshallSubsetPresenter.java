package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.GroupDetailBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.CheckMarshallSubsetView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CheckMarshallSubsetPresenter extends BasePresenter<CheckMarshallSubsetView> {

    public void getMarShallSubSet(String  groupId) {
        Disposable subscribe = RequestModel.getInstance().checkMarshallSub(groupId)
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
                .subscribe(new Consumer<GroupDetailBean>() {
                    @Override
                    public void accept(GroupDetailBean baseResult) throws Exception {
                        mView.getSubsetSucess(baseResult.getGroupDeviceList());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getSubsetFail(throwable.getMessage());
                    }
                });
        addSubscrebe(subscribe);
    }

}
