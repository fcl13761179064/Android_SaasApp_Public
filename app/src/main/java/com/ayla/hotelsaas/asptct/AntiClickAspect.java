package com.ayla.hotelsaas.asptct;

import android.util.Log;
import android.view.View;

import com.ayla.hotelsaas.R;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * AOP方法 处理 点击事件的 抖动问题。
 */
@Aspect
public class AntiClickAspect {
    private static final String TAG = "AntiClickAspect";

    private static long anti_click_timeout_millis = 400;

    @Around("execution(void android.view.View.OnClickListener.onClick(android.view.View))")
    public void around_OnClickListener_onClick(ProceedingJoinPoint joinPoint) throws Throwable {
        Log.d(TAG, "around_OnClickListener_onClick: " + joinPoint.getTarget() + "#" + joinPoint.getSignature().getName());
        View view = (View) joinPoint.getArgs()[0];
        Object tag = view.getTag(R.id.anti_click_tag);
        long currentTimeMillis = System.currentTimeMillis();
        if (tag instanceof Long) {
            long out_time = currentTimeMillis - (long) tag;
            if (out_time > 0 && out_time < anti_click_timeout_millis) {
                Log.e(TAG, "anti_clicked: " + joinPoint.getTarget() + "#" + joinPoint.getSignature().getName());
                return;
            }
        }
        view.setTag(R.id.anti_click_tag, currentTimeMillis);
        joinPoint.proceed(joinPoint.getArgs());
    }
}
