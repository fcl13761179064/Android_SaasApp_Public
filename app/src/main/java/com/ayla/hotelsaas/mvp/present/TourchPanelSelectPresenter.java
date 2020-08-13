package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.TourchPanelSelectView;
import com.ayla.hotelsaas.mvp.view.TourchPanelView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/29
 */
public class TourchPanelSelectPresenter extends BasePresenter<TourchPanelSelectView> {


    public void getTouchPanelData(int id, String  deviceId) {
        RequestModel.getInstance().getALlTouchPanelDeviceInfo(id, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        mView.showProgress("移除中...");
                    }
                })
                .subscribe(new RxjavaObserver<List<TouchPanelDataBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);

                    }

                    @Override
                    public void _onNext(List<TouchPanelDataBean> data) {
                        mView.hideProgress();
                        mView.operateSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.hideProgress();
                        mView.operateError(msg);
                    }
                });
    }


}
