package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.mvp.present.A2DeviceBindPresenter;
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.A2DeviceBindView;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * wifi设备、节点设备 配网引导页面
 * 进入必须带上{@link Bundle addInfo}
 */
public class A2GatewayAddStatusActivity extends BaseMvpActivity<A2DeviceBindView, A2DeviceBindPresenter> implements A2DeviceBindView {
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
    @BindView(R.id.tv_gateway_bind_status)
    TextView tv_gateway_bind_status;

    private static final int LiNE_NET = 0;
    private static final int AP_NET = 1;
    private Bundle addInfo;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addInfo = getIntent().getBundleExtra("addInfo");
        String pid = addInfo.getString("pid");
        deviceId = addInfo.getString("deviceId");
        mPresenter.getNetworkConfigGuide(pid);
        mPresenter.getA2BindInfo(deviceId);
        gataway_name.setText(deviceId);
    }

    @Override
    protected A2DeviceBindPresenter initPresenter() {
        return new A2DeviceBindPresenter();
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
        Intent mainActivity = new Intent(this, ApWifiDistributeActivity.class);
        mainActivity.putExtras(getIntent());
        mainActivity.putExtra("deviceId", deviceId);
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
    public void getBindInfoSuccess(A2BindInfoBean o) {
        if (o.getBindStatus()) {
            tv_gateway_bind_status.setVisibility(View.VISIBLE);
        } else {
            tv_gateway_bind_status.setVisibility(View.GONE);
        }

    }

    @Override
    public void getBindInfoFail(String o) {

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
