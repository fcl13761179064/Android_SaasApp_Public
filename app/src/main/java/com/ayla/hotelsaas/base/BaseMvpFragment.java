package com.ayla.hotelsaas.base;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * 基础Mvp Fragment
 */
public abstract class BaseMvpFragment<V extends BaseView, T extends BasePresenter<V>> extends BasicFragment implements BaseView{
    //业务处理层
    public T mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mPresenter = initPresenter();
        attachView();
        super.onCreate(savedInstanceState);
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
        hideProgress();
        detachView();
        super.onDestroy();
    }

    public void showProgress(String msg) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).showProgress(msg);
        }
    }

    public void showProgress() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).showProgress();
        }
    }

    public void hideProgress() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseMvpActivity) {
            ((BaseMvpActivity) activity).hideProgress();
        }
    }

}
