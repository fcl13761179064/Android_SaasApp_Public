package com.ayla.hotelsaas.base;

import android.os.Bundle;

import com.ayla.hotelsaas.widget.LoadingDialog;

/**
 * MVP基础Activity
 */
public abstract class BaseMvpActivity<V extends BaseView, T extends BasePresenter<V>> extends BasicActivity implements BaseView {
    //业务处理层
    public T mPresenter;

    protected abstract T initPresenter();

    private LoadingDialog progressDialog;

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

    public void showProgress(String msg) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (null != progressDialog) {
            return;
        }
        progressDialog = LoadingDialog.newInstance(msg);
        progressDialog.show(getSupportFragmentManager(), "loading");
    }

    public void showProgress() {
        showProgress("加载中...");
    }

    public void hideProgress() {
        if (null != progressDialog) {
            progressDialog.dismissAllowingStateLoss();
        }
        progressDialog = null;
    }

}
