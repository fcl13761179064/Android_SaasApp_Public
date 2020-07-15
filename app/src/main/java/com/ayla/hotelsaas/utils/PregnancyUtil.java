package com.ayla.hotelsaas.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

public class PregnancyUtil extends CommonUtils {

	private static final String TAG = PregnancyUtil.class.getSimpleName();

	/**
	 * 给app评分
	 *
	 * @param context
     */
	public static void gradeForApp(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
			context.startActivity(intent);
		} catch (Exception e) {
			ToastUtil.show(context, "评分失败!");
			e.printStackTrace();
		}
	}


	/**
	 *获取应用的launcher activity的名称
	 * @param context
	 * @return
	 */
	public static String getLauncherClassName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setPackage(context.getPackageName());
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
			if (info == null) {
				info = packageManager.resolveActivity(intent, 0);
			}
			return info.activityInfo.name;
		}catch (Throwable e){
			e.printStackTrace();
			return null;
		}
	}
}
