package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.BuildConfig;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.PersonCenter;
import com.ayla.hotelsaas.bean.VersionUpgradeBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.PersonCenterView;

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
public class PersonCenterPresenter extends BasePresenter<PersonCenterView> {

    public void getUserInfo() {
        Disposable subscribe = RequestModel.getInstance().getUserInfo()
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
                .subscribe(new Consumer<PersonCenter>() {
                    @Override
                    public void accept(PersonCenter personCenter) throws Exception {
                        mView.getUserInfoFailSuccess(personCenter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getUserInfoFail(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void fetchVersionUpdate() {
        Disposable subscribe = RequestModel.getInstance().getAppVersion(BuildConfig.VERSION_CODE)
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
                .subscribe(new Consumer<BaseResult<VersionUpgradeBean>>() {
                    @Override
                    public void accept(BaseResult<VersionUpgradeBean> versionUpgradeBeanBaseResult) throws Exception {
                        VersionUpgradeBean upgradeBean = versionUpgradeBeanBaseResult.data;
                        mView.onVersionResult(upgradeBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onVersionResult(null);
                    }
                });
        addSubscrebe(subscribe);
    }
}
