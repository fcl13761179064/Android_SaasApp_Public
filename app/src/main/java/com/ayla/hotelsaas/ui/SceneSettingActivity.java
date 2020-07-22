package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.utils.ToastUtils;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.SceneNameSetDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

public class SceneSettingActivity extends BaseMvpActivity<SceneSettingView, SceneSettingPresenter> implements SceneSettingView {
    @BindView(R.id.rv)
    public RecyclerView mRecyclerView;
    @BindView(R.id.tv_scene_name)
    public TextView mSceneNameTextView;
    @BindView(R.id.appBar)
    AppBar appBar;

    private RuleEngineBean mRuleEngineBean;
    private SceneSettingActionItemAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mRuleEngineBean != null) {
            syncSourceAndAdapter();
            mSceneNameTextView.setText(mRuleEngineBean.getRuleName());
        } else {
            mRuleEngineBean = new RuleEngineBean();
            mRuleEngineBean.setScopeId(getIntent().getIntExtra("scopeId", 0));
            mRuleEngineBean.setRuleDescription("sssss");
            mRuleEngineBean.setRuleType(2);
        }
    }

    @Override
    protected SceneSettingPresenter initPresenter() {
        return new SceneSettingPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene_setting;
    }

    @Override
    protected void initView() {
        mAdapter = new SceneSettingActionItemAdapter(R.layout.item_scene_setting_action_device);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(android.R.color.transparent)
                .size(AutoSizeUtils.dp2px(this, 10)).build());
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.item_scene_setting_action_empty);
    }

    @Override
    protected void initListener() {
        if (mAdapter.getEmptyView() != null) {
            mAdapter.getEmptyView().findViewById(R.id.tv_add_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpAddActions();
                }
            });
        }
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mRuleEngineBean.getAction().getItems().remove(position);
                mRuleEngineBean.getAction().setExpression(calculateActionExpression(mRuleEngineBean.getAction().getItems()));
                mAdapter.remove(position);
            }
        });
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mRuleEngineBean.getRuleName() || mRuleEngineBean.getRuleName().length() == 0) {
                    CustomToast.makeText(SceneSettingActivity.this, "名称不能为空", R.drawable.ic_warning).show();
                    return;
                }
                if (null == mRuleEngineBean.getAction() || mRuleEngineBean.getAction().getItems().size() == 0) {
                    CustomToast.makeText(SceneSettingActivity.this, "请添加动作", R.drawable.ic_warning).show();
                    return;
                }
                mPresenter.saveRuleEngine(mRuleEngineBean);
            }
        });
    }

    @OnClick(R.id.tv_scene_name)
    public void sceneNameClicked() {
        SceneNameSetDialog.newInstance(new SceneNameSetDialog.DoneCallback() {
            @Override
            public void onDone(DialogFragment dialog, String txt) {
                if (null == txt || txt.length() == 0) {
                    ToastUtils.showShortToast("输入不能为空");
                } else {
                    mSceneNameTextView.setText(txt);
                    mRuleEngineBean.setRuleName(txt);
                    dialog.dismissAllowingStateLoss();
                }
            }
        }).show(getSupportFragmentManager(), "scene_name");
    }

    @OnClick(R.id.v_add_ic)
    public void jumpAddActions() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        startActivityForResult(mainActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = (SceneSettingFunctionDatumSetAdapter.DatumBean) data.getSerializableExtra("result");
            RuleEngineBean.Action.ActionItem actionItem = new RuleEngineBean.Action.ActionItem();
            actionItem.setTargetDeviceType(datumBean.getTargetDeviceType());
            actionItem.setTargetDeviceId(datumBean.getTargetDeviceId());
            actionItem.setRightValueType(datumBean.getRightValueType());
            actionItem.setOperator(datumBean.getOperator());
            actionItem.setLeftValue(datumBean.getLeftValue());
            actionItem.setRightValueType(datumBean.getRightValueType());
            if (mRuleEngineBean.getAction() == null) {
                mRuleEngineBean.setAction(new RuleEngineBean.Action());
            }
            if (mRuleEngineBean.getAction().getItems() == null) {
                mRuleEngineBean.getAction().setItems(new ArrayList<>());
            }
            mRuleEngineBean.getAction().getItems().add(actionItem);
            mRuleEngineBean.getAction().setExpression(calculateActionExpression(mRuleEngineBean.getAction().getItems()));
            mAdapter.getData().add(datumBean);
            mAdapter.notifyDataSetChanged();

        }
    }

    private String calculateActionExpression(List<RuleEngineBean.Action.ActionItem> actionItems) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < actionItems.size(); i++) {
            RuleEngineBean.Action.ActionItem actionItem = actionItems.get(i);
            result.append(String.format("func.execute('%s','%s','%s')", actionItem.getTargetDeviceType(), actionItem.getTargetDeviceId(), actionItem.getLeftValue()));
            if (i < actionItems.size() - 1) {
                result.append(" && ");
            }
        }
        return result.toString();
    }

    @Override
    public void saveSuccess() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void saveFailed() {

    }

    @OnClick(R.id.tv_delete)
    public void handleDelete() {
        CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
            @Override
            public void onDone(CustomAlarmDialog dialog) {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onCancel(CustomAlarmDialog dialog) {
                dialog.dismissAllowingStateLoss();
            }
        }, "确认是否移除", "111").show(getSupportFragmentManager(), "delete");
    }

    private void syncSourceAndAdapter() {
        List<SceneSettingFunctionDatumSetAdapter.DatumBean> datas = new ArrayList<>();
        for (RuleEngineBean.Action.ActionItem actionItem : mRuleEngineBean.getAction().getItems()) {
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = new SceneSettingFunctionDatumSetAdapter.DatumBean();
            datumBean.setLeftValue(actionItem.getLeftValue());
            datumBean.setFunctionName("Switch_Control".equals(actionItem.getLeftValue()) ? "功能开关" : "未知功能");
            datumBean.setValueName(actionItem.getRightValue() == 1 ? "开启" : "关闭");
            datumBean.setTargetDeviceId(actionItem.getTargetDeviceId());
            datumBean.setTargetDeviceType(actionItem.getTargetDeviceType());
            datumBean.setRightValueType(actionItem.getRightValueType());
            datumBean.setOperator(actionItem.getOperator());
            datumBean.setLeftValue(actionItem.getLeftValue());
            datas.add(datumBean);
        }
        mAdapter.setNewData(datas);
    }
}
