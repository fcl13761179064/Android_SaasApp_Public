package com.ayla.hotelsaas.mvp.present;

import android.text.TextUtils;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.TransferRoomListBean;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DistributionStructSelectView;

import java.util.Iterator;
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
                .zip(fetchTree, fetchRoom, new BiFunction<List<TreeListBean>, TransferRoomListBean, List<TreeListBean>>() {
                    @Override
                    public List<TreeListBean> apply(List<TreeListBean> treeListBeans, TransferRoomListBean transferRoomListBean) throws Exception {
                        //treeListBeans 所有的结构、房间的层级关系
                        //transferRoomListBean 下面没有设备的结构、房间
                        //需要把treeListBeans中过滤掉有设备的房间，剩下就是：
                        //酒店中，不包含设备的 结构和房间的层级关系。
                        //分配时，直接用transformRoom接口
                        ss(treeListBeans, transferRoomListBean);

                        return treeListBeans;
                    }

                    private void ss(List<TreeListBean> treeListBeans, TransferRoomListBean transferRoomListBean) {
                        Iterator<TreeListBean> iterator = treeListBeans.iterator();
                        s2:
                        while (iterator.hasNext()) {
                            TreeListBean treeListBean = iterator.next();
                            String id_1 = treeListBean.getId();
                            for (TransferRoomListBean.RecordsBean record : transferRoomListBean.getRecords()) {
                                if (TextUtils.equals(id_1, record.getId())) {//这个结构没有设备，可以保留
                                    ss(treeListBean.getChildren(), transferRoomListBean);
                                    continue s2;
                                }
                            }
                            treeListBeans.remove(treeListBean);
                        }
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
                .subscribe(new Consumer<List<TreeListBean>>() {
                    @Override
                    public void accept(List<TreeListBean> treeListBeans) throws Exception {
                        mView.showData(treeListBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
