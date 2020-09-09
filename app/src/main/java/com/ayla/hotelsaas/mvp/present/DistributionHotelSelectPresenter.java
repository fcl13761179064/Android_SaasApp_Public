package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.HotelListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DistributionHotelSelectView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public class DistributionHotelSelectPresenter extends BasePresenter<DistributionHotelSelectView> {
    public void fetchTransferHotelList(){
        Disposable subscribe = RequestModel.getInstance().fetchTransferHotelList()
                .map(new Function<BaseResult<HotelListBean>, HotelListBean>() {
                    @Override
                    public HotelListBean apply(BaseResult<HotelListBean> hotelListBeanBaseResult) throws Exception {
                        return hotelListBeanBaseResult.data;
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
                .subscribe(new Consumer<HotelListBean>() {
                    @Override
                    public void accept(HotelListBean hotelListBean) throws Exception {
mView.showData(hotelListBean.getRecords());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
