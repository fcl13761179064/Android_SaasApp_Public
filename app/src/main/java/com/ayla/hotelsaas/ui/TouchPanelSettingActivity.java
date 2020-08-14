package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.TouchPanelBean;
import com.ayla.hotelsaas.bean.TouchPanelDataBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.present.TourchPanelPresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.mvp.view.TourchPanelView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;

public class TouchPanelSettingActivity extends BaseMvpActivity<TourchPanelView, TourchPanelPresenter> implements TourchPanelView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_touchpanel_rename)
    RelativeLayout rl_touchpanel_rename;
    @BindView(R.id.rl_touchpanel_icon)
    RelativeLayout rl_touchpanel_icon;
    @BindView(R.id.tv_touchpanel_rename)
    TextView tv_touchpanel_rename;
    @BindView(R.id.iv_scene_icon)
    ImageView iv_scene_icon;

    private String mPosition;

    private final int REQUEST_CODE_SELECT_ICON = 0X12;
    private DeviceListBean.DevicesBean mDevicesBean;
    private String mTouch_position;
    private String mString;
    private int mId;

    @Override
    public void refreshUI() {
        appBar.setCenterText("场景按键设置");
        super.refreshUI();
        mPosition = getIntent().getStringExtra("position");
        mId = getIntent().getIntExtra("id", 0);
        final int[] drawableIcon = TouchPanelSelectActivity.drawableIcon;
        final int drawable_icon = drawableIcon[Integer.parseInt(mPosition)];
        iv_scene_icon.setImageResource(drawable_icon);
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        tv_touchpanel_rename.setText("场景" + mPosition);
    }


    @Override
    protected TourchPanelPresenter initPresenter() {
        return new TourchPanelPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.scene_touchpanel_setting;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
        rl_touchpanel_icon.setOnClickListener(new View.OnClickListener() {

            private int mIndex;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), TouchPanelSceneIconSelectActivity.class);
                try {
                    mIndex = Integer.parseInt(mPosition);
                } catch (Exception ignore) {
                }
                intent.putExtra("index", mIndex);
                startActivityForResult(intent, REQUEST_CODE_SELECT_ICON);

            }
        });

        rl_touchpanel_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt)) {
                                    CustomToast.makeText(getBaseContext(), "修改场景名称不能为空", R.drawable.ic_toast_warming).show();
                                } else {
                                    mString = txt;
                                    tv_touchpanel_rename.setHint(txt);
                                    if (mDevicesBean != null) {
                                        mPresenter.TourchPanelRenameInsertMethod(mId, mDevicesBean.getDeviceId(), mDevicesBean.getCuId(), mPosition, "Words", txt);
                                    }
                                }
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setEditValue(tv_touchpanel_rename.getText().toString())
                        .setTitle("修改名称")
                        .setEditHint("请输入名称")
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "scene_name");
            }
        });
    }

    @Override
    public void operateError(String msg) {
        CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void operateSuccess(Boolean is_rename) {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_ICON && resultCode == RESULT_OK) {//选择ICON返回结果
            int index = data.getIntExtra("index", 1);
            mPresenter.TourchPanelRenameInsertMethod(mId, mDevicesBean.getDeviceId(), mDevicesBean.getCuId(), mPosition, "icon", index + "");
            //iv_scene_icon.setImageResource();
        }
    }
}
