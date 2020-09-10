package com.ayla.hotelsaas.utils;

/**
 * @描述  防止快速点击
 * @作者 丁军伟
 * @时间 2017/7/20
 */
public class FastClickUtils {
    private static long lastClickTime;
    private final static int SPACE_TIME = 800;

    public static void initLastClickTime() {
        lastClickTime = 0;
    }

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime > SPACE_TIME) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;
        return isClick2;
    }
}
