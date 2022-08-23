package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.GroupDetail;

public interface GroupMoreView extends BaseView {
    /**
     * 删除编组结果  获取详情  更新名称
     *
     * @param throwable 失败异常，成功为null
     */
    void getDataResult(Throwable throwable);

    void updateGroupResult(Boolean result, Throwable throwable);

    void loadGroupDetailSuccess(GroupDetail groupDetail);

    void deleteGroupResult(Boolean result);

    void updateSwitchModeResult(boolean result, Throwable throwable);

}
