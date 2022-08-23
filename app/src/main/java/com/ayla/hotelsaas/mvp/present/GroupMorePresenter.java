package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.data.net.SpecialException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.GroupMoreView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GroupMorePresenter extends BasePresenter<GroupMoreView> {

    public void getGroupDetail(String groupId, boolean showProgress) {
        if (showProgress)
            mView.showProgress();
        Disposable subscribe = RequestModel.getInstance().getGroupDetail(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groupDetail -> {
                    if (showProgress)
                        mView.hideProgress();
                    mView.loadGroupDetailSuccess(groupDetail);
                }, throwable -> {
                    mView.getDataResult(throwable);
                    if (showProgress)
                        mView.hideProgress();
                });


        addSubscrebe(subscribe);
    }

    public void deleteGroup(String groupId) {
        mView.showProgress();
        Disposable subscribe = RequestModel.getInstance().deleteGroup(groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.deleteGroupResult(result);
                }, throwable -> {
                    mView.getDataResult(throwable);
                    mView.hideProgress();
                });


        addSubscrebe(subscribe);
    }

    public void updateGroup(String json) {
        mView.showProgress();
        Disposable subscribe = RequestModel.getInstance().updateGroup(json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.hideProgress();
                    mView.updateGroupResult(true, null);
                }, throwable -> {
                    mView.updateGroupResult(false, throwable);
                    mView.hideProgress();
                });


        addSubscrebe(subscribe);
    }

    public void updateSwitchMode(String deviceId, String currentIndex, int mode) {
        Disposable subscribe = RequestModel.getInstance().updateSwitchProperty(deviceId, currentIndex, mode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mView.hideProgress();
                    if (result.isSuccess())
                        mView.updateSwitchModeResult(true, null);
                    else
                        mView.updateSwitchModeResult(false, new SpecialException(1011, "更新开关模式失败", null));
                }, throwable -> {
                    mView.updateSwitchModeResult(false, throwable);
                    mView.hideProgress();
                });
        addSubscrebe(subscribe);
    }
}
