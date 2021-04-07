package com.ayla.hotelsaas.adapter;

import android.content.Context;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.utils.DensityUtils;
import com.blankj.utilcode.util.ColorUtils;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import java.util.List;

public class ScaleTabAdapter extends CommonNavigatorAdapter {

    private List<DeviceLocationBean> titles;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    @Nullable
    private Context context;
    private int index;

    public ScaleTabAdapter(List<DeviceLocationBean> titles) {
        this.titles = titles;
    }

    public void ScaleTabAdapter() {
        this.titles = titles;
    }

    public  ScaleTabAdapter(List<DeviceLocationBean> titles, ViewPager viewPager, MagicIndicator magicIndicator) {
        this.titles = titles;
        this.viewPager = viewPager;
        this.magicIndicator = magicIndicator;
    }

    /**
     * @param context
     * @param index
     * @return
     */
    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        ScaleTabTitleView scaleTabTitleView = new ScaleTabTitleView(context);
        scaleTabTitleView.setText(titles.get(index).getRegionName());
        scaleTabTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(index);
                magicIndicator.onPageScrolled(index, 0f, 0);
                magicIndicator.onPageSelected(index);
            }
        });
        return scaleTabTitleView;
    }


    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        indicator.setColors(ColorUtils.getColor(R.color.common_green));
        indicator.setYOffset(DensityUtils.setDptoPx(13));
        indicator.setRoundRadius(DensityUtils.setDptoPx(2));
        indicator.setLineWidth(DensityUtils.setDptoPx(18));
        indicator.setLineHeight(DensityUtils.setDptoPx(3));
        return indicator;
    }
}