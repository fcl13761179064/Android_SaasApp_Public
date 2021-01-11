package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.databinding.ActivityRoomPlanSettingBinding;
import com.ayla.hotelsaas.mvp.present.RoomPlanSettingPresenter;
import com.ayla.hotelsaas.mvp.view.RoomPlanSettingView;
import com.ayla.hotelsaas.widget.ShareRoomPlanDialog;

/**
 * removeEnable ,标记是否支持删除
 * 参数
 * long scopeId
 * boolean hasPlan
 */
public class RoomPlanSettingActivity extends BaseMvpActivity<RoomPlanSettingView, RoomPlanSettingPresenter> implements RoomPlanSettingView {
    private final int REQUEST_CODE_ROOM_PLAN_SETTING = 0X10;

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
        boolean hasPlan = getIntent().getBooleanExtra("hasPlan", false);
        switchPlanState(hasPlan);
    }

    @Override
    protected void initListener() {
        long scopeId = getIntent().getLongExtra("scopeId", 0);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomPlanSettingActivity.this, RoomPlanApplyActivity.class);
                intent.putExtra("scopeId", scopeId);
                startActivityForResult(intent, REQUEST_CODE_ROOM_PLAN_SETTING);
            }
        });
        binding.constraintLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.exportPlan(scopeId);
            }
        });
    }

    @Override
    public void planExportSuccess(String s) {
        ShareRoomPlanDialog.newInstance(s).show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROOM_PLAN_SETTING && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void switchPlanState(boolean hasPlan) {
        if (hasPlan) {
            binding.imageView4.setImageResource(R.drawable.iv_room_plan_1);
            binding.textView5.setText("已使用");
            binding.button.setText("更换方案");
        } else {
            binding.imageView4.setImageResource(R.drawable.iv_room_plan_2);
            binding.textView5.setText("未使用");
            binding.button.setText("添加方案");
        }
    }
}
