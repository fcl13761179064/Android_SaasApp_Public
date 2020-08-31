package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter;
import com.ayla.hotelsaas.adapter.SceneSettingConditionItemAdapter;
import com.ayla.hotelsaas.adapter.SceneSettingFunctionDatumSetAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.bean.RuleEngineBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.widget.AppBar;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.CustomSheet;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * 场景编辑页面
 * 进入时必须带入(创建：scopeId、siteType ,如果是创建本地联动，还要带上网关的deviceId：targetGateway) 或者 更新：sceneBean
 */
public class SceneSettingActivity extends BaseMvpActivity<SceneSettingView, SceneSettingPresenter> implements SceneSettingView {
    private final int REQUEST_CODE_SELECT_CONDITION = 0X10;
    private final int REQUEST_CODE_SELECT_ACTION = 0X11;
    private final int REQUEST_CODE_SELECT_ICON = 0X12;
    private final int REQUEST_CODE_SELECT_CONDITION_TYPE = 0X13;
    @BindView(R.id.rv_condition)
    public RecyclerView mConditionRecyclerView;
    @BindView(R.id.rv_action)
    public RecyclerView mActionRecyclerView;
    @BindView(R.id.tv_scene_name)
    public TextView mSceneNameTextView;
    @BindView(R.id.appBar)
    AppBar appBar;
    @BindView(R.id.tv_delete)
    View mDeleteView;
    @BindView(R.id.v_add_action)
    ImageView mAddActionImageView;
    @BindView(R.id.v_add_condition)
    ImageView mAddConditionImageView;
    @BindView(R.id.iv_scene_icon)
    ImageView mIconImageView;
    @BindView(R.id.tv_scene_site)
    TextView mSiteTextView;
    @BindView(R.id.ll_join_type)
    LinearLayout mJoinTypeLinearLayout;
    @BindView(R.id.tv_join_type)
    TextView mJoinTypeTextView;

    private RuleEngineBean mRuleEngineBean;
    private SceneSettingConditionItemAdapter mConditionAdapter;
    private SceneSettingActionItemAdapter mActionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Serializable sceneBean = getIntent().getSerializableExtra("sceneBean");
        if (sceneBean instanceof RuleEngineBean) {
            mRuleEngineBean = (RuleEngineBean) sceneBean;
            mSceneNameTextView.setText(mRuleEngineBean.getRuleName());
            mDeleteView.setVisibility(View.VISIBLE);
            int iconIndex = getIconIndexByPath(mRuleEngineBean.getIconPath());
            mIconImageView.setImageResource(getIconResByIndex(iconIndex));
            syncSourceAndAdapter2();
        } else {
            mRuleEngineBean = new RuleEngineBean();
            mRuleEngineBean.setRuleSetMode(3);
            mRuleEngineBean.setScopeId(getIntent().getLongExtra("scopeId", 0));
            mRuleEngineBean.setSiteType(getIntent().getIntExtra("siteType", 0));//1:本地 2:云端
            if (mRuleEngineBean.getSiteType() == 1) {//创建的是本地联动
                String targetGateway = getIntent().getStringExtra("targetGateway");
                mRuleEngineBean.setTargetGateway(targetGateway);
                for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                    if (devicesBean.getDeviceId().equals(targetGateway)) {
                        mRuleEngineBean.setTargetGatewayType(devicesBean.getCuId());
                        break;
                    }
                }
            }
            mRuleEngineBean.setRuleDescription("test");
            mRuleEngineBean.setStatus(1);
            mRuleEngineBean.setIconPath(getIconPathByIndex(1));
            mIconImageView.setImageResource(getIconResByIndex(1));
            mDeleteView.setVisibility(View.GONE);
        }
