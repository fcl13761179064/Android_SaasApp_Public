package com.ayla.hotelsaas.base;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * 基础Mvp Fragment
 */
public abstract class BaseMvpFragment<V extends BaseView, T extends BasePresenter<V>> extends BasicFragment {
    //业务处理层
    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = initPresenter();
        attachView();
    }

    protected abstract T initPresenter();

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
    public void onDestroy() {
        detachView();
        super.onDestroy();
    }
}
