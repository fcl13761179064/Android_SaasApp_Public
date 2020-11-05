package com.ayla.hotelsaas.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.SceneSettingActionItemAdapter;
import com.ayla.hotelsaas.adapter.SceneSettingConditionItemAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseMvpActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.data.net.RxjavaFlatmapThrowable;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.localBean.DeviceType;
import com.ayla.hotelsaas.localBean.LocalSceneBean;
import com.ayla.hotelsaas.localBean.RemoteSceneBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.CustomSheet;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 场景编辑页面
 * 进入时必须带入(创建：scopeId、siteType ,如果是创建本地联动，还要带上网关的deviceId：targetGateway) 或者 更新：sceneBean
 */
public class SceneSettingActivity extends BaseMvpActivity<SceneSettingView, SceneSettingPresenter> implements SceneSettingView {
    private final int REQUEST_CODE_SELECT_CONDITION = 0X10;
    private final int REQUEST_CODE_SELECT_ACTION = 0X11;
    private final int REQUEST_CODE_SELECT_ICON = 0X12;
    private final int REQUEST_CODE_SELECT_CONDITION_TYPE = 0X13;
    private final int REQUEST_CODE_SELECT_ENABLE_TIME = 0X14;
    @BindView(R.id.rv_condition)
    public RecyclerView mConditionRecyclerView;
    @BindView(R.id.rv_action)
    public RecyclerView mActionRecyclerView;
    @BindView(R.id.tv_scene_name)
    public TextView mSceneNameTextView;
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
    @BindView(R.id.tv_join_type)
    TextView mJoinTypeTextView;
    @BindView(R.id.tv_enable_time)
    TextView mEnableTimeTextView;
    @BindView(R.id.rl_enable_time)
    View rl_enable_time;

