package com.ayla.hotelsaas.ui;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.FunctionRenameListAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.events.DeviceAddEvent;
import com.ayla.hotelsaas.events.DeviceChangedEvent;
import com.ayla.hotelsaas.events.SwitchRenameEvent;
import com.ayla.hotelsaas.mvp.present.FunctionRenamePresenter;
import com.ayla.hotelsaas.mvp.view.FunctionRenameView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 功能重命名页面
 * 进入必须带上 deviceId
 */
public class FunctionRenameActivity extends BaseMvpActivity<FunctionRenameView, FunctionRenamePresenter> implements FunctionRenameView {
    @BindView(R.id.rl)
    RecyclerView mRecyclerView;
    private FunctionRenameListAdapter mAdapter;
    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevicesBean = MyApplication.getInstance().getDevicesBean(getIntent().getStringExtra("deviceId"));
        loadData();
    }

    @Override
    protected FunctionRenamePresenter initPresenter() {
        return new FunctionRenamePresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_function_rename;
    }

    @Override
    protected void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mAdapter = new FunctionRenameListAdapter(R.layout.item_function_rename_list);
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                FunctionRenameListAdapter.Bean attributesBean = mAdapter.getItem(position);
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                                    CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warming);
                                    return;
                                } else {
                                    if (mDevicesBean != null) {
                                        for (int i = 0; i < mAdapter.getData().size(); i++) {
                                            FunctionRenameListAdapter.Bean item = mAdapter.getItem(i);
                                            if (i == position) {
                                                continue;
                                            }
                                            if (TextUtils.equals(item.getPropertyNickname(), txt) || TextUtils.equals(item.getPropertyName(), txt)) {
                                                CustomToast.makeText(getBaseContext(), "不能和其他开关重名", R.drawable.ic_toast_warming);
                                                return;
                                            }
                                        }
                                        mAdapter.notifyItemChanged(position);
                                        mPresenter.renameFunction(mDevicesBean.getCuId(), mDevicesBean.getDeviceId(),
                                                attributesBean.getNickNameId(), attributesBean.getPropertyCode(), txt);
                                    }
                                }
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setEditValue(TextUtils.isEmpty(attributesBean.getNickNameId()) ? attributesBean.getPropertyName() : attributesBean.getPropertyNickname())
                        .setTitle("修改名称")
                        .setEditHint("请输入名称")
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "scene_name");
            }
        });
    }

    @Override
    public void showFunctions(List<Map<String, String>> attributesBeans) {
        List<FunctionRenameListAdapter.Bean> data = new ArrayList<>();
        for (Map<String, String> attributesBean : attributesBeans) {
            FunctionRenameListAdapter.Bean bean = new FunctionRenameListAdapter.Bean();
            bean.setPropertyCode(attributesBean.get("propertyCode"));
            bean.setPropertyName(attributesBean.get("propertyName"));
            bean.setNickNameId(attributesBean.get("nickNameId"));
            bean.setPropertyNickname(attributesBean.get("propertyNickname"));
            data.add(bean);
        }
        mAdapter.setNewData(data);
        EventBus.getDefault().post(new SwitchRenameEvent());
    }

    @Override
    public void renameSuccess() {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_success);
        loadData();
    }

    @Override
    public void renameFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg("修改失败", throwable), R.drawable.ic_toast_warming);
    }

    private void loadData() {
        mPresenter.getRenameAbleFunctions(mDevicesBean.getCuId(), mDevicesBean.getPid(), mDevicesBean.getDeviceId());
    }
}
