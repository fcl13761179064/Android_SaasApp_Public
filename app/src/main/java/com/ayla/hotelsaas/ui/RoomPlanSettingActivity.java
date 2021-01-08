package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;

import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanSettingBinding;
import com.ayla.hotelsaas.mvp.present.RoomPlanSettingPresenter;
import com.ayla.hotelsaas.mvp.view.RoomPlanSettingView;
import com.ayla.hotelsaas.widget.ShareRoomPlanDialog;

/**
 * removeEnable ,标记是否支持删除
 */
public class RoomPlanSettingActivity extends BaseMvpActivity<RoomPlanSettingView, RoomPlanSettingPresenter> implements RoomPlanSettingView {

    ActivityRoomPlanSettingBinding binding;

    @Override
    protected RoomPlanSettingPresenter initPresenter() {
        return new RoomPlanSettingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected View getLayoutView() {
        binding = ActivityRoomPlanSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomPlanSettingActivity.this, RoomPlanApplyActivity.class);
                startActivity(intent);
            }
        });
        binding.constraintLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShareRoomPlanDialog().show(getSupportFragmentManager(), null);
            }
        });
    }
}
