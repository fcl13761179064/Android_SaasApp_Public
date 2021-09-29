package com.ayla.hotelsaas.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.PurposeCategoryBean;
import com.ayla.hotelsaas.databinding.ActivitySwitchUsageSettingBinding;
import com.ayla.hotelsaas.mvp.present.SwitchUsageSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SwitchUsageSettingView;
import com.ayla.hotelsaas.widget.ItemPickerDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.blankj.utilcode.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 开关用途设置
 * 进入需要带上 deviceId  scopeId
 */
public class SwitchUsageSettingActivity extends BaseMvpActivity<SwitchUsageSettingView, SwitchUsageSettingPresenter> implements SwitchUsageSettingView {

    private ActivitySwitchUsageSettingBinding mBinding;

    private int type = 0;//区分是几路开关
    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceId = getIntent().getStringExtra("deviceId");
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(deviceId);
        if (devicesBean == null) {
            return;
        }
        {
            if (devicesBean.getDeviceName().contains("一")) {
                type = 1;
            } else if (devicesBean.getDeviceName().contains("二")) {
                type = 2;
            } else if (devicesBean.getDeviceName().contains("三")) {
                type = 3;
            } else if (devicesBean.getDeviceName().contains("四")) {
                type = 4;
            }
        }
        if (type == 0) {
            return;
        }
        selfNames = new String[type];
        selfPurposeCategories = new PurposeCategoryBean[type];
        mPresenter.getPurposeCategory();
    }

    private String[] selfNames;//设置的名称
    private PurposeCategoryBean[] selfPurposeCategories;

    @Override
    protected SwitchUsageSettingPresenter initPresenter() {
        return new SwitchUsageSettingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_switch_usage_setting;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected View getLayoutView() {
        mBinding = ActivitySwitchUsageSettingBinding.inflate(getLayoutInflater());
        return mBinding.getRoot();
    }

    @Override
    protected void initListener() {

    }

    private static String[] names = new String[]{"①", "②", "③", "④"};

    private void adjustShow(int type, int position) {
        if (position > names.length) {
            return;
        }
        selfPurposeCategories[position - 1] = purposeCategory.get(0);
        mBinding.tvControl.setText(selfPurposeCategories[position - 1].getPurposeName());
        mBinding.tvName.setText(null);
        {
            mBinding.textView.setText("按键" + names[position - 1] + "控制的设备");

            mBinding.button2.setText(position < type ? "下一步" : "保存设置");
            mBinding.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = selfNames[position - 1];
                    if (TextUtils.isEmpty(name)) {
                        CustomToast.makeText(getApplicationContext(), "名称不能为空", R.drawable.ic_toast_warming);
                        return;
                    }
                    for (int i = 0; i < selfNames.length; i++) {
                        if (i < position - 1) {
                            if (TextUtils.equals(name, selfNames[i])) {
                                CustomToast.makeText(getApplicationContext(), "名称重复，请修改", R.drawable.ic_toast_warming);
                                return;
                            }
                        }
                    }

                    if (position < type) {
                        adjustShow(type, position + 1);
                    } else {
                        Log.d(TAG, "onClick: " + selfNames + "  " + selfPurposeCategories);

                        handleSave();
                    }
                }
            });

            mBinding.rlName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValueChangeDialog
                            .newInstance(new ValueChangeDialog.DoneCallback() {
                                @Override
                                public void onDone(DialogFragment dialog, String txt) {
                                    if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                                        CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warming);
                                        return;
                                    } else {
                                        selfNames[position - 1] = txt;
                                        mBinding.tvName.setText(txt);
                                    }
                                    dialog.dismissAllowingStateLoss();
                                }
                            }).setTitle("按键" + names[position - 1] + " 名称")
                            .setMaxLength(10)
                            .setEditValue(selfNames[position - 1])
                            .show(getSupportFragmentManager(), "dialog");
                }
            });

            mBinding.rlControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int defIndex = -1;
                    PurposeCategoryBean selfPurposeCategory = selfPurposeCategories[position - 1];
                    for (int i = 0; i < purposeCategory.size(); i++) {
                        if (purposeCategory.get(i).getId() == selfPurposeCategory.getId()) {
                            defIndex = i;
                        }
                    }

                    ItemPickerDialog.newInstance()
                            .setSubTitle("请选择按键控制的设备")
                            .setTitle("控制设备")
                            .setIconRes(R.drawable.ic_purpose_select)
                            .setData(purposeCategory)
                            .setDefaultIndex(defIndex)
                            .setCallback(new ItemPickerDialog.Callback<PurposeCategoryBean>() {
                                @Override
                                public void onCallback(PurposeCategoryBean object) {
                                    mBinding.tvControl.setText(object.getPurposeName());
                                    selfPurposeCategories[position - 1] = object;
                                }
                            })
                            .show(getSupportFragmentManager(), "dialog");
                }
            });
        }

        int imageId = ResourceUtils.getDrawableIdByName("ic_switch_" + type);
        int imageSolidId = ResourceUtils.getDrawableIdByName("ic_switch_" + type + "_" + position);

        mBinding.imageView.setImageResource(imageId);
        mBinding.imageViewSolid.setImageResource(imageSolidId);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.2f);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.02f, 1, 1.02f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatCount(Animation.INFINITE);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        animationSet.setRepeatMode(Animation.REVERSE);
        animationSet.setDuration(1000);

        mBinding.imageViewSolid.clearAnimation();
        mBinding.imageViewSolid.startAnimation(animationSet);
    }

    private void handleSave() {
        long scopeId = getIntent().getLongExtra("scopeId", 0);
        mPresenter.handleSave(scopeId, deviceId, selfNames, selfPurposeCategories);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Animation animation = mBinding.imageViewSolid.getAnimation();
        if (animation != null && !animation.hasStarted()) {
            animation.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Animation animation = mBinding.imageViewSolid.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    private List<PurposeCategoryBean> purposeCategory = new ArrayList<>();

    @Override
    public void showPurposeCategory(List<PurposeCategoryBean> purposeCategoryBeans) {
        purposeCategory.addAll(purposeCategoryBeans);
        adjustShow(type, 1);
    }

    @Override
    public void saveFailed(Throwable throwable) {
        CustomToast.makeText(getApplicationContext(), "保存失败", R.drawable.ic_toast_warming);
    }

    @Override
    public void renameFail(String throwable) {
        CustomToast.makeText(getApplicationContext(), throwable, R.drawable.ic_toast_warming);
    }

    @Override
    public void saveSuccess() {
        CustomToast.makeText(getApplicationContext(), "保存成功", R.drawable.ic_toast_warming);
        setResult(RESULT_OK);
        finish();
    }
}
