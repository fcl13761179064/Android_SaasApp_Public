package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneLikeageView;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class SceneLikeagePresenter extends BasePresenter<SceneLikeageView> {
    //页码
    private int pageNum = 1;
    //拉取数量
    private static String maxNum = "10";

    /**
     * 加载下一页
     */
    public void loadNextPage(Long businessId) {
        pageNum++;
        loadData(businessId);
    }

    /**
     * 加载第一页
     */
    public void loadFistPage(Long resourceRoomId) {
        pageNum = 1;
        loadData(resourceRoomId);
    }

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(Long resourceRoomId) {
        RequestModel.getInstance().fetchRuleEngines(resourceRoomId.intValue())
                .subscribe(new RxjavaObserver<List<RuleEngineBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                    }

                    @Override
                    public void _onNext(List<RuleEngineBean> data) {
mView.loadDataSuccess(data);
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.loadDataFinish();
                    }
                });
    }
}
