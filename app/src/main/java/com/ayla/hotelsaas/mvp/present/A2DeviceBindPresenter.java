package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.A2DeviceBindView;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class A2DeviceBindPresenter extends BasePresenter<A2DeviceBindView> {
    public void getNetworkConfigGuide(String pid) {
        Disposable subscribe = RequestModel.getInstance()
                .getNetworkConfigGuide(String.valueOf(pid))
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
                .subscribe(new Consumer<List<NetworkConfigGuideBean>>() {
                    @Override
                    public void accept(List<NetworkConfigGuideBean> networkConfigGuideBeans) throws Exception {
                        NetworkConfigGuideBean guideBean = networkConfigGuideBeans.get(0);
                        mView.getGuideInfoSuccess(guideBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        mView.getGuideInfoFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void getA2BindInfo(String pid) {
        Disposable subscribe = RequestModel.getInstance()
                .getA2BindInfo(String.valueOf(pid))
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
                .subscribe(new Consumer<A2BindInfoBean>() {
                    @Override
                    public void accept(A2BindInfoBean a2BindInfoBean) throws Exception {
                        mView.getBindInfoSuccess(a2BindInfoBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.getBindInfoFail(throwable.getMessage());
                    }
                });
        addSubscrebe(subscribe);
    }
}