    private BaseSceneBean mRuleEngineBean;
    private SceneSettingConditionItemAdapter mConditionAdapter;
    private SceneSettingActionItemAdapter mActionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Serializable sceneBean = getIntent().getSerializableExtra("sceneBean");
        if (sceneBean instanceof BaseSceneBean) {
            mRuleEngineBean = (BaseSceneBean) sceneBean;
            mSceneNameTextView.setText(mRuleEngineBean.getRuleName());
            mDeleteView.setVisibility(View.VISIBLE);
            syncSourceAndAdapter2();
        } else {
            long scopeId = getIntent().getLongExtra("scopeId", 0);
            int siteType = getIntent().getIntExtra("siteType", 0);
            if (siteType == BaseSceneBean.SITE_TYPE.LOCAL.code) {
                mRuleEngineBean = new LocalSceneBean();
                String targetGateway = getIntent().getStringExtra("targetGateway");
                ((LocalSceneBean) mRuleEngineBean).setTargetGateway(targetGateway);
                for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                    if (devicesBean.getDeviceId().equals(targetGateway)) {
                        ((LocalSceneBean) mRuleEngineBean).setTargetGatewayType(DeviceType.valueOf(devicesBean.getCuId()));
                        break;
                    }
                }
            } else {
                mRuleEngineBean = new RemoteSceneBean();
            }
            mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.AUTO);
            mRuleEngineBean.setRuleSetMode(BaseSceneBean.RULE_SET_MODE.ANY);
            mRuleEngineBean.setScopeId(scopeId);
            mRuleEngineBean.setRuleDescription("test");
            mRuleEngineBean.setEnable(true);
            mRuleEngineBean.setIconPath(getIconPathByIndex(1));
            BaseSceneBean.EnableTime enableTime = new BaseSceneBean.EnableTime();
            mRuleEngineBean.setEnableTime(enableTime);
            mDeleteView.setVisibility(View.GONE);
        }
        int iconIndex = getIconIndexByPath(mRuleEngineBean.getIconPath());
        mIconImageView.setImageResource(getIconResByIndex(iconIndex));
        refreshJoinTypeShow();
        if (mRuleEngineBean instanceof LocalSceneBean) {
            String targetGateway = ((LocalSceneBean) mRuleEngineBean).getTargetGateway();
            for (DeviceListBean.DevicesBean devicesBean : MyApplication.getInstance().getDevicesBean()) {
                if (devicesBean.getDeviceId().equals(targetGateway)) {
                    mSiteTextView.setText(devicesBean.getNickname());
                    break;
                }
            }
        } else {
            mSiteTextView.setText("云端");
        }
        mEnableTimeTextView.setText(decodeCronExpression2(mRuleEngineBean.getEnableTime()));
        syncRuleTYpeShow();
    }

    private String decodeCronExpression2(BaseSceneBean.EnableTime enableTime) {
        StringBuilder sb = new StringBuilder();
        try {
            if (enableTime.getEnableWeekDay().length == 7) {
                sb.append("每天");
            } else {
                for (int i = 0; i < enableTime.getEnableWeekDay().length; i++) {
                    switch (enableTime.getEnableWeekDay()[i]) {
                        case 1:
                            sb.append("周一");
                            break;
                        case 2:
                            sb.append("周二");
                            break;
                        case 3:
                            sb.append("周三");
                            break;
                        case 4:
                            sb.append("周四");
                            break;
                        case 5:
                            sb.append("周五");
                            break;
                        case 6:
                            sb.append("周六");
                            break;
                        case 7:
                            sb.append("周日");
                            break;
                    }
                    if (i != enableTime.getEnableWeekDay().length - 1) {
                        sb.append("/");
                    }
                }
            }
            sb.append(" ");

            if (enableTime.isAllDay()) {
                sb.append("全天");
            } else {
                sb.append(formatTime(enableTime.getStartHour(), enableTime.getStartMinute()));
                sb.append("~");
                sb.append(formatTime(enableTime.getEndHour(), enableTime.getEndMinute()));
            }
        } catch (Exception ignored) {
        }
        return sb.toString();
    }

    private String formatTime(int hour, int minute) {
        SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        String format = sf.format(calendar.getTime());
        return format;
    }

    private void refreshJoinTypeShow() {
        mJoinTypeTextView.setText(mRuleEngineBean.getRuleSetMode() == BaseSceneBean.RULE_SET_MODE.ALL ? "当满足所有条件时" : "当满足任意条件时");
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
        mConditionRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        mConditionAdapter = new SceneSettingConditionItemAdapter(new ArrayList<>());
        mConditionAdapter.bindToRecyclerView(mConditionRecyclerView);
        mConditionAdapter.setEmptyView(R.layout.item_scene_setting_condition_empty);

        mActionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mActionRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
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
                BaseSceneBean.Condition condition = mRuleEngineBean.getConditions().remove(position);
                if (condition instanceof BaseSceneBean.OneKeyCondition) {
                    mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.AUTO);
                    syncRuleTYpeShow();
                }
                showData();
            }
        });
        mActionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                mRuleEngineBean.getActions().remove(position);
                showData();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_CONDITION_TYPE && resultCode == RESULT_OK) {//选择条件类型返回结果
            boolean onekey = data.getBooleanExtra("onekey", false);
            if (onekey) {
                mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.ONE_KEY);
                mRuleEngineBean.getConditions().add(new BaseSceneBean.OneKeyCondition());
                showData();
                syncRuleTYpeShow();
            } else {
                doJumpAddConditions();
            }
        }
        if (requestCode == REQUEST_CODE_SELECT_CONDITION && resultCode == RESULT_OK) {//选择条件返回结果
            ISceneSettingFunctionDatumSet.CallBackBean datumBean = (ISceneSettingFunctionDatumSet.CallBackBean) data.getSerializableExtra("result");
            BaseSceneBean.DeviceCondition conditionItem = new BaseSceneBean.DeviceCondition();
            conditionItem.setSourceDeviceId(datumBean.getDeviceId());
            conditionItem.setSourceDeviceType(DeviceType.valueOf(datumBean.getDeviceType()));
            conditionItem.setRightValue(datumBean.getRightValue());
            conditionItem.setLeftValue(datumBean.getLeftValue());
            conditionItem.setOperator(datumBean.getOperator());
            conditionItem.setFunctionName(datumBean.getFunctionName());
            conditionItem.setValueName(datumBean.getValueName());
            mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.AUTO);
            mRuleEngineBean.getConditions().add(0, conditionItem);
            showData();
        }
        if (requestCode == REQUEST_CODE_SELECT_ACTION && resultCode == RESULT_OK) {//选择动作返回结果
            ISceneSettingFunctionDatumSet.CallBackBean datumBean = (ISceneSettingFunctionDatumSet.CallBackBean) data.getSerializableExtra("result");
            BaseSceneBean.Action actionItem = new BaseSceneBean.Action();
            actionItem.setTargetDeviceType(DeviceType.valueOf(datumBean.getDeviceType()));
            actionItem.setTargetDeviceId(datumBean.getDeviceId());
            actionItem.setRightValueType(BaseSceneBean.Action.VALUE_TYPE.valueOf(datumBean.getRightValueType()));
            actionItem.setOperator(datumBean.getOperator());
            actionItem.setLeftValue(datumBean.getLeftValue());
            actionItem.setRightValue(datumBean.getRightValue());
            actionItem.setFunctionName(datumBean.getFunctionName());
            actionItem.setValueName(datumBean.getValueName());
            mRuleEngineBean.getActions().add(0, actionItem);
            showData();
        }
        if (requestCode == REQUEST_CODE_SELECT_ICON && resultCode == RESULT_OK) {//选择ICON返回结果
            int index = data.getIntExtra("index", 1);
            mRuleEngineBean.setIconPath(getIconPathByIndex(index));
            mIconImageView.setImageResource(getIconResByIndex(index));
        }
        if (requestCode == REQUEST_CODE_SELECT_ENABLE_TIME && resultCode == RESULT_OK) {//选择生效时间段返回
            BaseSceneBean.EnableTime enableTime = (BaseSceneBean.EnableTime) data.getSerializableExtra("enableTime");
            mRuleEngineBean.setEnableTime(enableTime);
            mEnableTimeTextView.setText(decodeCronExpression2(enableTime));
        }
    }

    @Override
    protected void appBarRightTvClicked() {
        if (null == mRuleEngineBean.getRuleName() || mRuleEngineBean.getRuleName().length() == 0 || mRuleEngineBean.getRuleName().trim().isEmpty()) {
            CustomToast.makeText(SceneSettingActivity.this, "名称不能为空", R.drawable.ic_warning).show();
            return;
        }
        if (mRuleEngineBean.getConditions().size() == 0) {
            CustomToast.makeText(SceneSettingActivity.this, "请添加条件", R.drawable.ic_warning).show();
            return;
        }
        if (mRuleEngineBean.getActions().size() == 0) {
            CustomToast.makeText(SceneSettingActivity.this, "请添加动作", R.drawable.ic_warning).show();
            return;
        }
        if (mRuleEngineBean.getRuleSetMode() == BaseSceneBean.RULE_SET_MODE.ALL) {//满足所有条件时
            List<String> exist = new ArrayList<>();
            for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
                if (condition instanceof BaseSceneBean.DeviceCondition) {
                    String deviceId = ((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId();
                    String leftValue = ((BaseSceneBean.DeviceCondition) condition).getLeftValue();
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

    @Override
    public void saveSuccess() {
        CustomToast.makeText(this, "创建成功", R.drawable.ic_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void saveFailed(Throwable throwable) {
        if (throwable instanceof RxjavaFlatmapThrowable) {
            String code = ((RxjavaFlatmapThrowable) throwable).getCode();
            if ("159999".equals(code)) {
                CustomToast.makeText(this, "该设备有异常,请移除后再创建场景", R.drawable.ic_toast_warming).show();
                return;
            } else if ("155000".equals(code)) {
                CustomToast.makeText(this, "场景名称已被使用", R.drawable.ic_toast_warming).show();
                return;
            }
        }
        CustomToast.makeText(this, "操作失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void deleteSuccess() {
        CustomToast.makeText(this, "删除成功", R.drawable.ic_success).show();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void deleteFailed() {
        CustomToast.makeText(this, "删除失败", R.drawable.ic_toast_warming).show();
    }

    @Override
    public void showData() {
        List<SceneSettingConditionItemAdapter.ConditionItem> items = new ArrayList<>();
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            items.add(new SceneSettingConditionItemAdapter.ConditionItem(condition));
        }
        mConditionAdapter.setNewData(items);
        mActionAdapter.setNewData(mRuleEngineBean.getActions());
    }

    private void syncSourceAndAdapter2() {
        List<BaseSceneBean.DeviceCondition> conditions = new ArrayList<>();
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            if (condition instanceof BaseSceneBean.DeviceCondition) {
                conditions.add((BaseSceneBean.DeviceCondition) condition);
            }
        }
        mPresenter.loadFunctionDetail(conditions, mRuleEngineBean.getActions());
    }

    @OnClick(R.id.ll_icon_area)
    public void jumpIconSelect() {
        Intent mainActivity = new Intent(this, SceneIconSelectActivity.class);
        mainActivity.putExtra("index", getIconIndexByPath(mRuleEngineBean.getIconPath()));
        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_ICON);
    }

    @OnClick(R.id.rl_scene_name)
    public void sceneNameClicked() {
        String currentSceneName = mSceneNameTextView.getText().toString();
        ValueChangeDialog
                .newInstance(new ValueChangeDialog.DoneCallback() {
                    @Override
                    public void onDone(DialogFragment dialog, String txt) {
                        if (TextUtils.isEmpty(txt) || txt.trim().isEmpty()) {
                            CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warming).show();
                            return;
                        }
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
        if (mRuleEngineBean.getConditions().size() == 0 && mRuleEngineBean.getSiteType() == BaseSceneBean.SITE_TYPE.REMOTE) {//只有云端场景才可以设置一键触发。
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
        mainActivity.putExtra("scopeId", mRuleEngineBean.getScopeId());
        if (mRuleEngineBean instanceof LocalSceneBean) {
            mainActivity.putExtra("targetGateway", ((LocalSceneBean) mRuleEngineBean).getTargetGateway());
        }

        ArrayList<String> selectedDatum = new ArrayList<>();
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            String targetDeviceId = action.getTargetDeviceId();
            String leftValue = action.getLeftValue();
            selectedDatum.add(targetDeviceId + " " + leftValue);
        }

        mainActivity.putStringArrayListExtra("selectedDatum", selectedDatum);
        startActivityForResult(mainActivity, REQUEST_CODE_SELECT_ACTION);
    }

    private void doJumpAddConditions() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        mainActivity.putExtra("type", 0);
        mainActivity.putExtra("scopeId", mRuleEngineBean.getScopeId());
        if (mRuleEngineBean instanceof LocalSceneBean) {
            mainActivity.putExtra("targetGateway", ((LocalSceneBean) mRuleEngineBean).getTargetGateway());
        }

        ArrayList<String> selectedDatum = new ArrayList<>();
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            if (condition instanceof BaseSceneBean.DeviceCondition) {
                String sourceDeviceId = ((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId();
                String leftValue = ((BaseSceneBean.DeviceCondition) condition).getLeftValue();
                selectedDatum.add(sourceDeviceId + " " + leftValue);
            }
        }

        mainActivity.putStringArrayListExtra("selectedDatum", selectedDatum);
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
                            mRuleEngineBean.setRuleSetMode(index == 0 ? BaseSceneBean.RULE_SET_MODE.ANY : BaseSceneBean.RULE_SET_MODE.ALL);
                            refreshJoinTypeShow();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    @OnClick(R.id.rl_enable_time)
    public void handleJumpEnableTime() {
        Intent intent = new Intent(this, SceneSettingEnableTimeActivity.class);
        intent.putExtra("enableTime", mRuleEngineBean.getEnableTime());
        startActivityForResult(intent, REQUEST_CODE_SELECT_ENABLE_TIME);
    }

    /**
     * 同步一键执行 或者 自动化 场景的不同显示效果
     */
    private void syncRuleTYpeShow() {
        if (mRuleEngineBean.getRuleType() == BaseSceneBean.RULE_TYPE.ONE_KEY) {//一键执行
            mAddConditionImageView.setImageResource(R.drawable.ic_scene_action_add_disable);
            mAddConditionImageView.setClickable(false);
            rl_enable_time.setVisibility(View.GONE);
        } else {
            mAddConditionImageView.setImageResource(R.drawable.ic_scene_action_add_enable);
            mAddConditionImageView.setClickable(true);
            rl_enable_time.setVisibility(View.VISIBLE);
        }
    }
}
