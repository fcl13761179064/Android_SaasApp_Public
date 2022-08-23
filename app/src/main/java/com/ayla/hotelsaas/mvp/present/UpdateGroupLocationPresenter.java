package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.GroupMoreView;
import com.ayla.hotelsaas.mvp.view.UpdateGroupLocationView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateGroupLocationPresenter extends BasePresenter<UpdateGroupLocationView> {

    public void updateGroup(String json) {
        mView.showProgress();
        Disposable subscribe = RequestModel.getInstance().updateGroup(json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.hideProgress();
                    mView.updateLocationResult(true, null);
                }, throwable -> {
                    mView.updateLocationResult(false, throwable);
                    mView.hideProgress();
                });


        addSubscrebe(subscribe);
    }
}
