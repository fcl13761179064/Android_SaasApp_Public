package com.ayla.hotelsaas.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.RoomTypeShowBean;
import com.ayla.hotelsaas.data.net.ServerBadException;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanApplyBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.mvp.present.RoomPlanApplyPresenter;
import com.ayla.hotelsaas.mvp.view.RoomPlanApplyView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * removeEnable ,标记是否支持删除
 */
public class RoomPlanApplyActivity extends BaseMvpActivity<RoomPlanApplyView, RoomPlanApplyPresenter> implements RoomPlanApplyView {

    ActivityRoomPlanApplyBinding binding;

    long scopeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scopeId = getIntent().getLongExtra("roomId", 0);
        mPresenter.showRoomType(scopeId);
    }

    @Override
    protected RoomPlanApplyPresenter initPresenter() {
        return new RoomPlanApplyPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRoomPlanApplyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {
        int roomTypeId = getIntent().getIntExtra("roomTypeId", 0);
        /*if (roomTypeId == 1) {
            binding.tvRoomName.setText("默认房型");
        }*/
    }

    @Override
    protected void initListener() {
        binding.textView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String planLinkage = getPlanLinkage();
                if (!TextUtils.isEmpty(planLinkage)) {
                    binding.editText.setText(planLinkage);
                }
            }
        });
    }

    @Override
    protected void appBarRightTvClicked() {
        String linkage = binding.editText.getText().toString();
        if (TextUtils.isEmpty(linkage)) {
            CustomToast.makeText(this, "请输入链接", R.drawable.ic_toast_warming);
        } else {
            mPresenter.applyPlan(scopeId, linkage);
        }
    }

    private String getPlanLinkage() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = cm.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            return clipData.getItemAt(0).getText().toString();
        }
        return null;
    }

    @Override
    public void importPlanSuccess() {
        CustomToast.makeText(this, "导入成功", R.drawable.ic_success);
        EventBus.getDefault().post(new DeviceAddEvent());
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void importPlanFailed(Throwable throwable) {
        if (throwable instanceof ServerBadException) {
            if (TextUtils.equals(((ServerBadException) throwable).getCode(), "140002")) {
                CustomAlarmDialog.newInstance().setTitle("点位名称重复")
                        .setContent(String.format("当前房间设备点位名称与方案点位名称【%s】重复，请修改后重试。", ((ServerBadException) throwable).getMsg()))
                        .setStyle(CustomAlarmDialog.Style.STYLE_SINGLE_BUTTON)
                        .setDoneCallback(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {

                            }
                        })
                        .show(getSupportFragmentManager(), "dialog");
                return;
            }
        }
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @Override
    public void showRoomTypeSuccess(RoomTypeShowBean bean) {
        binding.tvRoomName.setText(bean.getTypeName());
    }
}
