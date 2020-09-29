package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DeviceAddGuidePresenter extends BasePresenter<DeviceAddGuideView> {
    public void getNetworkConfigGuide(String categoryId){
        Disposable subscribe = RequestModel.getInstance()
                .getNetworkConfigGuide(String.valueOf(categoryId))
                .map(new Function<BaseResult<NetworkConfigGuideBean>, NetworkConfigGuideBean>() {
                    @Override
                    public NetworkConfigGuideBean apply(BaseResult<NetworkConfigGuideBean> networkConfigGuideBeanBaseResult) throws Exception {
                        return networkConfigGuideBeanBaseResult.data;
                    }
                })
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
                .subscribe(new Consumer<NetworkConfigGuideBean>() {
                    @Override
                    public void accept(NetworkConfigGuideBean o) throws Exception {
mView.showGuideInfo(o.getNetworkGuidePic(),o.getNetworkGuideDesc());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
