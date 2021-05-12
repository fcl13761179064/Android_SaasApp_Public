package com.ayla.hotelsaas.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;
import com.ayla.hotelsaas.popmenu.PopupWindowUtil;
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
public class DeviceAddGuideActivity extends BaseMvpActivity<DeviceAddGuideView, DeviceAddGuidePresenter> implements DeviceAddGuideView {
    private final int REQUEST_CODE_GET_WIFI_SSID_PWD = 0X10;

    @BindView(R.id.iv)
    ImageView imageView;
    @BindView(R.id.tv_content)
    TextView textView;
    @BindView(R.id.cb)
    AppCompatCheckBox checkBox;
    @BindView(R.id.bt)
    Button button;
    @BindView(R.id.right_ll)
    LinearLayout right_ll;
    private static final int USER_SEARCH = 0;
    private static final int USER_ADD = 1;
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
        return R.layout.activity_device_add_guide;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        right_ll.setOnClickListener(new View.OnClickListener() {
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
        items.add("AP配网");
        final PopupWindowUtil popupWindow = new PopupWindowUtil(this ,items);
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                String title_type = SharePreferenceUtils.getString(getContext(), Constance.SP_SAAS, "1");
                switch ((int) id) {
                    case USER_SEARCH:
                        if ("1".equals(title_type)) {
                            return;
                        }

                        break;
                    case USER_ADD:

                        break;
                }

            }
        });
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 2);
    }

    private void handleJump() {
        int networkType = addInfo.getInt("networkType");

        if (networkType == 3) {//艾拉节点设备配网
            Intent mainActivity = new Intent(this, DeviceAddActivity.class);
            mainActivity.putExtra("addInfo", addInfo);
            startActivity(mainActivity);
        } else if (networkType == 4) {//鸿雁节点设备配网
            Intent mainActivity = new Intent(this, DeviceAddActivity.class);
            mainActivity.putExtra("addInfo", addInfo);
            startActivity(mainActivity);
        } else if (networkType == 5) {//艾拉WiFi设备配网
            Intent mainActivity = new Intent(this, AylaWiFiAddInputActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_GET_WIFI_SSID_PWD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_WIFI_SSID_PWD && resultCode == RESULT_OK) {//获取到了输入的wifi ssid 、pwd
            Intent mainActivity = new Intent(this, DeviceAddActivity.class);
            addInfo.putString("wifiName", data.getStringExtra("wifiName"));
            addInfo.putString("wifiPassword", data.getStringExtra("wifiPassword"));
            mainActivity.putExtra("addInfo", addInfo);
            startActivity(mainActivity);
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
        String title_type = SharePreferenceUtils.getString(this, Constance.SP_SAAS, "1");
        if (Constance.isNetworkDebug()) {//这个判断是dev，qa环境
            if ("1".equalsIgnoreCase(title_type)) {
                IoTSmart.setAuthCode("dev_saas");
            } else {
                IoTSmart.setAuthCode("dev_miya");
            }
            IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
        } else {//这个是prod环境
            if ("1".equalsIgnoreCase(title_type)) {
                IoTSmart.setAuthCode("pord_saas");
            } else {
                IoTSmart.setAuthCode("china_production");
            }
        }
        IoTSmart.init(MyApplication.getInstance(), new IoTSmart.InitConfig().setDebug(Constance.isNetworkDebug()));
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
