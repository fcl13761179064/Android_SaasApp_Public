package com.ayla.hotelsaas.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.constant.ConstantValue;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.NetworkConfigGuideBean;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.mvp.present.DeviceAddGuidePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceAddGuideView;
import com.ayla.hotelsaas.widget.popmenu.ApNetpopupWindowUtil;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.ImageLoader;
import com.ayla.hotelsaas.utils.SharePreferenceUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private ImageView iv_right;
    private String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharePreferenceUtils.saveString(getContext(), ConstantValue.AP_NET_SELECT, "1");
        addInfo = getIntent().getBundleExtra("addInfo");
        pid = addInfo.getString("pid");
        mPresenter.getNetworkConfigGuide(pid);
        if ("ZBGW0-A000003".equals(pid)) {
            tv_right.setText("");
            iv_right.setImageResource(0);
        } else {
            tv_right.setText("网线配网");
        }
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
        iv_right = appBar.findViewById(R.id.iv_right);
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
        String ap_choose = SharePreferenceUtils.getString(getContext(), ConstantValue.AP_NET_SELECT, "1");
        final ApNetpopupWindowUtil popupWindow = new ApNetpopupWindowUtil(this, items, ap_choose);
        popupWindow.setItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindow.dismiss();
                switch ((int) id) {
                    case LiNE_NET:
                        SharePreferenceUtils.saveString(getContext(), ConstantValue.AP_NET_SELECT, "1");
                        tv_right.setText("网线配网");
                        break;
                    case AP_NET:
                        tv_right.setText("Ap 配网");
                        SharePreferenceUtils.saveString(getContext(), ConstantValue.AP_NET_SELECT, "2");
                        break;
                }

            }
        });
        popupWindow.setOff(-30, 0);
        //根据后面的数字 手动调节窗口的宽度
        popupWindow.show(view, 300);
    }

    private void handleJump() {
        if ("ZBGW0-A000003".equals(pid)) {
            Intent mainActivity = new Intent(this, ScanA6Activity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
        } else {
            Intent mainActivity = new Intent(this, ScanActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_SCAN);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_FOR_DSN_SCAN || requestCode == REQUEST_CODE_FOR_DSN_INPUT) && resultCode == RESULT_OK) {//获取到了DSN
            if (data != null) {
                String deviceId = data.getStringExtra("result").trim();
                if (!TextUtils.isEmpty(deviceId)) {
                    if (deviceId.startsWith("Lark_DSN:") && deviceId.endsWith("##")) {
                        deviceId = deviceId.substring(9, deviceId.length() - 2).trim();
                    }
                }
                checkRelue(deviceId);
            }
        } else if (requestCode == REQUEST_CODE_FOR_DSN_SCAN && resultCode == ScanActivity.RESULT_FOR_INPUT) {//扫码页面回退到手动输入页面
            Intent mainActivity = new Intent(this, GatewayAddDsnInputActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_FOR_DSN_INPUT);
        }
    }


    public void checkRelue(String deviceId) {
        ArrayList arrayList = new ArrayList();
        arrayList.add("deviceId");
        arrayList.add("regToken");
        arrayList.add("tempToken");
        if (deviceId.startsWith("http") && Uri.parse(deviceId).getQueryParameterNames().containsAll(arrayList)) {
            Intent intent = new Intent(this, DeviceAddActivity.class);
            Bundle bundle = new Bundle();
            Uri uri = Uri.parse(deviceId);
            Set<String> paramNames = uri.getQueryParameterNames();
            for (String paramName : paramNames) {
                bundle.putString(paramName, new String(Base64.decode(uri.getQueryParameter(paramName), Base64.NO_WRAP)));
            }
            String device_Id = (String) bundle.get("deviceId");
            String regToken = (String) bundle.get("regToken");
            String tempToken = (String) bundle.get("tempToken");
            intent.putExtra(KEYS.CONFIG_MODE, KEYS.A6_CONFIG_MODE);
            Intent mainActivity = new Intent(this, DeviceAddActivity.class);
            Bundle addInfo = getIntent().getBundleExtra("addInfo");
            addInfo.putString("deviceId", device_Id);
            mainActivity.putExtra("addInfo", addInfo);
            mainActivity.putExtra("regToken", regToken);
            mainActivity.putExtra("tempToken", tempToken);
            startActivity(mainActivity);
        } else if (!TextUtils.isEmpty(deviceId)) {
            String type = SharePreferenceUtils.getString(getContext(), ConstantValue.AP_NET_SELECT, "1");
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
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warning);
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
