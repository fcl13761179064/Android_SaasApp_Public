package com.ayla.hotelsaas.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.ayla.hotelsaas.R;

import java.util.Random;

/**
 * Created by james on 22/11/15.
 */
public class BiuEditTextUtils extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {
    private ViewGroup contentContainer;
    private int height;
    private String cacheStr = "";

    public BiuEditTextUtils(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        //setlistener();
        initView();
    }

    private void setlistener() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (cacheStr.length() < s.length()) {
                    char last = s.charAt(s.length() - 1);
                    update(last, true);

                } else {
                    char last = cacheStr.charAt(cacheStr.length() - 1);
                    update(last, false);

                }
                cacheStr = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void update(char last, boolean isUp) {
        final TextView textView = new TextView(getContext());
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setTextSize(30);
        textView.setText(String.valueOf(last));
        textView.setGravity(Gravity.CENTER);
        contentContainer.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.measure(0, 0);

        int pos = getSelectionStart();//?????????????????????????????????
        Layout layout = getLayout();
        int line = layout.getLineForOffset(pos); // ????????????????????????
        int baseline = layout.getLineBaseline(line);//???????????????????????????????????????
        int ascent = layout.getLineAscent(line);//

        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = 0;
        if (isUp) {
            startX = layout.getPrimaryHorizontal(pos) + 100;
            startY = height / 6 * 2;
            endX = startX;
            endY = baseline + ascent;
        } else {
            endX = new Random().nextInt(contentContainer.getWidth());
            endY = height / 5 * 2;
            startX = layout.getPrimaryHorizontal(pos) + 80;
            startY = baseline + ascent;
        }


        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(textView, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(textView, "translationY", startY, endY);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(textView, "scaleX", 0.6f, 1.2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(textView, "scaleY", 0.6f, 1.2f);

        animY.setInterpolator(new DecelerateInterpolator());
        animSet.setDuration(600);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                contentContainer.removeView(textView);
            }
        });
        animSet.playTogether(animX, animY, scaleX, scaleY);
        animSet.start();
    }


    private void init() {
        contentContainer = (ViewGroup) findViewById(android.R.id.content);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        height = windowManager.getDefaultDisplay().getHeight();
    }


    /**
     * ?????????????????????
     */
    private Drawable mClearDrawable;

    /**
     * ?????????????????????
     */
    private boolean hasFocus;


    private void initView() {
        //??????EditText???DrawableRight,????????????????????????????????????????????????, 2????????????????????????  ????????????????????????0,1,2,3,???
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            // ???????????????????????????????????????????????????????????????drawable???????????? ;
            mClearDrawable = ResourcesCompat.getDrawable(getResources(),R.drawable.delete_selector,null);
        }
        //???????????????????????????
        mClearDrawable.setBounds(0, 0, (int) (mClearDrawable.getIntrinsicWidth() / 1f), (int) (mClearDrawable.getIntrinsicHeight() / 1f));
        //????????????????????????
        setClearIconVisible(false);
        //???????????????????????????
        setOnFocusChangeListener(this);
        //????????????????????????????????????????????????
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mClearDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            //???????????????????????????????????????
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
                    (x < (getWidth() - getPaddingRight()));
            //??????????????????????????????????????????Rect??????
            Rect rect = mClearDrawable.getBounds();
            //???????????????????????????
            int height = rect.height();
            int y = (int) event.getY();
            //??????????????????????????????????????????
            int distance = (getHeight() - height) / 2;
            //???????????????????????????????????????(?????????????????????)
            //????????????????????????distance??????distance+????????????????????????????????????????????????????????????
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerHeight && isInnerWidth) {
                this.setText("");
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * ?????????????????????????????????????????????setCompoundDrawables???EditText????????????
     *
     * @param visible
     */
    private void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                right, getCompoundDrawables()[3]);
    }

    /**
     * ???ClearEditText?????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (hasFocus) {
            setClearIconVisible(text.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
