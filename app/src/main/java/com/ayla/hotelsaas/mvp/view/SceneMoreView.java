package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.GroupDetail;

public interface SceneMoreView extends BaseView {
    /**
     * 更新名称
     * @param throwable 失败异常，成功
     */
    void upSceneDataFail(Throwable throwable);

    void upSceneDataSuccess(Boolean b);

}