//        mJoinTypeLinearLayout.setVisibility(mRuleEngineBean.getSiteType() == 2 ? View.VISIBLE : View.GONE);
        refreshJoinTypeShow();
        mSiteTextView.setText(mRuleEngineBean.getSiteType() == 1 ? "网关本地" : "云端");
    }

    private void refreshJoinTypeShow() {
        mJoinTypeTextView.setText(mRuleEngineBean.getRuleSetMode() == 2 ? "当满足所有条件时" : "当满足任意条件时");
    }

    /**
     * 通过icon下标后去图片资源ID
     *
     * @param index
     * @return
     */
    private int getIconResByIndex(int index) {
        return getResources().getIdentifier(String.format("ic_scene_%s", index), "drawable", getBaseContext().getPackageName());
    }

    /**
     * 通过icon下标获得图片url
     *
     * @param i
     * @return
     */
    private String getIconPathByIndex(int i) {
        return String.format("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/%s.png", i);
    }

    /**
     * 通过图片url获取icon下标
     *
     * @param path
     * @return
     */
    private int getIconIndexByPath(String path) {
        int i = 1;
        if (null == path)
            return i;
        String indexString = path.replace("http://cdn-smht.ayla.com.cn/minip/assets/public/scene/", "").replace(".png", "");
        try {
            i = Integer.parseInt(indexString);
        } catch (Exception ignore) {
        }
        return i;
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
        mConditionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mConditionRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(android.R.color.transparent)
                .size(AutoSizeUtils.dp2px(this, 16)).build());
        mConditionAdapter = new SceneSettingConditionItemAdapter(new ArrayList<>());
        mConditionAdapter.bindToRecyclerView(mConditionRecyclerView);
        mConditionAdapter.setEmptyView(R.layout.item_scene_setting_condition_empty);

        mActionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mActionRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(android.R.color.transparent)
                .size(AutoSizeUtils.dp2px(this, 16)).build());
        mActionAdapter = new SceneSettingActionItemAdapter(R.layout.item_scene_setting_action_device);
        mActionAdapter.bindToRecyclerView(mActionRecyclerView);
        mActionAdapter.setEmptyView(R.layout.item_scene_setting_action_empty);
    }

    @Override
    protected void initListener() {
        {
            TextView textView = mConditionAdapter.getEmptyView().findViewById(R.id.tv_add);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpAddConditions();
                }
            });
        }
        {
            TextView textView = mActionAdapter.getEmptyView().findViewById(R.id.tv_add);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    jumpAddActions();
                }
            });
        }
        mConditionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (mRuleEngineBean.getCondition() != null
                        && mRuleEngineBean.getCondition().getItems() != null
                        && mRuleEngineBean.getCondition().getItems().size() > position) {
                    mRuleEngineBean.getCondition().getItems().remove(position);
                    mRuleEngineBean.getCondition().setExpression(calculateConditionExpression(mRuleEngineBean.getRuleSetMode() == 2, mRuleEngineBean.getCondition().getItems()));
                }
                adapter.remove(position);
                mAddConditionImageView.setImageResource(R.drawable.ic_scene_action_add_enable);
            }
        });
        mActionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mRuleEngineBean.getAction().getItems().remove(position);
                mRuleEngineBean.getAction().setExpression(calculateActionExpression(mRuleEngineBean.getAction().getItems()));
                adapter.remove(position);
            }
        });
        appBar.rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mRuleEngineBean.getRuleName() || mRuleEngineBean.getRuleName().length() == 0) {
                    CustomToast.makeText(SceneSettingActivity.this, "名称不能为空", R.drawable.ic_warning).show();
                    return;
                }
                if (mConditionAdapter.getData().size() == 0) {
                    CustomToast.makeText(SceneSettingActivity.this, "请添加条件", R.drawable.ic_warning).show();
                    return;
                }
                if (mActionAdapter.getData().size() == 0) {
                    CustomToast.makeText(SceneSettingActivity.this, "请添加动作", R.drawable.ic_warning).show();
                    return;
                }
                if (mRuleEngineBean.getRuleSetMode() == 2) {//满足所有条件时
                    List<String> exist = new ArrayList<>();
                    for (SceneSettingConditionItemAdapter.ConditionItem datum : mConditionAdapter.getData()) {
                        if (datum instanceof SceneSettingConditionItemAdapter.DeviceConditionItem) {
                            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = ((SceneSettingConditionItemAdapter.DeviceConditionItem) datum).getDatumBean();
                            String deviceId = datumBean.getDeviceId();
                            String leftValue = datumBean.getLeftValue();
                            String value = deviceId + leftValue;
                            if (exist.contains(value)) {
                                CustomToast.makeText(getBaseContext(), "选择满足所有条件时，条件中不可以添加多个同一设备的同一功能", R.drawable.ic_toast_warming).show();
                                return;
                            } else {
                                exist.add(value);
                            }
                        }
                    }
                }
                mPresenter.saveOrUpdateRuleEngine(mRuleEngineBean);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_CONDITION_TYPE && resultCode == RESULT_OK) {//选择条件类型返回结果
            boolean onekey = data.getBooleanExtra("onekey", false);
            if (onekey) {
                mRuleEngineBean.setRuleType(2);
                mConditionAdapter.addData(new SceneSettingConditionItemAdapter.OneKeyConditionItem());
                mConditionAdapter.notifyDataSetChanged();
                mAddConditionImageView.setImageResource(R.drawable.ic_scene_action_add_disable);
            } else {
                doJumpAddConditions();
            }
        }
        if (requestCode == REQUEST_CODE_SELECT_CONDITION && resultCode == RESULT_OK) {//选择条件返回结果
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = (SceneSettingFunctionDatumSetAdapter.DatumBean) data.getParcelableExtra("result");
            RuleEngineBean.Condition.ConditionItem conditionItem = new RuleEngineBean.Condition.ConditionItem();
            conditionItem.setSourceDeviceId(datumBean.getDeviceId());
            conditionItem.setSourceDeviceType(datumBean.getDeviceType());
            conditionItem.setRightValue(datumBean.getRightValue());
            conditionItem.setLeftValue(datumBean.getLeftValue());
            conditionItem.setOperator(datumBean.getOperator());
            mRuleEngineBean.setRuleType(1);
            if (mRuleEngineBean.getCondition() == null) {
                mRuleEngineBean.setCondition(new RuleEngineBean.Condition());
            }
            if (mRuleEngineBean.getCondition().getItems() == null) {
                mRuleEngineBean.getCondition().setItems(new ArrayList<>());
            }
            mRuleEngineBean.getCondition().getItems().add(0, conditionItem);
            mRuleEngineBean.getCondition().setExpression(calculateConditionExpression(mRuleEngineBean.getRuleSetMode() == 2, mRuleEngineBean.getCondition().getItems()));
            mConditionAdapter.getData().add(0, new SceneSettingConditionItemAdapter.DeviceConditionItem(datumBean));
            mConditionAdapter.notifyDataSetChanged();
        }
        if (requestCode == REQUEST_CODE_SELECT_ACTION && resultCode == RESULT_OK) {//选择动作返回结果
            SceneSettingFunctionDatumSetAdapter.DatumBean datumBean = (SceneSettingFunctionDatumSetAdapter.DatumBean) data.getParcelableExtra("result");
            RuleEngineBean.Action.ActionItem actionItem = new RuleEngineBean.Action.ActionItem();
            actionItem.setTargetDeviceType(datumBean.getDeviceType());
            actionItem.setTargetDeviceId(datumBean.getDeviceId());
            actionItem.setRightValueType(datumBean.getRightValueType());
            actionItem.setOperator(datumBean.getOperator());
            actionItem.setLeftValue(datumBean.getLeftValue());
            actionItem.setRightValue(datumBean.getRightValue());
            if (mRuleEngineBean.getAction() == null) {
                mRuleEngineBean.setAction(new RuleEngineBean.Action());
            }
            if (mRuleEngineBean.getAction().getItems() == null) {
                mRuleEngineBean.getAction().setItems(new ArrayList<>());
            }
            mRuleEngineBean.getAction().getItems().add(0, actionItem);
            mRuleEngineBean.getAction().setExpression(calculateActionExpression(mRuleEngineBean.getAction().getItems()));
            mActionAdapter.getData().add(0, datumBean);
            mActionAdapter.notifyDataSetChanged();
        }
        if (requestCode == REQUEST_CODE_SELECT_ICON && resultCode == RESULT_OK) {//选择ICON返回结果
            int index = data.getIntExtra("index", 1);
            mRuleEngineBean.setIconPath(getIconPathByIndex(index));
            mIconImageView.setImageResource(getIconResByIndex(index));
        }
    }

    /**
     * 计算condition表达式 ，并且修改了每个condition's item 的joinType。
     *
     * @param joinAll
     * @param conditionItems
     * @return
     */
    private String calculateConditionExpression(boolean joinAll, List<RuleEngineBean.Condition.ConditionItem> conditionItems) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < conditionItems.size(); i++) {
            RuleEngineBean.Condition.ConditionItem conditionItem = conditionItems.get(i);
            result.append(String.format("func.get('%s','%s','%s') == %s", conditionItem.getSourceDeviceType(), conditionItem.getSourceDeviceId(), conditionItem.getLeftValue(), conditionItem.getRightValue()));
            if (i < conditionItems.size() - 1) {
                result.append(joinAll ? " && " : " || ");
            }
            if (i == 0) {
                conditionItem.setJoinType(0);
            } else {
                conditionItem.setJoinType(joinAll ? 1 : 2);
            }
        }
        return result.toString();
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
        CustomToast.makeText(this, "创建成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void saveFailed(String code) {
        if ("159999".equals(code)) {
            CustomToast.makeText(this, "该设备有异常,请移除后再创建场景", R.drawable.ic_toast_warming).show();
        } else {
            CustomToast.makeText(this, "操作失败", R.drawable.ic_toast_warming).show();
        }
    }

    @Override
    public void deleteSuccess() {
        CustomToast.makeText(this, "删除成功", R.drawable.ic_toast_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void deleteFailed() {
        CustomToast.makeText(this, "删除失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void showData(List<SceneSettingFunctionDatumSetAdapter.DatumBean> conditions, List<SceneSettingFunctionDatumSetAdapter.DatumBean> actions) {
        mActionAdapter.setNewData(actions);
        if (mConditionAdapter.getData().size() == 0) {
            List<SceneSettingConditionItemAdapter.ConditionItem> s = new ArrayList<>();
            for (SceneSettingFunctionDatumSetAdapter.DatumBean condition : conditions) {
                SceneSettingConditionItemAdapter.ConditionItem bean = new SceneSettingConditionItemAdapter.DeviceConditionItem(condition);
                s.add(bean);
            }
            mConditionAdapter.setNewData(s);
        }
    }

    private void syncSourceAndAdapter2() {
        if (mRuleEngineBean.getRuleType() == 2) {//一键执行
            ArrayList<SceneSettingConditionItemAdapter.ConditionItem> list = new ArrayList<>();
            list.add(new SceneSettingConditionItemAdapter.OneKeyConditionItem());
            mConditionAdapter.setNewData(list);
            mAddConditionImageView.setImageResource(R.drawable.ic_scene_action_add_disable);
        }

        List<RuleEngineBean.Condition.ConditionItem> conditionItems = new ArrayList<>();
        List<RuleEngineBean.Action.ActionItem> actionItems = new ArrayList<>();
        if (mRuleEngineBean.getCondition() != null) {
            conditionItems.addAll(mRuleEngineBean.getCondition().getItems());
        }
        if (mRuleEngineBean.getAction() != null) {
            actionItems.addAll(mRuleEngineBean.getAction().getItems());
        }
        mPresenter.loadFunctionDetail(conditionItems, actionItems);
    }

    @OnClick(R.id.ll_icon_area)
    public void jumpIconSelect() {
        Intent mainActivity = new Intent(this, SceneIconSelectActivity.class);
        mainActivity.putExtra("index", getIconIndexByPath(mRuleEngineBean.getIconPath()));
        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_ICON);
    }

    @OnClick(R.id.tv_scene_name)
    public void sceneNameClicked() {
        String currentSceneName = mSceneNameTextView.getText().toString();
        ValueChangeDialog
                .newInstance(new ValueChangeDialog.DoneCallback() {
                    @Override
                    public void onDone(DialogFragment dialog, String txt) {
                        mSceneNameTextView.setText(txt);
                        mRuleEngineBean.setRuleName(txt);
                        dialog.dismissAllowingStateLoss();
                    }
                })
                .setTitle("场景名称")
                .setEditHint("请输入场景名称")
                .setEditValue(currentSceneName)
                .setMaxLength(20)
                .show(getSupportFragmentManager(), "scene_name");
    }

    @OnClick(R.id.v_add_condition)
    public void jumpAddConditions() {
        if (mConditionAdapter.getData().size() == 1 && mConditionAdapter.getData().get(0) instanceof SceneSettingConditionItemAdapter.OneKeyConditionItem) {
            return;
        }
        if (mConditionAdapter.getData().size() == 0 && mRuleEngineBean.getSiteType() == 2) {//只有云端场景才可以设置一键触发。
            Intent mainActivity = new Intent(this, RuleEngineConditionTypeGuideActivity.class);
            startActivityForResult(mainActivity, REQUEST_CODE_SELECT_CONDITION_TYPE);
        } else {
            doJumpAddConditions();
        }
    }

    @OnClick(R.id.v_add_action)
    public void jumpAddActions() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        mainActivity.putExtra("type", 1);
        mainActivity.putParcelableArrayListExtra("datums", new ArrayList<>(mActionAdapter.getData()));
        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_ACTION);
    }

    private void doJumpAddConditions() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        mainActivity.putExtra("type", 0);
        ArrayList<SceneSettingFunctionDatumSetAdapter.DatumBean> datums = new ArrayList<>();

        for (SceneSettingConditionItemAdapter.ConditionItem datum : mConditionAdapter.getData()) {
            if (datum instanceof SceneSettingConditionItemAdapter.DeviceConditionItem) {
                datums.add(((SceneSettingConditionItemAdapter.DeviceConditionItem) datum).getDatumBean());
            }
        }

        mainActivity.putParcelableArrayListExtra("datums", datums);
        mainActivity.putExtra("ruleSetMode", mRuleEngineBean.getRuleSetMode());
        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_CONDITION);
    }

    @OnClick(R.id.tv_delete)
    public void handleDelete() {
        CustomAlarmDialog.newInstance(new CustomAlarmDialog.Callback() {
            @Override
            public void onDone(CustomAlarmDialog dialog) {
                dialog.dismissAllowingStateLoss();
                mPresenter.deleteScene(mRuleEngineBean.getRuleId());
            }

            @Override
            public void onCancel(CustomAlarmDialog dialog) {
                dialog.dismissAllowingStateLoss();
            }
        }).setTitle("确认是否移除").setContent("确认后将永久的从列表中移除该场景，请谨慎操作！").show(getSupportFragmentManager(), "delete");
    }

    @OnClick(R.id.ll_join_type)
    public void handleSwitchJoinType() {
        new CustomSheet.Builder(this)
                .setText("当满足任意条件时", "当满足所有条件时")
                .show(new CustomSheet.CallBack() {
                    @Override
                    public void callback(int index) {
                        if (mRuleEngineBean != null) {
                            mRuleEngineBean.setRuleSetMode(index == 0 ? 3 : 2);
                            refreshJoinTypeShow();
                            if (mRuleEngineBean.getCondition() != null && mRuleEngineBean.getCondition().getItems() != null) {
                                mRuleEngineBean.getCondition().setExpression(calculateConditionExpression(mRuleEngineBean.getRuleSetMode() == 2, mRuleEngineBean.getCondition().getItems()));
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }
}
