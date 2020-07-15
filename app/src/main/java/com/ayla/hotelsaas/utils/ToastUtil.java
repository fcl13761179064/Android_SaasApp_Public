package com.ayla.hotelsaas.utils;

import android.content.Context;
import android.widget.Toast;

import com.ayla.hotelsaas.application.MyApplication;


/**
 * 显示Toast
 * 
 * @author guozhiqing
 * 
 */
public class ToastUtil {

	private static final String TAG = ToastUtil.class.getSimpleName();

	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

	/**
	 * 显示Toast
	 * 
	 * @param context
	 *            : 上下文
	 * @param msg
	 *            ：提示信息
	 */
	public static void show(Context context, String msg) {
		show(context, msg, TOAST_DURATION);
	}

	/**
	 * 显示Toast
	 * 
	 * @param context
	 *            : 上下文
	 * @param msgId
	 *            : 提示信息id
	 */
	public static void show(Context context, int msgId) {
		show(context, msgId, TOAST_DURATION);
	}

	/**
	 * 显示Toast
	 * 
	 * @param context
	 *            : 上下文
	 * @param msg
	 *            : 提示信息
	 * @param duration
	 *            ：显示时长
	 */
	public static void show(Context context, String msg, int duration) {
		try {
			if (null == context) {
				context = MyApplication.getContext();
			}
			Toast.makeText(context, msg, duration).show();
		} catch (Throwable e) {
			LogUtil.e(TAG, "show e[" + e + "]");
		}
	}

	/**
	 * 显示Toast
	 * 
	 * @param context
	 *            : 上下文
	 * @param msgId
	 *            : 提示信息id
	 * @param duration
	 *            ：显示时长
	 */
	public static void show(Context context, int msgId, int duration) {
		if (context == null) {
			context = MyApplication.getContext();
		}
		show(context, context.getString(msgId), duration);
	}
}
