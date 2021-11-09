package com.ayla.hotelsaas.utils;

import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ImageLoader {
    public static void loadImg(ImageView imageView, String url, @DrawableRes int placeholder, @DrawableRes int error) {
        Glide.with(imageView).load(url).placeholder(placeholder).error(error).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }
}
