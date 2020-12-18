package com.ayla.hotelsaas.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpFragment;
import com.ayla.hotelsaas.base.BasePresenter;
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.xw.repo.BubbleSeekBar;

import butterknife.BindView;

/**
 * 场景创建，选择执行功能点的页面，范围选择情况
 */
public class SceneSettingFunctionDatumSetRangeFragment extends BaseMvpFragment implements ISceneSettingFunctionDatumSet {
    @BindView(R.id.sb_value)
    BubbleSeekBar mAppCompatSeekBar;
    @BindView(R.id.iv_addition)
    ImageView mAdditionImageView;
    @BindView(R.id.iv_subtract)
    ImageView mSubtractImageView;
    @BindView(R.id.tv_value)
    TextView mValueTextView;

    public static SceneSettingFunctionDatumSetRangeFragment newInstance(DeviceTemplateBean.AttributesBean attributesBean) {

        Bundle args = new Bundle();
        args.putSerializable("attributeBean", attributesBean);
        SceneSettingFunctionDatumSetRangeFragment fragment = new SceneSettingFunctionDatumSetRangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_scene_function_datum_set_range;
    }

    private DeviceTemplateBean.AttributesBean attributesBean;

    @Override
    protected void initView(View view) {
        attributesBean = (DeviceTemplateBean.AttributesBean) getArguments().getSerializable("attributeBean");

        Double max = attributesBean.getSetup().getMax();
        Double min = attributesBean.getSetup().getMin();
        Double step = attributesBean.getSetup().getStep();
        String unit = attributesBean.getSetup().getUnit();
        mAppCompatSeekBar.getConfigBuilder()
                .min(min.floatValue())
                .max(max.floatValue())
                .sectionCount(Double.valueOf(((max - min) / step)).intValue())
                .sectionTextColor(Color.parseColor("#648C1A"))
                .trackColor(Color.parseColor("#DFE4EB"))
                .build();
        mAppCompatSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                mValueTextView.setText(String.format("%s%s", progress, TextUtils.isEmpty(unit) ? "" : unit));
            }
        });

        boolean autoJump = getActivity().getIntent().getBooleanExtra("autoJump", false);
        BaseSceneBean.Action action = (BaseSceneBean.Action) getActivity().getIntent().getSerializableExtra("action");
        float targetValue = Double.valueOf((max + min) / 2).floatValue();
        if (autoJump && action != null) {
            String rightValue = action.getRightValue();
            try {
                targetValue = Float.parseFloat(rightValue);
            } catch (Exception ignored) {
            }
        }
        mAppCompatSeekBar.setProgress(targetValue);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {

    }

    @Override
    public CallBackBean getDatum() {
        String targetValue = String.valueOf(mAppCompatSeekBar.getProgress());

        return new SetupCallBackBean("==", targetValue, attributesBean.getSetup());
    }
}
