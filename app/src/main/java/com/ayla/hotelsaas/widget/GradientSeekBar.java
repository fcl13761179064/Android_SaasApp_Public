package com.ayla.hotelsaas.widget;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.ayla.hotelsaas.utils.ColorPickGradient;

/**
 * @author: CeMa
 * @date: 2021/7/7
 */
public class GradientSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {

  private ColorPickGradient mColorPickGradient;

  public GradientSeekBar(Context context) {
    super(context);
    init();
  }

  public GradientSeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public GradientSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  private void init() {

    this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (mColorPickGradient != null) {
          float radio = (float) progress / seekBar.getMax();
          int mColor = mColorPickGradient.getColor(radio);
          LayerDrawable layerDrawable = (LayerDrawable) seekBar.getThumb();
          GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.getDrawable(0);
          gradientDrawable.setColor(mColor);
        }

      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });

  }

  public void setColorPickGradient(
      ColorPickGradient mColorPickGradient) {
    this.mColorPickGradient = mColorPickGradient;
  }


}
