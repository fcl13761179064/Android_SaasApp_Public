package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.OrderListView;

public interface GatewayAddGuideView extends OrderListView {
    void bindSuccess();

    void bindFailed();
}
