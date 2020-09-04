package com.ayla.hotelsaas.mvp.present;

import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceCategoryBean;
import com.ayla.hotelsaas.bean.RoomManageBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.DeviceAddCategoryView;
import com.ayla.hotelsaas.mvp.view.DistributionView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DistributionPresenter extends BasePresenter<DistributionView> {
    public void loadHotels() {
        List<CheckableSupport<RoomManageBean.RecordsBean>> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            RoomManageBean.RecordsBean recordsBean = new RoomManageBean.RecordsBean();
            recordsBean.setContentName("room" + i);
            CheckableSupport<RoomManageBean.RecordsBean> recordsBeanCheckableSupport = new CheckableSupport<>(recordsBean);
            recordsBeanCheckableSupport.setChecked(i == 5);
            data.add(recordsBeanCheckableSupport);
        }
        mView.hotelLoadSuccess(data);
    }
}
