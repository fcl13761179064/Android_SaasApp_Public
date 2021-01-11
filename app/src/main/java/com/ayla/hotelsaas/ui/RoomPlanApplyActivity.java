package com.ayla.hotelsaas.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanApplyBinding;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.mvp.present.RoomPlanApplyPresenter;
import com.ayla.hotelsaas.mvp.view.RoomPlanApplyView;

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
        scopeId = getIntent().getLongExtra("scopeId", 0);
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
            CustomToast.makeText("请输入链接", R.drawable.ic_toast_warming);
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
        CustomToast.makeText("导入成功", R.drawable.ic_success);
        EventBus.getDefault().post(new DeviceAddEvent());
        setResult(RESULT_OK);
        finish();
    }
}
