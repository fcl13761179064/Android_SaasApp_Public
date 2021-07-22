package com.ayla.hotelsaas.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.ayla.hotelsaas.application.MyApplication;


/**
 * 软键盘隐藏显示工具集
 *
 * @author stephen.fan
 * @version ${VERSION}
 * @since 2016-08-18 11:16
 */
public class SoftInputUtil {
    private static final String TAG = SoftInputUtil.class.getSimpleName();

    private static final InputMethodManager imm = (InputMethodManager) MyApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

    /**
     * 如果输入法在窗口上已经显示，则隐藏，反之则显示
     */
    public static void showOrHide() {
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * SHOW_FORCED表示强制显示
     *
     * @param view 接受软键盘输入的视图
     */
    public static void showSoftInput(View view) {
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * Activity隐藏系统默认的输入法
     */
    public static void hideSysSoftInput(Activity activity) {
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 获取输入法打开的状态
     *
     * @return 若返回true，则表示输入法打开
     */
    public static boolean isOpen() {
        return imm.isActive();
    }

    /**
     * 强制隐藏键盘
     *
     * @param view 接受软键盘输入的视图
     */
    public static void hideShow(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);// 强制隐藏键盘
    }

    /**
     * 点击该控件重新获取焦点并打开键盘（配合hideShow使用）
     *
     * @param view 接受软键盘输入的视图
     *
     * 一般是EditText 一般控件xml需要添加属性
     * android:focusable="false"
     * android:focusableInTouchMode="false"
     */
    public static void requstShow(final View view) {
        view.setOnClickListener(new View.OnClickListener() {// 打开键盘
            @Override
            public void onClick(View v) {
                // view.clearFocus(); //失去焦点
                view.setFocusable(true);
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    /**
     *
     * @param oriview
     * @param destview
     */
    public static void clearFocus(View oriview, View destview){
        oriview.clearFocus();
        destview.setFocusable(true);
        destview.setFocusableInTouchMode(true);
        destview.requestFocus();
        destview.requestFocusFromTouch();
    }
}
