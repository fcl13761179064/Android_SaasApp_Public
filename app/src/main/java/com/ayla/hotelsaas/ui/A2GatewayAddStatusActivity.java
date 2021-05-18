package com.ayla.hotelsaas.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;
import com.ayla.hotelsaas.popmenu.ApNetpopupWindowUtil;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ayla.hotelsaas.application.MyApplication.getContext;

/**
 * wifi设备、节点设备 配网引导页面
 * 进入必须带上{@link Bundle addInfo}
 */
public class A2GatewayAddStatusActivity extends BaseMvpActivity<DeviceAddGuideView, DeviceAddGuidePresenter> implements DeviceAddGuideView {
    private final int REQUEST_CODE_FOR_DSN_INPUT = 0X11;
    private final int REQUEST_CODE_FOR_DSN_SCAN = 0X12;

    @BindView(R.id.iv)
    ImageView imageView;
    @BindView(R.id.gataway_name)
    TextView gataway_name;
    @BindView(R.id.bt)
    Button button;
    @BindView(R.id.appBar)
    AppBar appBar;

    private static final int LiNE_NET = 0;
    private static final int AP_NET = 1;
    private Bundle addInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addInfo = getIntent().getBundleExtra("addInfo");
        String pid = addInfo.getString("pid");
        mPresenter.getNetworkConfigGuide(pid);
    }

    @Override
    protected DeviceAddGuidePresenter initPresenter() {
        return new DeviceAddGuidePresenter();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_a2_gataway_add_status;
    }

    @Override
    protected void initView() {
    }


    @Override
    protected void initListener() {
    }


    private void handleJump() {
        Intent mainActivity = new Intent(this, ApDistributeActivity.class);
        startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
    }

    @Override
    public void getGuideInfoSuccess(NetworkConfigGuideBean o) {
        if (o != null) {
            String guidePic = o.getNetworkGuidePic();
            String guideDesc = o.getNetworkGuideDesc();
            ImageLoader.loadImg(imageView, guidePic, 0, 0);
        }
    }

    @Override
    public void getGuideInfoFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @OnClick(R.id.bt)
    public void onClicked() {
        handleJump();
    }

}
