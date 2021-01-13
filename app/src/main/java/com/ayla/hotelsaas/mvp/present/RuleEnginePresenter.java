package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RuleEngineView;
import com.ayla.hotelsaas.utils.BeanObtainCompactUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RuleEnginePresenter extends BasePresenter<RuleEngineView> {
    /**
     * 加载列表
     *
     * @param resourceRoomId
     */
    public void loadData(long resourceRoomId) {
        Disposable subscribe = RequestModel.getInstance()
                .fetchRuleEngines(resourceRoomId)
                .map(new Function<List<RuleEngineBean>, List<BaseSceneBean>>() {
                    @Override
                    public List<BaseSceneBean> apply(@NonNull List<RuleEngineBean> ruleEngineBeans) throws Exception {
                        List<BaseSceneBean> sceneBeans = new ArrayList<>();
                        for (RuleEngineBean ruleEngineBean : ruleEngineBeans) {
                            BaseSceneBean sceneBean = BeanObtainCompactUtil.obtainSceneBean(ruleEngineBean);
                            sceneBeans.add(sceneBean);
                        }
                        return sceneBeans;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<BaseSceneBean>>() {
                    @Override
                    public void accept(List<BaseSceneBean> baseSceneBeans) throws Exception {
                        mView.loadDataSuccess(baseSceneBeans);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {
                        mView.loadDataFailed(throwable);
                    }
                });
        addSubscrebe(subscribe);
    }
}
