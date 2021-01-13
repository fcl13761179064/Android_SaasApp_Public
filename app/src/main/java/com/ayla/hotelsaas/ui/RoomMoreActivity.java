package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.events.RoomChangedEvent;
import com.ayla.hotelsaas.mvp.present.RoomMorePresenter;
import com.ayla.hotelsaas.mvp.view.RoomMoreView;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * removeEnable ,标记是否支持删除
 */
public class RoomMoreActivity extends BaseMvpActivity<RoomMoreView, RoomMorePresenter> implements RoomMoreView {
    public static final int RESULT_CODE_REMOVED = 0X10;
    public static final int RESULT_CODE_RENAMED = 0X11;
    private final int REQUEST_CODE_DISTRIBUTION_ROOM = 0x12;
    private final int REQUEST_CODE_ROOM_PLAN_SETTING = 0x13;

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_room_rename)
    RelativeLayout rl_room_rename;
    @BindView(R.id.rl_room_distribution)
    RelativeLayout rl_room_distribution;
    @BindView(R.id.tv_room_name)
    TextView tv_room_name;
    @BindView(R.id.btn_remove_room)
    Button btn_remove_room;
    private long mRoom_ID;
    private String mRoom_name;


    @Override
    protected RoomMorePresenter initPresenter() {
        return new RoomMorePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.room_more_activity;
    }

    @Override
    protected void initView() {
        appBar.setCenterText("更多");

        mRoom_ID = getIntent().getLongExtra("roomId", 0);
        mRoom_name = getIntent().getStringExtra("roomName");
        tv_room_name.setText(mRoom_name);
        boolean removeEnable = getIntent().getBooleanExtra("removeEnable", false);
        btn_remove_room.setVisibility(removeEnable ? View.VISIBLE : View.GONE);
        //暂时没有这个需求，屏蔽入口
//        rl_room_distribution.setVisibility(removeEnable ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initListener() {

        btn_remove_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlarmDialog
                        .newInstance(new CustomAlarmDialog.Callback() {
                            @Override
                            public void onDone(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                                if (mRoom_ID != 0) {
                                    mPresenter.deleteRoomNum(mRoom_ID);
                                }
                            }

                            @Override
                            public void onCancel(CustomAlarmDialog dialog) {
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setContent("是否删除当前房间，设备与联动关系")
                        .show(getSupportFragmentManager(), "");
            }
        });

        rl_room_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "修改房间名不能为空", R.drawable.ic_toast_warming);
                                    return;
                                } else {
                                    tv_room_name.setText(txt);
                                    if (mRoom_ID != 0) {
                                        mPresenter.roomRename(mRoom_ID, txt);
                                    }
                                }
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setEditValue(tv_room_name.getText().toString())
                        .setTitle("修改名称")
                        .setEditHint("请输入房间名称")
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "scene_name");
            }
        });

        rl_room_distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomMoreActivity.this, DistributionHotelSelectActivity.class);
                startActivityForResult(intent, REQUEST_CODE_DISTRIBUTION_ROOM);
            }
        });
    }

    @Override
    public void renameFailed(String msg) {
    }

    @Override
    public void renameSuccess(String newName) {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        setResult(RESULT_CODE_RENAMED, new Intent().putExtra("newName", newName));
        EventBus.getDefault().post(new RoomChangedEvent(mRoom_ID, newName));
    }

    @Override
    public void removeSuccess(String is_rename) {
        CustomToast.makeText(this, "移除成功", R.drawable.ic_success);
        Intent mainActivity = new Intent(this, ProjectListActivity.class);
        startActivity(mainActivity);
        setResult(RESULT_CODE_REMOVED);
        finish();
    }

    @Override
    public void removeFailed(String code, String msg) {
    }

    @Override
    public void planCheckResult(boolean s) {
        Intent intent = new Intent(RoomMoreActivity.this, RoomPlanSettingActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra("hasPlan", s);
        startActivityForResult(intent, REQUEST_CODE_ROOM_PLAN_SETTING);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DISTRIBUTION_ROOM && resultCode == RESULT_OK) {
            Intent mainActivity = new Intent(this, ProjectListActivity.class);
            startActivity(mainActivity);
            finish();
        } else if (requestCode == REQUEST_CODE_ROOM_PLAN_SETTING && resultCode == RESULT_OK) {
            finish();
        }
    }

    @OnClick(R.id.rl_room_plan)
    public void handleRoomPlanJump() {
        mPresenter.checkPlan(mRoom_ID);
    }
}
