package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.RxjavaObserver;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.SceneLikeageView;
import com.ayla.hotelsaas.widget.LoadingDialog;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

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
    public void loadNextPage(long businessId) {
        pageNum++;
        loadData(businessId);
    }

    /**
     * 加载第一页
     */
    public void loadFistPage(long resourceRoomId) {
        pageNum = 1;
        loadData(resourceRoomId);
    }

    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(long resourceRoomId) {
        RequestModel.getInstance().fetchRuleEngines(resourceRoomId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    public void runRuleEngine(int ruleId) {
        RequestModel.getInstance().runRuleEngine(ruleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mView.hideProgress();
                    }
                })
                .subscribe(new RxjavaObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addSubscrebe(d);
                        mView.showProgress("加载中");
                    }

                    @Override
                    public void _onNext(Boolean data) {
                        mView.runSceneSuccess();
                    }

                    @Override
                    public void _onError(String code, String msg) {
                        mView.runSceneFailed();
                    }
                });
    }
}
