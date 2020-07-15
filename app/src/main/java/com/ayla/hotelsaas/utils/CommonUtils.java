package com.ayla.hotelsaas.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

	private static final String TAG = CommonUtils.class.getSimpleName();

	/**
	 * 启动新的activity
	 *
	 * @param activity
	 * @param intent
	 * @param forResult
	 *            是否启用startActivityForResult方法
	 * @param requestCode
	 *            请求码:若forResult为false时候，不需要使用startActivityForResult方法，此设置无效
	 */
	public static void launch(Activity activity, Intent intent, boolean forResult, int requestCode) {
		if (forResult) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivity(intent);
		}
	}

	/**
	 * 判断android设备中是否有相应的应用来处理这个Intent。
	 *
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean hasIntentActivities(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

		return list.size() > 0;
	}

	/**
	 * 获取AndroidMenifest.xml中Application中的meta data
	 *
	 * @param context
	 *            ：上下文
	 * @param name
	 *            ：meta data的名称
	 * @return：meta data的值
	 */
	public static String getApplicationMetaData(Context context, String name) {
		String result = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			result = appInfo.metaData.get(name) + "";
		} catch (Exception e) {
			LogUtil.e(TAG, "getApplicationMetaData e[" + e + "]");
		}
		return result;
	}



	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回当前程序版本号
	 */
	public static String getAppVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(pi.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 返回当前程序名
	 */
	public static String getAppLabel(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			return String.valueOf(pm.getApplicationLabel(context.getApplicationInfo()));
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 返回mac地址
	 */
	public static String getMacAddress() {
		String macSerial = null;
		LineNumberReader numberReader = null;
		FileReader fileReader = null;
		try {
			Process process = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			numberReader = new LineNumberReader(new InputStreamReader(process.getInputStream()));
			String str = "";
			while (str != null){
				str = numberReader.readLine();
				if (str != null) {
					macSerial = str.trim();
					break;
				}
			}
			if (TextUtils.isEmpty(macSerial)) {
				fileReader = new FileReader("/sys/class/net/eth0/address");
				StringBuilder builder = new StringBuilder();
				char[] buffer = new char[4096];
				int readLength;
				while ((readLength = fileReader.read(buffer)) >= 0) {
					builder.append(buffer, 0, readLength);
				}
				macSerial = builder.toString().trim().substring(0, 17);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if(numberReader != null) numberReader.close();
				if(fileReader != null) fileReader.close();
			} catch (Exception e){}
		}
		return macSerial;
	}


	/**
	 * 网络状态判断
	 *
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		try {
			if (null == context || null == context.getApplicationContext()) {
				return false;
			}
			ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
					Context.CONNECTIVITY_SERVICE);
			if (manager == null) {
				return false;
			}
			NetworkInfo networkinfo = manager.getActiveNetworkInfo();
			if (networkinfo == null || !networkinfo.isAvailable()) {
				return false;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			LogUtil.e(TAG, "hasNetwork e[" + e + "]");
		}
		return true;
	}

	/**
	 * 是否在wifi环境下
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiActive(Context context) {
		boolean result = false;
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (null != manager) {
				NetworkInfo networkinfo = manager.getActiveNetworkInfo();
				if (networkinfo != null && (networkinfo.getType() == ConnectivityManager.TYPE_WIFI)) {
					result = true;
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "isWifiActive e[" + e + "]");
		}
		LogUtil.i(TAG, "isWifiActive result[" + result + "]");
		return result;
	}

	/**
	 * 是否在移动环境下，且已连接
	 *
	 * @param context
	 * @return
	 */
	public static boolean isMobileActive(Context context) {
		try {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
					Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = manager.getActiveNetworkInfo();
			return info != null && (info.getType() == ConnectivityManager.TYPE_MOBILE)
					&& info.isConnected();
		} catch (Throwable e) {
			e.printStackTrace();
			LogUtil.e(TAG, "isMobileActive e[" + e + "]");
		}
		return false;
	}

	/**
	 * 获取网络
	 *
	 * @param context
	 * @return
	 */
	public static String getExtraInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
		if (nwInfo == null) {
			return null;
		}
		Log.e("BabytreeTag", nwInfo.toString());
		String extraInfo = nwInfo.getExtraInfo();
		String typeName = nwInfo.getTypeName();
		if (typeName != null && typeName.equalsIgnoreCase("WIFI")) {
			return typeName;
		}
		return extraInfo;
	}

	/**
	 * 返回网络信息
	 *
	 * @param context
	 * @return
	 */
	public static String getNetType(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
			if (nwInfo == null) {
				return "unknow";
			}
			return nwInfo.toString();
		} catch (Exception ex) {
			return "unknow";
		}
	}

	/**
	 *  获取运营商
	 * @param context
	 * @return 0，无/其他；1，移动；2，联通；3，电信
     */
	public static int getOperator(Context context){
		TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String IMSI = telephonyManager.getSubscriberId();
		if (!TextUtils.isEmpty(IMSI) && IMSI.length() >= 5){
			switch (IMSI.substring(0, 5)) {
				case "46000":
				case "46002":
				case "46007": // 移动
					return 1;
				case "46001":
				case "46006":
					return 2; // 联通
				case "46003":
				case "46005":// 电信
					return 3;
				default:
					return 0;
			}
		}
		return 0;
	}
	/**
	 * 枚举网络状态
	 * NET_NO：没有网络
	 * NET_2G:2g网络
	 * NET_3G：3g网络
	 * NET_4G：4g网络
	 * NET_WIFI：wifi
	 * NET_UNKNOWN：未知网络
	 */
	public interface NetState{
		Integer NET_NO = 0;
		Integer NET_WIFI = 1;
		Integer NET_2G = 2;
		Integer NET_3G = 3;
		Integer NET_4G = 4;
		Integer NET_UNKNOWN = 5;}

	/**
	 * 判断当前是否网络连接
	 * @param context
	 * @return 状态码
	 */
	public static int isConnected(Context context) {
		int stateCode = NetState.NET_NO;
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni != null && ni.isConnectedOrConnecting()) {
				switch (ni.getType()) {
					case ConnectivityManager.TYPE_WIFI:
						stateCode = NetState.NET_WIFI;
						break;
					case ConnectivityManager.TYPE_MOBILE:
						switch (ni.getSubtype()) {
							case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
							case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
							case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
							case TelephonyManager.NETWORK_TYPE_1xRTT:
							case TelephonyManager. NETWORK_TYPE_IDEN:
								stateCode = NetState.NET_2G;
								break;
							case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
							case TelephonyManager.NETWORK_TYPE_UMTS:
							case TelephonyManager.NETWORK_TYPE_EVDO_0:
							case TelephonyManager.NETWORK_TYPE_HSDPA:
							case TelephonyManager.NETWORK_TYPE_HSUPA:
							case TelephonyManager.NETWORK_TYPE_HSPA:
							case TelephonyManager.NETWORK_TYPE_EVDO_B:
							case TelephonyManager.NETWORK_TYPE_EHRPD:
							case TelephonyManager.NETWORK_TYPE_HSPAP:
								stateCode = NetState.NET_3G;
								break;
							case TelephonyManager.NETWORK_TYPE_LTE:
								stateCode = NetState.NET_4G;
								break;
							default:
								stateCode = NetState.NET_UNKNOWN;
						}
						break;
					default:
						stateCode = NetState.NET_UNKNOWN;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return stateCode;
	}


	/**
	 * 获取手机系统对应的SDK_INT
	 *
	 * @return
	 */
	public static int getSDKINT() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取手机系统版本
	 *
	 * @return
	 */
	public static String getVersionRelease() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 获取厂商
	 *
	 * @return
	 */
	public static String getManufacturer() {
		return android.os.Build.MANUFACTURER;
	}

	/**
	 * 手机型号
	 *
	 * @return
	 */
	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}
	public static String getOSVersionString(){
		return Build.VERSION.RELEASE;
	}


	/**
	 * 设置cookies
	 *
	 * @param context
	 * @param url
	 *            需要设置的网页地址
	 * @param cookie
	 *            内容
	 */
	public static void setCookies(Context context, String url, String cookie, String cookieName) {
		if (cookie != null && !cookie.trim().equals("")) {
			CookieSyncManager.createInstance(context); // 得到同步cookie的对象
			CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
			cookieManager.setCookie(url, encodeCookieKV(cookieName, cookie));
			// 设置cookie
			// cookieManager.setCookie(url, "NL=" + cookie);
			// cookieManager.setCookie(url, "domain=" + ".babytree.com");
			// cookieManager.setCookie(url, "version=" + "0");
			// cookieManager.setCookie(url, "path=" + "/");
//			 cookieManager.setCookie(url, "expiry=" + "86400*30");
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				CookieSyncManager.getInstance().sync();
			} else {
				cookieManager.flush();
			}
		}
	}


	/**
	 * 编码key value
	 * <p/>
	 * <b>Note:</b> 解决网站cookie验证【NL与L cookie】，严重延迟问题
	 * <p/>
	 * <p/>
	 * 【Cookie问题--里程碑】
	 */
	@SuppressWarnings("deprecation")
	private static String encodeCookieKV(String key, String value) {
		String encodeKey = URLEncoder.encode(key);// encode Key
		String encodeValue = URLEncoder.encode(value);// encode Value
		LogUtil.i(TAG, "encodeCookieKV encodeValue[" + encodeValue + "] value[" + value + "]");
		return encodeKey + "=" + encodeValue;
	}

	/**
	 * 清除cookies
	 *
	 * @param context
	 */
	public static void clearCookies(Context context) {
		CookieSyncManager.createInstance(context); // 得到同步cookie的对象
		CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
		cookieManager.removeAllCookie();

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.getInstance().sync();
		} else {
			cookieManager.flush();
		}
	}

	/**
	 * 获取raw 文件夹下 Uri
	 *
	 * @param context
	 * @param key
     * @return
     */
	public static Uri getRawUri(Context context, String key) {
		if (TextUtils.isEmpty(key))
			return null;
		return getRawUri(context, getResbyName(context, "raw", key));
	}

	public static Uri getRawUri(Context context, int id) {
		if (id <= 0) return null;
		try {
			return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + id);
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * 获取资源id
	 *
	 * @param context
	 * @param file      raw 、drawable ...
	 * @param resName
     * @return
     */
	public static int getResbyName(Context context, String file, String resName) {
		try {
			return context.getResources().getIdentifier(resName, file, context.getPackageName());
		} catch (Throwable e) {
			return 0;
		}
	}

	/**
	 * 设置html格式的颜色值
	 *
	 * @param text
	 *            需要设置的文本
	 * @param color
	 *            需要设置的颜色：#ffffff
	 * @return
	 */
	public static String setHtmlColor(String text, String color) {
		return "<font color=\"" + color + "\">" + text + "</font>";
	}

	public static boolean isBiaoQing(String lastString) {
		if (lastString != null) {
			final String reg = "\\d+\\.{0,1}\\d*";
			String ziMu = "[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]";
			boolean isDigits = lastString.matches(reg);
			String regEx = "[\\u4e00-\\u9fa5]";
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(lastString);
			if (m.find() || isDigits == true || lastString.matches(ziMu) == true) {
				return true;
			}
		}
		return false;
	}

	/*
	 * MD5加密
	 * 注意so中有使用此接口, 不能混淆和删除, 必须生成小写
	 */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        String result = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
            byte[] byteArray = messageDigest.digest();
            StringBuffer md5StrBuff = new StringBuffer();
            for (int i = 0; i < byteArray.length; i++) {
                md5StrBuff.append(String.format("%02x", ((int) byteArray[i]) & 0xff));
            }
            result = md5StrBuff.toString().toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	/**
	 * 设置html格式的下划线
	 *
	 * @param text
	 *            需要设置的文本
	 * @return
	 */
	public static String setHtmlBottomLine(String text) {
		return "<u\">" + text + "</u>";
	}

	/**
	 * 判断是否为手机号
	 *
	 * @param phoneNum
	 * @return
	 */
	public static boolean checkPhoneNum(String phoneNum) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("^((1))\\d{10}$");
			Matcher m = p.matcher(phoneNum);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	public static boolean checkNumber(String num) {
		boolean flag = false;
		try {
			Pattern p = Pattern.compile("[0-9]*");
			Matcher m = p.matcher(num);
			flag = m.matches();
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证邮箱
	 *
	 * @param email
	 * @return
	 */
	private static final String EMAIL_REGULAR = "^([\\w\\.-]+)@([\\w\\.-]+)\\.([A-Za-z\\.]{2,6})$";
	public static boolean checkEmail(String email) {
		if (TextUtils.isEmpty(email) || !email.matches(EMAIL_REGULAR)) {
			return false;
		}
		return true;
	}

	private static final String PASSWORD_REGULAR = "(?!^\\d+$)(?!^[a-zA-Z]+$)(?!^[_]+$)\\w{8,24}";
	public static boolean checkPassword(String password) {
		if (TextUtils.isEmpty(password) || !password.matches(PASSWORD_REGULAR)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取应用安装时间(TS)秒数
	 *
	 * @return
	 */
	public static long getAppInstallTime(Context context) {

		// if (pushMessage.serial_number <
		// BabytreeUtil.getAppInstallTime(getApplicationContext())) {
		// continue;
		// }

		long ret = System.currentTimeMillis() / 1000;

		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
			String appFile = appInfo.sourceDir;
			long installed = new File(appFile).lastModified(); // Ep
			if (installed != 0) {
				ret = installed / 1000;
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "getAppInstallTime e[" + e + "]");
		}

		return ret;
	}

	/**
	 * 隐藏键盘
	 *
	 * @param context
	 */
	public static void hideSoftInputKeyboard(Activity context) {
		try {
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
					context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			LogUtil.e(TAG, "hideSoftInputKeyboard e[" + e + "]");
		}
	}

	/**
	 * 显示输入框
	 *
	 * @param editText
	 */
	public static void showSoftKeyboardAt(EditText editText) {
		if (editText == null) return;
		try {
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
			editText.requestFocus();
			InputMethodManager m = (InputMethodManager) editText.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean startsWithIgnoreCase(String string, String prefix) {
		if ((null == string) || (null == prefix)) {
			return false;
		}
		if (string.length() < prefix.length()) {
			return false;
		}
		String temp = string.substring(0, prefix.length());
		return temp.equalsIgnoreCase(prefix);
	}

	public static boolean endsWithIgnoreCase(String string, String suffix) {
		if ((null == string) || (null == suffix)) {
			return false;
		}
		if (string.length() < suffix.length()) {
			return false;
		}
		String temp = string.substring(string.length() - suffix.length(), string.length());
		return temp.equalsIgnoreCase(suffix);
	}

	public static boolean containsWithIgnoreCase(String string, String sub) {
		boolean result = false;
		if ((null != string) && (null != sub)) {
			String temp1 = string.toLowerCase(Locale.getDefault());
			String temp2 = sub.toLowerCase(Locale.getDefault());
			result = temp1.contains(temp2);
		}

		return result;
	}

	/**
	 * 设置Selector
	 * <p>
	 * 不需要设置 传-1
	 *
	 * @param context
	 * @param idNormal
	 * @param idPressed
	 * @param idFocused
	 * @param idUnable
	 * @return
	 */
	public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,
                                                int idUnable) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
		Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		// View.WINDOW_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}

	/**
	 * 隐藏输入法键盘
	 *
	 * @param context
	 * @param editText
	 */
	public static void hideInputMethod(Context context, EditText editText) {
		if (editText != null) {
			try {
				InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			} catch (Exception e) {
				LogUtil.e(TAG, "hideInputMethod e[" + e + "]");
			}
		}
	}

	/**
	 * 判断resid大于0的为合法
	 *
	 * @return
	 */
	public static boolean checkResId(int resId) {
		return resId > 0;
	}

	/**
	 * 检查View的可视值是否合法
	 *
	 * @return
	 */
	public static boolean checkVisibility(int visibility) {
		return (View.VISIBLE == visibility || View.GONE == visibility || View.INVISIBLE == visibility);
	}

	/**
	 * 读取本地SD卡文件下  XXX.properties中的属性key 的value
	 *
	 * @param filePath
	 * @param key
	 * @return
	 */
	private static String getSDcardProperties(String filePath, String key) {
		String result = "";
		if (FileUtil.isExistSdcard() && FileUtil.isFileExist(filePath)) {
			try {
				InputStream inStream = new FileInputStream(filePath);
				Properties proper = new Properties();
				proper.load(inStream);
				if (proper.containsKey(key)) {
					result = proper.get(key).toString();
				}
				inStream.close();
			} catch (Exception e) {
				LogUtil.e(TAG, "getSDcardProperties e[" + e + "]");
			}
		}
		return result;
	}

	/**
	 * 首页是否在运行
	 *
	 * @author wangshuaibo
	 * @return
	 */
	public static boolean isAppRunning(Context context) {
		try {
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			ActivityManager.RunningTaskInfo runningTaskInfo = manager.getRunningTasks(1).get(0);
			LogUtil.d("pack:" + runningTaskInfo.topActivity.getPackageName() + " | " + context.getPackageName());
			return runningTaskInfo.topActivity.getPackageName().equalsIgnoreCase(context.getPackageName());
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 在某些经过简洁版本的Android系统中
	 * RunningAppProcessInfo.importance的值一直为IMPORTANCE_BACKGROUND.
	 * 记录是否为特殊这种系统
	 */
	private static boolean mIsSpecialSystem = false;

	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public static boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (!mIsSpecialSystem) {
			try {
				boolean isSpecial = true;
				String packageName = context.getPackageName();
				List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
				if (null != appProcesses) {
					for (RunningAppProcessInfo appProcess : appProcesses) {
						if (appProcess.processName.equals(packageName)) {
							if ((appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
									|| (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE)) {
								KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
								return !keyguardManager.inKeyguardRestrictedInputMode();
							}
						}
						if (isSpecial) {
							if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
								isSpecial = false;
							}
						}
					}
					if (isSpecial) {
						mIsSpecialSystem = true;
						return !isApplicationBroughtToBackgroundByTask(context, activityManager);
					}
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "isAppOnForeground e[" + e + "]");
			}
		} else {
			return !isApplicationBroughtToBackgroundByTask(context, activityManager);
		}
		return false;
	}

	/**
	 * 判断当前应用程序是否处于后台，通过getRunningTasks的方式
	 *
	 * @return true 在后台; false 在前台
	 */
	public static boolean isApplicationBroughtToBackgroundByTask(Context context, ActivityManager activityManager) {
		try {
			List<RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
			if (!tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (!topActivity.getPackageName().equals(context.getPackageName())) {
					return true;
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "isApplicationBroughtToBackgroundByTask e[" + e + "]");
		}
		return false;
	}

	/**
	 * 下载app
	 */
	public static void downapp(Context context, String downURL) {
		try {
			Intent intent = null;
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(downURL));
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取定位key
	 *
	 * @param context
	 * @return
	 */
	public static String getAK(Context context) {
		return getApplicationMetaData(context, "com.baidu.lbsapi.API_KEY");
	}

	/**
	 * 用户名字符长度为4-12个英文字母、数字或汉字;
	 *
	 * @param name
	 * @return true 昵称不符合规则
	 */
	public static boolean isNameInvalid(String name) {
		return (name == null) || (name.length() < 4) || (name.length() > 12);
	}

	/**
	 * 动态配置图片资源
	 *
	 * @param mContext
	 * @param pic_name
	 *            图片名字
	 * @return
	 */
	public static int getDrawbleResource(Context mContext, String pic_name) {
		ApplicationInfo appInfo = mContext.getApplicationInfo();
		// 得到该图片的id(name 是该图片的名字，"drawable"
		// 是该图片存放的目录，appInfo.packageName是应用程序的包)
		int resID = mContext.getResources().getIdentifier(pic_name, "drawable", appInfo.packageName);
		return resID;
	}

    /**
     * 获取IMEI
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        String result = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                result = tm.getDeviceId();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取android id
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String result = null;
        try {
            result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取build serial
     * @param context
     * @return
     */
    public static String getBuildSerial(Context context) {
        String result = null;
        try {
            result = android.os.Build.SERIAL;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

	/**
	 * server IP 地址
	 *
	 * @param host
	 * @return
	 */
	public static String getHostIPAddress(String host) {
		try {
			return InetAddress.getByName(host).getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * client IP 地址
	 *
	 * @return
	 */
	public static String getLocalIPAddress() {
		try{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()){
						return inetAddress.getHostAddress();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}


		/**
         * provide default 0 to avoid NumberFormatException
         *
         * @param value integer in format of string
         * @return 0 or value as int
         */
	public static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static long parseLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}
	public static float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return 0;
		}
	}

	public static float parseFloat(String value, float defValue) {
		try {
			return Float.parseFloat(value);
		} catch (Exception e) {
			return defValue;
		}
	}



    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;

        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = CommonUtils.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    /**
     * 判断class对应的Service是否开启
     * @param context
     * @param className service 全称
     * eg： com.babytree.apps.pregnancy.music.MusicService
     *
     *                   获取当前手机运行的前200个服务
     * @return
     */
    public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        if (!TextUtils.isEmpty(className)) {
            try {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(200);
                if (serviceList == null || !(serviceList.size() > 0)) {
                    return false;
                }
                for (int i = 0; i < serviceList.size(); i++) {
                    ComponentName service = serviceList.get(i).service;
                    if (service != null && className.equals(service.getClassName())) {
                        isRunning = true;
                        break;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return isRunning;
    }

	/**
	 * 获取View Tag里面的值
	 *
	 * @param view        数据源
	 * @param defaultData 默认返回数据
	 * @return Object 数据
	 */
	public static Object getDataByViewTag(View view, Object defaultData) {
		try {
			if (view != null) {
				Object tag = view.getTag();
				if (tag != null) {
					return tag;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return defaultData;
	}

	/**
	 * 获取Asset目录下的Json文件
	 *
	 * @param context
	 * @param fileName 	文件名
	 * @return
	 */
	public static String getAssetJsonByName(Context context, String fileName) {
		StringBuilder sb = new StringBuilder();
		BufferedReader bf = null;
		try {
			AssetManager am = context.getAssets();
			bf = new BufferedReader(new InputStreamReader(am.open(fileName)));
			String line;
			while ((line = bf.readLine()) != null) {
				sb.append(line);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (bf != null) {
					bf.close();
					bf = null;
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return sb.toString().length() > 0 ? sb.toString() : null;
	}

	/**
	 * 在指定 count范围内，获取一个随机数，不包含之前的 preIndex
	 *
	 * @param count 随机范围
	 * @param preIndex 之前的值
	 * @return
	 */
	public static int getNewRandomInt(int count, int preIndex) {
		try {
			Random random = new Random();
			int nextInt = random.nextInt(count);
			if (preIndex != nextInt) {
				return nextInt;
			}
			return getNewRandomInt(count, preIndex);
		} catch (Throwable e) {
			e.printStackTrace();
			return preIndex;
		}
	}

	/**
	 * 传入的字符串，是否等于1
	 *
	 * @param value 值
	 * @return
	 */
	public static boolean isEqualTo1(String value) {
		return "1".equals(value);
	}

	/**
	 * 把资源文件，转换成Drawable
	 *
	 * @param context
	 * @param
	 * @return
	 */
	public static Drawable getDrawableById(Context context, int id) {
		if (id <= 0) return null;
		try {
			Drawable drawable = context.getResources().getDrawable(id);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			return drawable;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 测量文字在TextView上面的宽度
	 *
	 * @param textView
	 * @param text
	 * @return
	 */
	public static int getTextViewLength(TextView textView, String text) {
		try {
			TextPaint paint = textView.getPaint();
			return (int) paint.measureText(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	static final Pattern reUnicode = Pattern.compile("0x([0-9a-zA-z]{4,5})");

	public static String decodeUnicode(String theString) {

		Matcher m = reUnicode.matcher(theString);
		StringBuffer sb = new StringBuffer(theString.length());
		while (m.find()) {
			m.appendReplacement(sb, newString(Integer.parseInt(m.group(1), 16)));
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static String newString(int codePoint) {
		return new String(Character.toChars(codePoint));
	}

	// meitun应用是否在前台
	private static boolean mIsMTActive = false;
	public static boolean isOnMT = false;

	/**
	 * 应用是否在前台
	 *
	 * @return
	 */
	public static boolean isMTActive() {
		LogUtil.i(TAG, "isActive mIsActive[" + mIsMTActive + "]");
		return mIsMTActive;
	}


	/**
	 * 启动原生页面
	 * @param context
	 * @param schemeName
	 */
	public static void openScheme(Context context, String schemeName) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(schemeName));
			context.startActivity(intent);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取指定app VersionCode
	 */
	public static int getAppVersionCode(Context context, String packageName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			return info.versionCode;
		} catch (Throwable e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 指定Apk是否安装
	 *
	 * @param context
	 * @param packageName
     * @return
     */
	public static boolean isAppInstall(Context context, String packageName) {
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
		return intent != null;
	}


	/**
	 * findViewById 工具方法
	 *
	 * @param view
	 * @param id
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * 根据要求的最大行数，在末尾添加...
	 * @param tv
	 * @param maxlines
     */
	public static void setEllipsizeForText(final TextView tv, final int maxlines) {
		ViewTreeObserver vto = tv.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				try {
					ViewTreeObserver obs = tv.getViewTreeObserver();
					obs.removeGlobalOnLayoutListener(this);
					if (tv.getLineCount() > maxlines) {
						int lineEndIndex = tv.getLayout().getLineEnd(maxlines - 1);
						CharSequence text = tv.getText().subSequence(0, lineEndIndex - 2);
						tv.setText(text);
						tv.append("...");
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 判断list 是否为空 或者null
	 * @param list
	 * @return
     */
	public static boolean isEmptyList(List list) {
		if (list == null || list.isEmpty()) return true;
		return false;
	}

	/**
	 * 判定输入汉字
	 *
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

	/**
	 * 检测String是否全是中文
	 *
	 * @param name
	 * @return
	 */
	public static boolean checkNameChese(String name) {
		boolean res = true;
		try {
			char[] cTemp = name.toCharArray();
			for (int i = 0; i < name.length(); i++) {
				if (!isChinese(cTemp[i])) {
					res = false;
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return res;
	}

}