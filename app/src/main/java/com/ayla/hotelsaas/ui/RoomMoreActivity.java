package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RoomOrderBean;
import com.ayla.hotelsaas.mvp.present.DeviceMorePresenter;
import com.ayla.hotelsaas.mvp.present.RoomMorePresenter;
import com.ayla.hotelsaas.mvp.view.DeviceMoreView;
import com.ayla.hotelsaas.mvp.view.RoomMoreView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.ValueChangeDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class RoomMoreActivity extends BaseMvpActivity<RoomMoreView, RoomMorePresenter> implements RoomMoreView {

    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.rl_room_rename)
    RelativeLayout rl_room_rename;
    @BindView(R.id.tv_room_name)
    TextView tv_room_name;
    @BindView(R.id.btn_remove_room)
    Button btn_remove_room;
    private long mRoom_ID;
    private String mRoom_name;

    @Override
    public void refreshUI() {
        appBar.setCenterText("更多");
        super.refreshUI();
    }

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
        mRoom_ID =  getIntent().getLongExtra("roomId",0);
        mRoom_name =  getIntent().getStringExtra("roomName");
        tv_room_name.setText(mRoom_name);
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
                        .setTitle(getResources().getString(R.string.remove_device_title))
                        .setContent(getResources().getString(R.string.remove_device_content))
                        .show(getSupportFragmentManager(), "");
            }
        });

        rl_room_rename.setOnClickListener(new View.OnClickListener() {
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
                                    CustomToast.makeText(getBaseContext(), "修改房间名不能为空", R.drawable.ic_toast_warming).show();
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
    }

    @Override
    public void operateError(String msg) {
        CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void operateSuccess(String is_rename) {
        if ("true".equals(is_rename)) {
            CustomToast.makeText(this, "修改成功", R.drawable.ic_toast_success).show();
            setResult(RESULT_OK);
        }
    }

    @Override
    public void operateRemoveSuccess(String is_rename) {
        if ("true".equals(is_rename))
        CustomToast.makeText(this, "移除成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
    }

    @Override
    public void operateMoveFailSuccess(String code, String msg) {
        CustomToast.makeText(this, "移除失败", R.drawable.ic_toast_warming).show();
    }
}
