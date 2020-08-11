package com.ayla.hotelsaas.base;

import android.os.Bundle;

/**
 * MVP基础Activity
 */
public abstract class BaseMvpActivity<V extends BaseView, T extends BasePresenter<V>> extends BasicActivity {
    //业务处理层
    public T mPresenter;

    @Override
    public void initSaveInstace(Bundle savedInstanceState) {
        mPresenter = initPresenter();
        attachView();
        super.initSaveInstace(savedInstanceState);
    }

    protected abstract T initPresenter();

    @Override
    public void onResume() {
        super.onResume();
        attachView();
    }

    private void attachView() {
        if (null != mPresenter) {
            if (mPresenter.mView == null)
                mPresenter.attachView((V) this);
        }
    }

    public void detachView() {
        if (null != mPresenter) {
            mPresenter.detachView();
            mPresenter = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachView();
    }
}
