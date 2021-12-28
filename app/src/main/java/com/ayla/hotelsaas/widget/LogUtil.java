package com.ayla.hotelsaas.widget;

import android.util.Log;


/**
 * log接口类
 * 
 * @author guozhiqing
 * 
 */
public final class LogUtil {
	public static boolean mDebug = false;


	public static void d(String tag, String str) {
		if (mDebug) {
			try {
				Log.d(tag, str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void e(String tag, String str) {
		if (mDebug) {
			try {
				Log.e(tag, str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void i(String tag, String str) {
		if (mDebug) {
			try {
				Log.i(tag, str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void v(String tag, String str) {
		if (mDebug) {
			try {
				Log.v(tag, str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void d(String str) {
		if (mDebug) {
			try {
				v("LogUtil", str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public static void w(String tag, String str) {
		if (mDebug) {
			try {
				w(tag, str);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取当前事件的内存调用栈信息
	 */
	public static void dumpStack() {
		if (mDebug) {
			Thread.dumpStack();
		}
	}
}
