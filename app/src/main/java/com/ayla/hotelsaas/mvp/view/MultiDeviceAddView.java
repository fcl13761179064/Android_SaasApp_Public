package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.protocol.MultiBindResultBean;

import java.util.List;

public interface MultiDeviceAddView extends BaseView {

    /**
     * 5.节点绑定流程失败
     *
     */
    void   multiBindSuccess(List<MultiBindResultBean> data);

    /**
     * 4.绑定节点成功
     */

    void multiBindFailure(String errorMsg  );

}
