package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.BaseResult;
import com.ayla.hotelsaas.bean.TreeListBean;
import com.ayla.hotelsaas.data.net.BaseResultTransformer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.ProjectRoomsView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ProjectRoomsPresenter extends BasePresenter<ProjectRoomsView> {
    public void loadData(String billId, String hotelId) {
        Disposable subscribe = RequestModel.getInstance().fetchTransferTreeList(billId, hotelId)
                .compose(new BaseResultTransformer<BaseResult<List<TreeListBean>>, List<TreeListBean>>() {
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<TreeListBean>>() {
                    @Override
                    public void accept(List<TreeListBean> treeListBeans) throws Exception {
                        mView.showData(treeListBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.loadDataFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
