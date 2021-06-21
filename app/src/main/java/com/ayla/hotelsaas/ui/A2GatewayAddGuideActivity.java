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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.aliyun.iot.aep.sdk.IoTSmart;
import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.Constance;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.A2BindInfoBean;
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
public class A2GatewayAddGuideActivity extends BaseMvpActivity<DeviceAddGuideView, DeviceAddGuidePresenter> implements DeviceAddGuideView {
    private final int REQUEST_CODE_FOR_DSN_INPUT = 0X11;
    private final int REQUEST_CODE_FOR_DSN_SCAN = 0X12;

    @BindView(R.id.iv)
    ImageView imageView;
    @BindView(R.id.tv_content)
    TextView textView;
    @BindView(R.id.cb)
    AppCompatCheckBox checkBox;
    @BindView(R.id.bt)
    Button button;
    @BindView(R.id.appBar)
    AppBar appBar;

    private static final int LiNE_NET = 0;
    private static final int AP_NET = 1;
    private Bundle addInfo;
    private View viewById;
    private TextView tv_right;

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
        return R.layout.activity_a2_gataway_add_guide;
    }

    @Override
    protected void initView() {
    }


    @Override
    protected void initListener() {
        viewById = appBar.findViewById(R.id.iv_right);
        tv_right = appBar.findViewById(R.id.tv_right);
        String ap_type = SharePreferenceUtils.getString(getContext(), Constance.AP_NET_SELECT, "1");
        if ("1".equalsIgnoreCase(ap_type)) {
            tv_right.setText("网线配网");
        } else {
            tv_right.setText("AP 配网");
        }

        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick(v);
            }
        });
    }


    //菜单按钮onClick事件
    public void menuClick(View view) {
        final List<String> items = new ArrayList<>();
        items.add("网线配网");
        items.add("AP 配网");
        String ap_choose = SharePreferenceUtils.getString(getContext(), Constance.AP_NET_SELECT, "1");
        final ApNetpopupWindowUtil popupWindow = new ApNetpopupWindowUtil(this, items, ap_choose);
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                switch ((int) id) {
                    case LiNE_NET:
                        SharePreferenceUtils.saveString(getContext(), Constance.AP_NET_SELECT, "1");
                        tv_right.setText("网线配网");
                        break;
                    case AP_NET:
                        tv_right.setText("Ap 配网");
                        SharePreferenceUtils.saveString(getContext(), Constance.AP_NET_SELECT, "2");
                        break;
                }

            }
        });
        popupWindow.setOff(-30, 0);
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 300);
    }

    private void handleJump() {
        Intent mainActivity = new Intent(this, ScanActivity.class);
        startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_FOR_DSN_SCAN || requestCode == REQUEST_CODE_FOR_DSN_INPUT) && resultCode == RESULT_OK) {//获取到了DSN
            if (data != null) {
                String deviceId = data.getStringExtra("result").trim();
                if (!TextUtils.isEmpty(deviceId)) {
                    if (deviceId.startsWith("Lark_DSN:") && deviceId.endsWith("##") && requestCode == REQUEST_CODE_FOR_DSN_SCAN) {
                        deviceId = deviceId.substring(9, deviceId.length() - 2).trim();
                        checkRelue(deviceId);
                    } else if (requestCode == REQUEST_CODE_FOR_DSN_INPUT) {
                        checkRelue(deviceId);
                    } else {
                        CustomToast.makeText(this, "无效的设备ID号", R.drawable.ic_toast_warming);
                    }
                }
            } else {
                CustomToast.makeText(this, "无效的设备ID号", R.drawable.ic_toast_warming);
            }
        } else if (requestCode == REQUEST_CODE_FOR_DSN_SCAN && resultCode == ScanActivity.RESULT_FOR_INPUT) {//扫码页面回退到手动输入页面
            Intent mainActivity = new Intent(this, GatewayAddDsnInputActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_INPUT);
        }
    }


    public void checkRelue(String deviceId) {
        if (!TextUtils.isEmpty(deviceId)) {
            String type = SharePreferenceUtils.getString(getContext(), Constance.AP_NET_SELECT, "1");
            if ("1".equalsIgnoreCase(type)) {
                Intent mainActivity = new Intent(this, DeviceAddActivity.class);
                Bundle addInfo = getIntent().getBundleExtra("addInfo");
                addInfo.putString("deviceId", deviceId);
                mainActivity.putExtra("addInfo", addInfo);
                startActivity(mainActivity);
            } else {
                Intent mainActivity = new Intent(this, A2GatewayAddStatusActivity.class);
                Bundle addInfo = getIntent().getBundleExtra("addInfo");
                addInfo.putString("deviceId", deviceId);
                mainActivity.putExtra("addInfo", addInfo);
                startActivity(mainActivity);
            }
            return;
        }
    }



    @Override
    public void getGuideInfoSuccess(NetworkConfigGuideBean o) {
        if (o != null) {
            String guidePic = o.getNetworkGuidePic();
            String guideDesc = o.getNetworkGuideDesc();

            ImageLoader.loadImg(imageView, guidePic, 0, 0);
            textView.setText(guideDesc);
        }
    }


    @Override
    public void getGuideInfoFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
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
