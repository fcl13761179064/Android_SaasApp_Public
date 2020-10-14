package com.ayla.hotelsaas.base;

import android.os.Bundle;

/**
 * MVP基础Activity
 */
public abstract class BaseMvpActivity<V extends BaseView, T extends BasePresenter<V>> extends BasicActivity {
    //业务处理层
    public T mPresenter;

    protected abstract T initPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = initPresenter();
        attachView();
        super.onCreate(savedInstanceState);
    }

    private void attachView() {
        if (null != mPresenter) {
            if (mPresenter.mView == null)
                mPresenter.attachView((V) this);
        }
    }

    @Override
    protected void onDestroy() {
        detachView();
        super.onDestroy();
    }

    private void detachView() {
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }
}
