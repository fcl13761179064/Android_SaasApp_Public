package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DistributionStructSelectView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2017/8/2
 */
public class DistributionStructSelectPresenter extends BasePresenter<DistributionStructSelectView> {
    public void fetchTransferTreeList(String hotelId) {
        Observable<List<TreeListBean>> fetchTree = RequestModel.getInstance().fetchTransferTreeList(hotelId)
                .map(new Function<BaseResult<List<TreeListBean>>, List<TreeListBean>>() {
                    @Override
                    public List<TreeListBean> apply(BaseResult<List<TreeListBean>> listBaseResult) throws Exception {
                        return listBaseResult.data;
                    }
                });
        Observable<TransferRoomListBean> fetchRoom = RequestModel.getInstance()
                .fetchTransferRoomList(hotelId)
                .map(new Function<BaseResult<TransferRoomListBean>, TransferRoomListBean>() {
                    @Override
                    public TransferRoomListBean apply(BaseResult<TransferRoomListBean> transferRoomListBeanBaseResult) throws Exception {
                        return transferRoomListBeanBaseResult.data;
                    }
                });
        Disposable subscribe = Observable
                .zip(fetchTree, fetchRoom, new BiFunction<List<TreeListBean>, TransferRoomListBean, Object[]>() {
                    @Override
                    public Object[] apply(List<TreeListBean> treeListBeans, TransferRoomListBean transferRoomListBean) throws Exception {
                        return new Object[]{treeListBeans, transferRoomListBean};
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
                .subscribe(new Consumer<Object[]>() {
                    @Override
                    public void accept(Object[] objects) throws Exception {
                        List<TreeListBean> treeListBeans = (List<TreeListBean>) objects[0];
                        TransferRoomListBean transferRoomListBean = (TransferRoomListBean) objects[1];
                        mView.showData(treeListBeans, transferRoomListBean.getRecords());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
