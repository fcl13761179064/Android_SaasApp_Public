package com.ayla.hotelsaas.mvp.view;

import com.ayla.base.data.protocol.BaseResp;
import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.protocol.BindGetwayReq;
import com.ayla.hotelsaas.protocol.MultiBindResp;
import com.ayla.hotelsaas.protocol.MultiBindResultBean;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface MultiDeviceAddView extends BaseView {

    /**
     * 5.节点绑定流程失败
     *
     */
    void multiBindFailure(String errorMsg  );

    /**
     * 4.绑定节点成功
     */

    void multiBindSuccess(@NotNull MultiBindResp data);
}
