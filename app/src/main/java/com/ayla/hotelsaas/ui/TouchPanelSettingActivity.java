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
import com.ayla.hotelsaas.fragment.DeviceListFragment;
import com.ayla.hotelsaas.mvp.present.TourchPanelPresenter;
import com.ayla.hotelsaas.mvp.view.TourchPanelView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import butterknife.BindView;

/**
 * 面板按键设置页面
 * 进入时带入TouchPanelBean "touchpanel"
 * DeviceListBean.DevicesBean "devicesBean"
 */
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

    private final int REQUEST_CODE_SELECT_ICON = 0X12;
    private DeviceListBean.DevicesBean mDevicesBean;
    private TouchPanelBean mTouchPanelBean;

    @Override
    public void refreshUI() {
        super.refreshUI();
        appBar.setCenterText("场景按键设置");
        mTouchPanelBean = (TouchPanelBean) getIntent().getSerializableExtra("touchpanel");
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("devicesBean");
        tv_touchpanel_rename.setText(mTouchPanelBean.getWords());
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
        iv_scene_icon.setImageResource(mTouchPanelBean.getIconRes());
    }

    @Override
    protected void initListener() {
        rl_touchpanel_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getContext(), TouchPanelSceneIconSelectActivity.class);
                int index = 0;
                try {
                    index = Integer.parseInt(mTouchPanelBean.getPictureCode()) - 1;
                } catch (Exception ignored) {
                }
                intent.putExtra("index", index);
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
                                    tv_touchpanel_rename.setText(txt);
                                    if (mDevicesBean != null) {
                                        mPresenter.TourchPanelRenameInsertMethod(mTouchPanelBean.getWordsId(),
                                                mDevicesBean.getDeviceId(), mDevicesBean.getCuId(),
                                                String.valueOf(mTouchPanelBean.getSequence()), "Words", txt,
                                                mDevicesBean.getDeviceCategory());
                                    }
                                    dialog.dismissAllowingStateLoss();
                                }
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
            int index = data.getIntExtra("index", 0);
            iv_scene_icon.setImageResource(DeviceListFragment.drawableIcon[index]);
            mPresenter.TourchPanelRenameInsertMethod(mTouchPanelBean.getPictureCodeId(), mDevicesBean.getDeviceId(),
                    mDevicesBean.getCuId(), String.valueOf(mTouchPanelBean.getSequence()), "PictureCode",
                    String.valueOf(index + 1), mDevicesBean.getDeviceCategory());
        }
    }
}
