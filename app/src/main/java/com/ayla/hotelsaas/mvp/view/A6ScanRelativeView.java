package com.ayla.hotelsaas.mvp.view;

import com.ayla.hotelsaas.base.BaseView;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;

public interface A6ScanRelativeView extends BaseView {
    void reletivieSuccess(A2BindInfoBean o);
    void reletivieFail(String o);
}
