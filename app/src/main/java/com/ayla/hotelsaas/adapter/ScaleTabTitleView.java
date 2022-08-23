package com.ayla.hotelsaas.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.TypedValue;
import androidx.annotation.Dimension;
import com.ayla.hotelsaas.R;
import com.blankj.utilcode.util.ColorUtils;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import static com.ayla.hotelsaas.utils.DensityUtils.setDptoPx;

public class ScaleTabTitleView extends SimplePagerTitleView{


    @Override
    public void onSelected(int index, int totalCount) {
        super.onSelected(index, totalCount);
        new TextPaint().setFakeBoldText(true);
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        super.onDeselected(index, totalCount);
        new TextPaint().setFakeBoldText(false);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);
        float textSize = 16 - 1 * leavePercent;
        setTextSize(Dimension.SP,textSize);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);
        float textSize = 16 + 1 * enterPercent;
        setTextSize(Dimension.SP,textSize);
    }

    public ScaleTabTitleView(Context context) {
        super(context);
        setNormalColor( ColorUtils.getColor(R.color.gray_6));
        setSelectedColor(ColorUtils.getColor(R.color.gray_3));
        int[] attribute = {android.R.attr.selectableItemBackground};
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new TypedValue().resourceId, attribute);
        Drawable background = typedArray.getDrawable(0);
        setBackground(background);
        typedArray.recycle();
        setClickable(true);
        setPadding(setDptoPx(15),0,setDptoPx(15),0);
        setTextSize(Dimension.SP,14f);
    }
}