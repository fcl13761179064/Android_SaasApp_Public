package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class DeviceAddCategoryPresenter extends BasePresenter<DeviceAddCategoryView> {

    public void loadCategory() {
        RequestModel.getInstance()
                .getDeviceCategory()
                .subscribe(new Observer<List<DeviceCategoryBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void onNext(List<DeviceCategoryBean> deviceCategoryBeans) {
                        mView.showCategory(deviceCategoryBeans);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
