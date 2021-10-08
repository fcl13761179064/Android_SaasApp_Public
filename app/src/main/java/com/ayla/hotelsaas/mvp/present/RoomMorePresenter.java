package com.ayla.hotelsaas.mvp.present;


import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.MoveWallBean;
import com.ayla.hotelsaas.data.net.UnifiedErrorConsumer;
import com.ayla.hotelsaas.mvp.model.RequestModel;
import com.ayla.hotelsaas.mvp.view.RoomMoreView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/7/14
 */
public class RoomMorePresenter extends BasePresenter<RoomMoreView> {

    public void roomRename(long room_id, String reName) {
        Disposable subscribe = RequestModel.getInstance().roomRename(room_id, reName)
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
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.renameSuccess(reName);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {
                    }

                    @Override
                    public String getDefaultErrorMsg() {
                        return "修改失败";
                    }
                });
        addSubscrebe(subscribe);
    }

    public void deleteRoomNum(long room_id) {
        Disposable subscribe = RequestModel.getInstance().deleteRoomNum(room_id)
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
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mView.removeSuccess(s);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {

                    }

                    @Override
                    public String getDefaultErrorMsg() {
                        return "移除失败";
                    }
                });
        addSubscrebe(subscribe);
    }

    public void checkPlan(long scopeId) {
        Disposable subscribe = RequestModel.getInstance()
                .roomPlanCheck(scopeId)
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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean s) throws Exception {
                        mView.planCheckResult(s);
                    }
                }, new UnifiedErrorConsumer() {
                    @Override
                    public void handle(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
    public void releation_xiaodu(long roomId,String roomName,String hotelId) {
        Disposable subscribe = RequestModel.getInstance()
                .getRelationXiaodu(roomId,roomName,hotelId)
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
                .subscribe(new Consumer<MoveWallBean>() {
                    @Override
                    public void accept(MoveWallBean moveWallBean) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        addSubscrebe(subscribe);
    }
}
