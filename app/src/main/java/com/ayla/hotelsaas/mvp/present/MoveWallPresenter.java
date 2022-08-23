package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.bean.ZxingMoveWallBean;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.MoveWallView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MoveWallPresenter extends BasePresenter<MoveWallView> {
    public void getNetworkConfigGuide(ZxingMoveWallBean zxingMoveWallBean) {
        Disposable subscribe = RequestModel.getInstance()
                .getMoveWallData(zxingMoveWallBean.getId(),zxingMoveWallBean.getRoomId())//这是施工单id
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
                .subscribe(new Consumer<MoveWallBean>() {
                    @Override
                    public void accept(MoveWallBean moveWallBean) throws Exception {
                        mView.getMoveWallDataSuccess(moveWallBean,zxingMoveWallBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ServerBadException) {
                            mView.getMoveWallDataFail(throwable.getMessage(), (ServerBadException) throwable);
                        }

                    }
                });
        addSubscrebe(subscribe);
    }

}
