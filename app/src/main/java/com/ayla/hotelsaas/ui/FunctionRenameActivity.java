package com.ayla.hotelsaas.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.FunctionRenameListAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.mvp.present.FunctionRenamePresenter;
import com.ayla.hotelsaas.mvp.view.FunctionRenameView;
import com.ayla.hotelsaas.utils.FastClickUtils;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * 功能重命名页面
 * 进入必须带上 device
 */
public class FunctionRenameActivity extends BaseMvpActivity<FunctionRenameView, FunctionRenamePresenter> implements FunctionRenameView {
    @BindView(R.id.rl)
    RecyclerView mRecyclerView;
    private FunctionRenameListAdapter mAdapter;
    private DeviceListBean.DevicesBean mDevicesBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevicesBean = (DeviceListBean.DevicesBean) getIntent().getSerializableExtra("device");
        mPresenter.getRenameAbleFunctions(mDevicesBean.getCuId(), mDevicesBean.getDeviceName(), mDevicesBean.getDeviceId());
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
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .size(SizeUtils.dp2px(1))
                .color(Color.TRANSPARENT).build());
        mAdapter = new FunctionRenameListAdapter(R.layout.item_function_rename_list);
        mAdapter.bindToRecyclerView(mRecyclerView);
    }

    @Override
    protected void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (FastClickUtils.isDoubleClick()) {
                    return;
                }
                FunctionRenameListAdapter.Bean attributesBean = mAdapter.getItem(position);
                ValueChangeDialog
                        .newInstance(new ValueChangeDialog.DoneCallback() {
                            @Override
                            public void onDone(DialogFragment dialog, String txt) {
                                if (TextUtils.isEmpty(txt)) {
                                    CustomToast.makeText(getBaseContext(), "不能为空", R.drawable.ic_toast_warming).show();
                                } else {
                                    if (mDevicesBean != null) {
                                        mPresenter.renameFunction(mDevicesBean.getCuId(), mDevicesBean.getDeviceId(),
                                                attributesBean.getId(), attributesBean.getCode(), attributesBean.getDisplayName(), txt);
                                    }
                                }
                                dialog.dismissAllowingStateLoss();
                            }
                        })
                        .setEditValue(attributesBean.getPropertyValue())
                        .setTitle("修改名称")
                        .setEditHint("请输入名称")
                        .setMaxLength(20)
                        .show(getSupportFragmentManager(), "scene_name");
            }
        });
    }

    @Override
    public void showFunctions(List<FunctionRenameListAdapter.Bean> attributesBeans) {
        mAdapter.setNewData(attributesBeans);
    }

    @Override
    public void renameSuccess() {
        CustomToast.makeText(this, "修改成功", R.drawable.ic_toast_success).show();
        finish();
    }

    @Override
    public void renameFailed() {
        CustomToast.makeText(this, "修改失败", R.drawable.ic_toast_success).show();
    }
}
