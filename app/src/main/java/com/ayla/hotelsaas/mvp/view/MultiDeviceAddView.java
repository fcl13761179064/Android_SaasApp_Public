package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.protocol.BaseResp;
import com.ayla.hotelsaas.protocol.MultiBindResp;

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
