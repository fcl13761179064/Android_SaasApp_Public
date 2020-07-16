package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class DeviceAddCategoryPresenter extends BasePresenter<DeviceAddCategoryView> {

    public void loadCategory() {
        RequestModel.getInstance()
                .getDeviceCategory()
                .subscribe(new RxjavaObserver<List<DeviceCategoryBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(List<DeviceCategoryBean> data) {
                        mView.showCategory(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {

                    }
                });
    }
}
