package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.MultiSinaleRenameView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MultiSignleRenamePresenter extends BasePresenter<MultiSinaleRenameView> {
    public void deviceRenameMethod(String deviceId, String nickName) {
        Disposable subscribe = RequestModel.getInstance().deviceSigleRename(deviceId, nickName)
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
                        mView.renameSuccess(nickName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.renameFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    /**
     * 加载区域位置
     *
     * @param
     * @param devicesBean
     */
    public void getAllDeviceLocation(Long roomId, DeviceListBean.DevicesBean devicesBean) {
        Disposable subscribe = RequestModel.getInstance()
                .getAllDeviceLocation(roomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<DeviceLocationBean>>() {
                    @Override
                    public void accept(List<DeviceLocationBean> deviceListBean) throws Exception {
                        mView.loadDeviceLocationSuccess(deviceListBean,devicesBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.renameFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }

    public void updateDevicePositionSite(String deviceId, Long regionId,String regionName) {
        Disposable subscribe = RequestModel.getInstance().devicePositionSite( deviceId, regionId,regionName)
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
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        mView.updatePurposeSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.updatePurposeFail(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
