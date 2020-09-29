package com.ayla.hotelsaas.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;
import com.ayla.hotelsaas.utils.ImageLoader;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * wifi设备、节点设备 配网引导页面
 * 进入时必须带入int networkType 。
 * networkType == 3、4 时 ，需要带入：网关deviceId 、cuId 、scopeId 、deviceName、deviceCategory、categoryId
 * networkType == 5 时 ，需要带入：scopeId 、deviceName、deviceCategory、categoryId
 */
public class DeviceAddGuideActivity extends BaseMvpActivity<DeviceAddGuideView, DeviceAddGuidePresenter> implements DeviceAddGuideView {
    @BindView(R.id.iv)
    ImageView imageView;
    @BindView(R.id.tv_content)
    TextView textView;
    @BindView(R.id.cb)
    AppCompatCheckBox checkBox;
    @BindView(R.id.bt)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long categoryId = getIntent().getLongExtra("categoryId", 0);
        mPresenter.getNetworkConfigGuide(String.valueOf(categoryId));
    }

    @Override
    protected DeviceAddGuidePresenter initPresenter() {
        return new DeviceAddGuidePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_add_guide;
    }

    @Override
    protected void initView() {
    }

    boolean loadError = false;

    @Override
    protected void initListener() {

    }


    private void handleJump() {
        int networkType = getIntent().getIntExtra("networkType", 0);
        if (networkType == 3) {//艾拉节点设备配网
            Intent mainActivity = new Intent(this, ZigBeeAddActivity.class);
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("networkType", 3);
            startActivityForResult(mainActivity, 0);
        }
        if (networkType == 4) {//鸿雁节点设备配网
            Intent mainActivity = new Intent(this, ZigBeeAddActivity.class);
            mainActivity.putExtras(getIntent());
            mainActivity.putExtra("networkType", 4);
            startActivityForResult(mainActivity, 0);
        } else if (networkType == 5) {//艾拉WiFi设备配网
            Intent mainActivity = new Intent(this, AylaWiFiAddInputActivity.class);
            mainActivity.putExtras(getIntent());
            startActivityForResult(mainActivity, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {//网关绑定页面关闭，告知绑定成功，本引导页面自动关闭。
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void showGuideInfo(String networkGuidePic, String networkGuideDesc) {
        ImageLoader.loadImg(imageView, networkGuidePic, 0, 0);
        textView.setText(networkGuideDesc);
    }

    @OnClick(R.id.bt)
    public void onClicked() {
        if (checkBox.isChecked()) {
            handleJump();
        } else {
            shakeButton();
        }
    }

    private void shakeButton() {
        final AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator animX = ObjectAnimator.ofFloat(button, "translationX", 0, 10, 0, -10);
        animX.setInterpolator(new CycleInterpolator(5));
        animSet.setDuration(200);
        animSet.play(animX);
        animSet.start();
    }
}
