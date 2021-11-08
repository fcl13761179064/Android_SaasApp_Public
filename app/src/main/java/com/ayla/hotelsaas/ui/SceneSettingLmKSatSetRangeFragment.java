package com.ayla.hotelsaas.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.gradientseekbar.ColorPickGradient;
import com.ayla.hotelsaas.gradientseekbar.GradientSeekBar;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.blankj.utilcode.util.NumberUtils;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面，范围选择情况
 */
public class SceneSettingLmKSatSetRangeFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {
    @BindView(R.id.sb_value)
    BubbleSeekBar mAppCompatSeekBar;
    @BindView(R.id.iv_lm_light)
    ImageView iv_lm_light;
    @BindView(R.id.tv_notice)
    TextView tv_notice;
    @BindView(R.id.tv_value)
    TextView tv_value;
    @BindView(R.id.rl_k_sat_layout)
    RelativeLayout rl_k_sat_layout;
    @BindView(R.id.tv_k_name_one)
    TextView tv_k_name_one;
    @BindView(R.id.tv_k_name_two)
    TextView tv_k_name_two;
    @BindView(R.id.gradientseekbar)
    SeekBar gradientseekbar;

    private int[] PICKCOLORBAR_COLORS;
    //每个颜色的位置
    private float[] PICKCOLORBAR_POSITIONS;
    ColorPickGradient colorPickGradient;
    private int fractionDigits;

    public static SceneSettingLmKSatSetRangeFragment newInstance(DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingLmKSatSetRangeFragment fragment = new SceneSettingLmKSatSetRangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_function_lm_k_sat_range;
    }

    private DeviceTemplateBean.AttributesBean attributesBean;

    @Override
    protected void initView(View view) {
        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        Double max = attributesBean.getSetup().getMax();
        Double min = attributesBean.getSetup().getMin();
        Double step = attributesBean.getSetup().getStep();
        String unit = attributesBean.getSetup().getUnit();
        String displayName = attributesBean.getDisplayName();
        fractionDigits = (int) Math.log10(1 / step);
        String min_value = NumberUtils.format(min, fractionDigits, false);
        String max_calue = NumberUtils.format(max, fractionDigits, false);
        boolean editMode = getActivity().getIntent().getBooleanExtra("editMode", false);
        BaseSceneBean.Action action = (BaseSceneBean.Action) getActivity().getIntent().getSerializableExtra("action");
        float targetValue = Double.valueOf((max + min) / 2).floatValue();
        if (editMode && action != null) {
            String rightValue = action.getRightValue();
            try {
                targetValue = Float.parseFloat(rightValue);
            } catch (Exception ignored) {
            }
        }
        if ("lm".equalsIgnoreCase(unit)) {
            iv_lm_light.setVisibility(View.VISIBLE);
            mAppCompatSeekBar.setVisibility(View.VISIBLE);
            gradientseekbar.setVisibility(View.GONE);
            rl_k_sat_layout.setVisibility(View.GONE);
            mAppCompatSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
                @Override
                public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                    tv_value.setText(String.format("%s", progress));
                }
            });
            mAppCompatSeekBar.getConfigBuilder()
                    .min(min.floatValue())
                    .max(max.floatValue())
                    .sectionCount(Double.valueOf(((max - min) / step)).intValue())
                    .sectionTextColor(Color.parseColor("#648C1A"))
                    .trackColor(Color.parseColor("#DFE4EB"))
                    .build();
            mAppCompatSeekBar.setProgress(targetValue);
            tv_notice.setVisibility(View.INVISIBLE);
        }

        if ("k".equalsIgnoreCase(unit)) {
            iv_lm_light.setVisibility(View.INVISIBLE);
            mAppCompatSeekBar.setVisibility(View.GONE);
            gradientseekbar.setVisibility(View.VISIBLE);
            PICKCOLORBAR_COLORS = new int[]{
                    ContextCompat.getColor(getContext(), R.color.seekbar_k_left),
                    ContextCompat.getColor(getContext(), R.color.seekbar_k_left),
                    ContextCompat.getColor(getContext(), R.color.seekbar_k_end)};
            PICKCOLORBAR_POSITIONS = new float[]{0f, 1f};
            colorPickGradient = new ColorPickGradient(PICKCOLORBAR_COLORS,
                    PICKCOLORBAR_POSITIONS);
            rl_k_sat_layout.setVisibility(View.VISIBLE);
            tv_k_name_one.setText("较冷");
            tv_k_name_two.setText("较暖");

            gradientseekbar.setMax(Integer.valueOf(max_calue));
            gradientseekbar.setMin(Integer.valueOf(min_value));
            gradientseekbar.setProgress((int) targetValue);
            tv_value.setText(String.format("%s", (int) targetValue) + "");
            gradientseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tv_value.setText(String.format("%s", progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            tv_notice.setText("色温调节仅在白光模式下有效");
        } else if ("SAT".equalsIgnoreCase(unit)) {
            iv_lm_light.setVisibility(View.INVISIBLE);
            gradientseekbar.setVisibility(View.VISIBLE);
            PICKCOLORBAR_COLORS = new int[]{
                    ContextCompat.getColor(getContext(), R.color.seekbar_sat_left),
                    ContextCompat.getColor(getContext(), R.color.seekbar_sat_left),
                    ContextCompat.getColor(getContext(), R.color.seekbar_sat_end)};
            PICKCOLORBAR_POSITIONS = new float[]{0f, 1f};
            colorPickGradient = new ColorPickGradient(PICKCOLORBAR_COLORS,
                    PICKCOLORBAR_POSITIONS);
            rl_k_sat_layout.setVisibility(View.VISIBLE);
            tv_k_name_one.setText("灰白");
            tv_k_name_two.setText("鲜艳");
            gradientseekbar.setMax(Integer.valueOf(max_calue));
            gradientseekbar.setMin(Integer.valueOf(min_value));
            gradientseekbar.setProgress((int) targetValue);
            tv_value.setText(String.format("%s", (int) targetValue) + "");
            gradientseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  tv_value.setText(String.format("%s", progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            tv_notice.setText("饱和度调节仅在彩光模式下有效");
        }
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    public CallBackBean getDatum() {
        String targetValue = "";
        String unit = attributesBean.getSetup().getUnit();
        if ("lm".equalsIgnoreCase(unit)) {
            targetValue = String.valueOf(mAppCompatSeekBar.getProgress());
        } else {
            targetValue = String.valueOf(gradientseekbar.getProgress());
        }


        return new SetupCallBackBean("==", targetValue, attributesBean.getSetup());
    }
}
