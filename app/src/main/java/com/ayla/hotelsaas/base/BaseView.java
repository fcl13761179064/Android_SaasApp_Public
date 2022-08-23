package com.ayla.hotelsaas.base;

import android.app.Activity;
import android.app.Fragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.TempUtils;

/**
 * MVP模式
 * View层
 */
public interface BaseView {

    void showProgress(String msg);

    void showProgress();

    void hideProgress();

    default void showError(Throwable throwable) {
        String errorMsg = TempUtils.getLocalErrorMsg(throwable);
        if (this instanceof Activity) {
            CustomToast.makeText(((Activity) this), errorMsg, R.drawable.ic_toast_warning);
        }
        if (this instanceof Fragment) {
            CustomToast.makeText(((Fragment) this).getActivity(), errorMsg, R.drawable.ic_toast_warning);
        }
    }

}
