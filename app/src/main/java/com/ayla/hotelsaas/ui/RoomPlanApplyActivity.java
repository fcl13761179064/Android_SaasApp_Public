package com.ayla.hotelsaas.ui;

import android.view.View;

import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanApplyBinding;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanSettingBinding;
import com.ayla.hotelsaas.mvp.present.RoomPlanApplyPresenter;
import com.ayla.hotelsaas.mvp.present.RoomPlanSettingPresenter;
import com.ayla.hotelsaas.mvp.view.RoomPlanApplyView;
import com.ayla.hotelsaas.mvp.view.RoomPlanSettingView;

/**
 * removeEnable ,标记是否支持删除
 */
public class RoomPlanApplyActivity extends BaseMvpActivity<RoomPlanApplyView, RoomPlanApplyPresenter> implements RoomPlanApplyView {

    ActivityRoomPlanApplyBinding binding;

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

    }
}
