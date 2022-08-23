package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.GroupDetail;

public interface UpdateGroupLocationView extends BaseView {

    void updateLocationResult(Boolean result, Throwable throwable);

}
