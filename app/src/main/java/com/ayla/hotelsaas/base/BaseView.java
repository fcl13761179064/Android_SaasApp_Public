package com.ayla.hotelsaas.base;

/**
 * MVP模式
 * View层
 */
public interface BaseView {

    void showProgress(String msg);

    void showProgress();

    void hideProgress();

}
