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
import com.ayla.hotelsaas.bean.DeviceTemplateBean;
import com.ayla.hotelsaas.data.net.SelfMsgException;
import com.ayla.hotelsaas.events.SceneChangedEvent;
import com.ayla.hotelsaas.events.SceneItemEvent;
import com.ayla.hotelsaas.localBean.BaseSceneBean;
import com.ayla.hotelsaas.localBean.DeviceType;
import com.ayla.hotelsaas.localBean.LocalSceneBean;
import com.ayla.hotelsaas.localBean.RemoteSceneBean;
import com.ayla.hotelsaas.mvp.present.SceneSettingPresenter;
import com.ayla.hotelsaas.mvp.view.SceneSettingView;
import com.ayla.hotelsaas.utils.TempUtils;
import com.ayla.hotelsaas.widget.CustomAlarmDialog;
import com.ayla.hotelsaas.widget.CustomSheet;
import com.ayla.hotelsaas.widget.ValueChangeDialog;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
 * 如果是要求创建一键联动，可以带上参数 {@link Boolean forceOneKey = true}
 */
public class SceneSettingActivity extends BaseMvpActivity<SceneSettingView, SceneSettingPresenter> implements SceneSettingView {
    private final int REQUEST_CODE_SELECT_ICON = 0X12;
    private final int REQUEST_CODE_SELECT_CONDITION_TYPE = 0X13;
    private final int REQUEST_CODE_SELECT_ENABLE_TIME = 0X14;
    private final int REQUEST_CODE_SELECT_ACTION_TYPE = 0X15;
    private final int REQUEST_CODE_SET_DELAY_ACTION = 0X16;
    private final int REQUEST_CODE_HOTEL_WELCOME_ACTION = 0X17;
    private final int REQUEST_CODE_EDIT_DELAY_ACTION = 0X18;
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
    @BindView(R.id.tv_ill_state)
    View tv_ill_state;

    private BaseSceneBean mRuleEngineBean;
    private SceneSettingConditionItemAdapter mConditionAdapter;
    private SceneSettingActionItemAdapter mActionAdapter;

    private boolean forceOneKey;

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
            if (siteType == BaseSceneBean.SITE_TYPE.LOCAL) {
                mRuleEngineBean = new LocalSceneBean();
                String targetGateway = getIntent().getStringExtra("targetGateway");
                DeviceListBean.DevicesBean gatewayDevice = MyApplication.getInstance().getDevicesBean(targetGateway);
                int cuId = gatewayDevice.getCuId();
                ((LocalSceneBean) mRuleEngineBean).setTargetGateway(targetGateway);
                ((LocalSceneBean) mRuleEngineBean).setTargetGatewayType(cuId);
            } else {
                mRuleEngineBean = new RemoteSceneBean();
            }
            mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.AUTO);
            mRuleEngineBean.setRuleSetMode(BaseSceneBean.RULE_SET_MODE.ANY);
            mRuleEngineBean.setScopeId(scopeId);
            mRuleEngineBean.setRuleDescription("test");
            mRuleEngineBean.setStatus(1);
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
        tv_ill_state.setVisibility((mRuleEngineBean.getStatus() == 0 || mRuleEngineBean.getStatus() == 1) ? View.GONE : View.VISIBLE);
        mEnableTimeTextView.setText(decodeCronExpression2(mRuleEngineBean.getEnableTime()));

        forceOneKey = getIntent().getBooleanExtra("forceOneKey", false);

        if (forceOneKey) {
            mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.ONE_KEY);
            mRuleEngineBean.getConditions().add(new BaseSceneBean.OneKeyCondition());
            showData();
        }

        syncRuleTYpeShow();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
        mConditionAdapter = new SceneSettingConditionItemAdapter(null);
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
        mActionAdapter = new SceneSettingActionItemAdapter(null);
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
                BaseSceneBean.Condition condition = mRuleEngineBean.getConditions().get(position);
                if (condition instanceof BaseSceneBean.OneKeyCondition && forceOneKey) {
                    showError(new SelfMsgException("不可删除", null));
                    return;
                }
                condition = mRuleEngineBean.getConditions().remove(position);

                if (condition instanceof BaseSceneBean.OneKeyCondition) {
                    mRuleEngineBean.setRuleType(BaseSceneBean.RULE_TYPE.AUTO);
                    syncRuleTYpeShow();
                }
                showData();
            }
        });
        mConditionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseSceneBean.Condition condition = mRuleEngineBean.getConditions().get(position);
                if (condition instanceof BaseSceneBean.DeviceCondition) {
                    jumpEditConditionDeviceItem((BaseSceneBean.DeviceCondition) condition, position);
                }
            }
        });
        mActionAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            private void doRemove(int position) {
                mRuleEngineBean.getActions().remove(position);
                showData();
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                BaseSceneBean.Action action = mRuleEngineBean.getActions().get(position);
                if (action instanceof BaseSceneBean.WelcomeAction) {
                    CustomAlarmDialog.newInstance()
                            .setDoneCallback(new CustomAlarmDialog.Callback() {
                                @Override
                                public void onDone(CustomAlarmDialog dialog) {
                                    dialog.dismissAllowingStateLoss();
                                    doRemove(position);
                                }

                                @Override
                                public void onCancel(CustomAlarmDialog dialog) {
                                    dialog.dismissAllowingStateLoss();
                                }
                            })
                            .setTitle("确认删除").setContent("删除后该房间下的音箱将不再播放欢迎语，是否删除？")
                            .show(getSupportFragmentManager(), "delete");
                } else {
                    doRemove(position);
                }
            }
        });
        mActionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseSceneBean.Action action = mRuleEngineBean.getActions().get(position);
                if (action instanceof BaseSceneBean.DelayAction) {
                    int seconds = 0;
                    try {
                        String rightValue = action.getRightValue();
                        seconds = Integer.parseInt(rightValue);
                    } catch (Exception ignored) {
                    }
                    startActivityForResult(new Intent(SceneSettingActivity.this, SceneActionDelaySettingActivity.class)
                            .putExtra("seconds", seconds)
                            .putExtra("position", position), REQUEST_CODE_EDIT_DELAY_ACTION);
                } else if (action instanceof BaseSceneBean.DeviceAction) {
                    jumpEditActionDeviceItem((BaseSceneBean.DeviceAction) action, position);
                }
            }
        });
    }

    private void jumpEditConditionDeviceItem(BaseSceneBean.DeviceCondition condition, int position) {
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(condition.getSourceDeviceId());
        if (devicesBean != null && devicesBean.getBindType() == 0) {
            Intent intent = addDeviceConditionIntent();

            intent.setClass(getApplicationContext(), SceneSettingFunctionDatumSetActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("condition", condition);
            intent.putExtra("editPosition", position);
            intent.putExtra("property", condition.getLeftValue());
            intent.putExtra("deviceId", condition.getSourceDeviceId());
            startActivity(intent);
        }
    }

    private void jumpEditActionDeviceItem(BaseSceneBean.DeviceAction action, int position) {
        DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(action.getTargetDeviceId());
        if (devicesBean != null && devicesBean.getBindType() == 0) {
            Intent intent = addDeviceActionIntent();

            intent.setClass(getApplicationContext(), SceneSettingFunctionDatumSetActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("action", action);
            intent.putExtra("editPosition", position);
            intent.putExtra("property", action.getLeftValue());
            intent.putExtra("deviceId", action.getTargetDeviceId());
            startActivity(intent);
        }
    }

    @Override
    protected void appBarRightTvClicked() {
        if (null == mRuleEngineBean.getRuleName() || mRuleEngineBean.getRuleName().length() == 0 || mRuleEngineBean.getRuleName().trim().isEmpty()) {
            CustomToast.makeText(SceneSettingActivity.this, "名称不能为空", R.drawable.ic_warning);
            return;
        }
        if (mRuleEngineBean.getConditions().size() == 0) {
            CustomToast.makeText(SceneSettingActivity.this, "请添加条件", R.drawable.ic_warning);
            return;
        }
        if (mRuleEngineBean.getActions().size() == 0) {
            CustomToast.makeText(SceneSettingActivity.this, "请添加动作", R.drawable.ic_warning);
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
                        CustomToast.makeText(getBaseContext(), "选择满足所有条件时，条件中不可以添加多个同一设备的同一功能", R.drawable.ic_toast_warming);
                        return;
                    } else {
                        exist.add(value);
                    }
                }
            }
        }
        BaseSceneBean.Action lastAction = mRuleEngineBean.getActions().get(mRuleEngineBean.getActions().size() - 1);
        if (lastAction instanceof BaseSceneBean.DelayAction) {//如果最后一个Action是延时，不允许
            CustomToast.makeText(getBaseContext(), "延时后必须添加一个设备类型的动作", R.drawable.ic_toast_warming);
            return;
        }
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            if (condition instanceof BaseSceneBean.DeviceCondition) {
                DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(((BaseSceneBean.DeviceCondition) condition).getSourceDeviceId());
                if (devicesBean == null) {
                    CustomToast.makeText(getBaseContext(), "如想激活此联动，请先删除已移除的设备", R.drawable.ic_toast_warming);
                    return;
                } else {
                    if (devicesBean.getBindType() == 1) {
                        CustomToast.makeText(getBaseContext(), "请先绑定待添加的设备", R.drawable.ic_toast_warming);
                        return;
                    }
                }
            }
        }
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            if (action instanceof BaseSceneBean.DeviceAction) {
                DeviceListBean.DevicesBean devicesBean = MyApplication.getInstance().getDevicesBean(((BaseSceneBean.DeviceAction) action).getTargetDeviceId());
                if (devicesBean == null) {
                    CustomToast.makeText(getBaseContext(), "如想激活此联动，请先删除已移除的设备", R.drawable.ic_toast_warming);
                    return;
                } else {
                    if (devicesBean.getBindType() == 1) {
                        CustomToast.makeText(getBaseContext(), "请先绑定待添加的设备", R.drawable.ic_toast_warming);
                        return;
                    }
                }
            }
        }

        if (mRuleEngineBean.getRuleType() == 2) {//一键执行
            mRuleEngineBean.setStatus(1);
        } else if (mRuleEngineBean.getRuleType() == 1) {//自动化
            if (mRuleEngineBean.getStatus() == 2) {//如果是异常状态，就要更改为 不可用的正常状态
                mRuleEngineBean.setStatus(0);
            }
        }
        mPresenter.saveOrUpdateRuleEngine(mRuleEngineBean);
    }

    @Override
    public void saveSuccess() {
        CustomToast.makeText(this, "创建成功", R.drawable.ic_success);
        setResult(RESULT_OK);
        EventBus.getDefault().post(new SceneChangedEvent());
        finish();
    }

    @Override
    public void saveFailed(Throwable throwable) {
        CustomToast.makeText(this, TempUtils.getLocalErrorMsg(throwable), R.drawable.ic_toast_warming);
    }

    @Override
    public void deleteSuccess() {
        CustomToast.makeText(this, "删除成功", R.drawable.ic_success);
        setResult(RESULT_OK);
        EventBus.getDefault().post(new SceneChangedEvent());
        finish();
    }

    @Override
    public void deleteFailed() {
    }

    @Override
    public void showData() {
        List<SceneSettingConditionItemAdapter.ConditionItem> conditionItems = new ArrayList<>();
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            conditionItems.add(new SceneSettingConditionItemAdapter.ConditionItem(condition));
        }
        mConditionAdapter.setNewData(conditionItems);

        List<SceneSettingActionItemAdapter.ActionItem> actionItems = new ArrayList<>();
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            actionItems.add(new SceneSettingActionItemAdapter.ActionItem(action));
        }
        mActionAdapter.setNewData(actionItems);
    }

    private void syncSourceAndAdapter2() {
        List<BaseSceneBean.DeviceCondition> conditions = new ArrayList<>();
        for (BaseSceneBean.Condition condition : mRuleEngineBean.getConditions()) {
            if (condition instanceof BaseSceneBean.DeviceCondition) {
                conditions.add((BaseSceneBean.DeviceCondition) condition);
            }
        }
        List<BaseSceneBean.DeviceAction> actions = new ArrayList<>();
        for (BaseSceneBean.Action action : mRuleEngineBean.getActions()) {
            if (action instanceof BaseSceneBean.DeviceAction) {
                actions.add((BaseSceneBean.DeviceAction) action);
            }
        }
        mPresenter.loadFunctionDetail(mRuleEngineBean.getScopeId(), conditions, actions);
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
                            CustomToast.makeText(getBaseContext(), "名称不能为空", R.drawable.ic_toast_warming);
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

    private Intent addDeviceConditionIntent() {
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

        return mainActivity;
    }

    private void doJumpAddConditions() {
        Intent mainActivity = addDeviceConditionIntent();
        startActivity(mainActivity);
    }

    @OnClick(R.id.v_add_action)
    public void jumpAddActions() {
        if (mRuleEngineBean instanceof LocalSceneBean && TempUtils.getDeviceSourceFromDeviceType(((LocalSceneBean) mRuleEngineBean).getTargetGatewayType()) == 1) {//如果是米兰网关的本地联动，只能添加设备类型的动作，所以不去类型选择页面。
            doJumpAddDeviceActions();
        } else {
            Intent mainActivity = new Intent(this, RuleEngineActionTypeGuideActivity.class);
            mainActivity.putExtra("data", mRuleEngineBean);
            mainActivity.putExtra("scopeId", mRuleEngineBean.getScopeId());
            startActivityForResult(mainActivity, REQUEST_CODE_SELECT_ACTION_TYPE);
        }
    }

    private Intent addDeviceActionIntent() {
        Intent mainActivity = new Intent(this, SceneSettingDeviceSelectActivity.class);
        mainActivity.putExtra("scopeId", mRuleEngineBean.getScopeId());
        mainActivity.putExtra("type", 1);

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
        return mainActivity;
    }

    private void doJumpAddDeviceActions() {
        Intent intent = addDeviceActionIntent();
        startActivity(intent);
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
        }).setTitle("确认删除").setContent("确认后将永久的从列表中移除该场景，请谨慎操作！").show(getSupportFragmentManager(), "delete");
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

    private void mergeDeviceConditionItem(BaseSceneBean.DeviceCondition
                                                  conditionItem, DeviceListBean.DevicesBean deviceBean,
                                          DeviceTemplateBean.AttributesBean attributesBean,
                                          ISceneSettingFunctionDatumSet.CallBackBean datumBean) {
        if (datumBean instanceof ISceneSettingFunctionDatumSet.ValueCallBackBean) {
            conditionItem.setSourceDeviceId(deviceBean.getDeviceId());
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.SWITCH_PURPOSE_SUB_DEVICE);
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.INFRARED_VIRTUAL_SUB_DEVICE);
            } else if (deviceBean.getCuId() == 0) {
                conditionItem.setSourceDeviceType(DeviceType.AYLA_DEVICE_ID);
            } else if (deviceBean.getCuId() == 1) {
                conditionItem.setSourceDeviceType(DeviceType.ALI_DEVICE_ID);
            }
            conditionItem.setRightValue(((ISceneSettingFunctionDatumSet.ValueCallBackBean) datumBean).getValueBean().getValue());
            conditionItem.setLeftValue(attributesBean.getCode());
            conditionItem.setOperator(datumBean.getOperator());
            conditionItem.setFunctionName(attributesBean.getDisplayName());
            conditionItem.setValueName(((ISceneSettingFunctionDatumSet.ValueCallBackBean) datumBean).getValueBean().getDisplayName());
        }
        if (datumBean instanceof ISceneSettingFunctionDatumSet.BitValueCallBackBean) {
            conditionItem.setSourceDeviceId(deviceBean.getDeviceId());
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.SWITCH_PURPOSE_SUB_DEVICE);
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.INFRARED_VIRTUAL_SUB_DEVICE);
            } else if (deviceBean.getCuId() == 0) {
                conditionItem.setSourceDeviceType(DeviceType.AYLA_DEVICE_ID);
            } else if (deviceBean.getCuId() == 1) {
                conditionItem.setSourceDeviceType(DeviceType.ALI_DEVICE_ID);
            }
            conditionItem.setRightValue(String.valueOf(((ISceneSettingFunctionDatumSet.BitValueCallBackBean) datumBean).getBitValueBean().getValue()));
            conditionItem.setLeftValue(attributesBean.getCode());
            conditionItem.setOperator(datumBean.getOperator());
            conditionItem.setFunctionName(attributesBean.getDisplayName());
            conditionItem.setValueName(((ISceneSettingFunctionDatumSet.BitValueCallBackBean) datumBean).getBitValueBean().getDisplayName());
            conditionItem.setBit(((ISceneSettingFunctionDatumSet.BitValueCallBackBean) datumBean).getBitValueBean().getBit());
            conditionItem.setCompareValue(((ISceneSettingFunctionDatumSet.BitValueCallBackBean) datumBean).getBitValueBean().getCompareValue());
        }
        if (datumBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
            conditionItem.setSourceDeviceId(deviceBean.getDeviceId());
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.SWITCH_PURPOSE_SUB_DEVICE);
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                conditionItem.setSourceDeviceType(DeviceType.INFRARED_VIRTUAL_SUB_DEVICE);
            } else if (deviceBean.getCuId() == 0) {
                conditionItem.setSourceDeviceType(DeviceType.AYLA_DEVICE_ID);
            } else if (deviceBean.getCuId() == 1) {
                conditionItem.setSourceDeviceType(DeviceType.ALI_DEVICE_ID);
            }
            conditionItem.setRightValue(((ISceneSettingFunctionDatumSet.SetupCallBackBean) datumBean).getTargetValue());
            conditionItem.setLeftValue(attributesBean.getCode());
            conditionItem.setOperator(datumBean.getOperator());
            conditionItem.setFunctionName(attributesBean.getDisplayName());
            conditionItem.setValueName(((ISceneSettingFunctionDatumSet.SetupCallBackBean) datumBean).getTargetValue() + ((ISceneSettingFunctionDatumSet.SetupCallBackBean) datumBean).getSetupBean().getUnit());
        }
    }

    private void mergeDeviceActionItem(BaseSceneBean.DeviceAction
                                               actionItem, DeviceListBean.DevicesBean deviceBean,
                                       DeviceTemplateBean.AttributesBean attributesBean,
                                       ISceneSettingFunctionDatumSet.CallBackBean callBackBean) {
        if (callBackBean instanceof ISceneSettingFunctionDatumSet.ValueCallBackBean) {
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                actionItem.setTargetDeviceType(DeviceType.SWITCH_PURPOSE_SUB_DEVICE);
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                actionItem.setTargetDeviceType(DeviceType.INFRARED_VIRTUAL_SUB_DEVICE);
            } else if (deviceBean.getCuId() == 0) {
                actionItem.setTargetDeviceType(DeviceType.AYLA_DEVICE_ID);
            } else if (deviceBean.getCuId() == 1) {
                actionItem.setTargetDeviceType(DeviceType.ALI_DEVICE_ID);
            }
            actionItem.setTargetDeviceId(deviceBean.getDeviceId());
            actionItem.setRightValueType(((ISceneSettingFunctionDatumSet.ValueCallBackBean) callBackBean).getValueBean().getDataType());
            actionItem.setOperator(callBackBean.getOperator());
            actionItem.setLeftValue(attributesBean.getCode());
            actionItem.setRightValue(((ISceneSettingFunctionDatumSet.ValueCallBackBean) callBackBean).getValueBean().getValue());
            actionItem.setFunctionName(attributesBean.getDisplayName());
            actionItem.setValueName(((ISceneSettingFunctionDatumSet.ValueCallBackBean) callBackBean).getValueBean().getDisplayName());
        }
        if (callBackBean instanceof ISceneSettingFunctionDatumSet.SetupCallBackBean) {
            if (TempUtils.isSWITCH_PURPOSE_SUB_DEVICE(deviceBean)) {
                actionItem.setTargetDeviceType(DeviceType.SWITCH_PURPOSE_SUB_DEVICE);
            } else if (TempUtils.isINFRARED_VIRTUAL_SUB_DEVICE(deviceBean)) {
                actionItem.setTargetDeviceType(DeviceType.INFRARED_VIRTUAL_SUB_DEVICE);
            } else if (deviceBean.getCuId() == 0) {
                actionItem.setTargetDeviceType(DeviceType.AYLA_DEVICE_ID);
            } else if (deviceBean.getCuId() == 1) {
                actionItem.setTargetDeviceType(DeviceType.ALI_DEVICE_ID);
            }
            actionItem.setTargetDeviceId(deviceBean.getDeviceId());
            actionItem.setRightValueType(attributesBean.getDataType());
            actionItem.setOperator(callBackBean.getOperator());
            actionItem.setLeftValue(attributesBean.getCode());
            actionItem.setRightValue(((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getTargetValue());
            actionItem.setFunctionName(attributesBean.getDisplayName());
            actionItem.setValueName(((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getTargetValue() + ((ISceneSettingFunctionDatumSet.SetupCallBackBean) callBackBean).getSetupBean().getUnit());
        }
    }

    /**
     * 处理设备类型的条件、动作 变化
     *
     * @param sceneItemEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleSceneItemEvent(SceneItemEvent sceneItemEvent) {
        DeviceListBean.DevicesBean deviceBean = MyApplication.getInstance().getDevicesBean(sceneItemEvent.deviceId);
        DeviceTemplateBean.AttributesBean attributesBean = sceneItemEvent.attributesBean;
        ISceneSettingFunctionDatumSet.CallBackBean datumBean = sceneItemEvent.callBackBean;

        if (sceneItemEvent.condition) {
            if (sceneItemEvent.editMode) {
                int position = sceneItemEvent.editPosition;
                if (position >= 0) {
                    if (mRuleEngineBean.getConditions().get(position) instanceof BaseSceneBean.DeviceCondition) {
                        BaseSceneBean.DeviceCondition conditionItem = (BaseSceneBean.DeviceCondition) mRuleEngineBean.getConditions().get(position);
                        mergeDeviceConditionItem(conditionItem, deviceBean, attributesBean, datumBean);
                        showData();
                    }
                }
            } else {
                BaseSceneBean.DeviceCondition conditionItem = new BaseSceneBean.DeviceCondition();
                mergeDeviceConditionItem(conditionItem, deviceBean, attributesBean, datumBean);
                mRuleEngineBean.getConditions().add(conditionItem);
                showData();
            }
        } else {
            if (sceneItemEvent.editMode) {
                int position = sceneItemEvent.editPosition;
                if (position >= 0) {
                    BaseSceneBean.DeviceAction actionItem = (BaseSceneBean.DeviceAction) mRuleEngineBean.getActions().get(position);
                    mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean);
                    showData();
                }
            } else {
                BaseSceneBean.DeviceAction actionItem = new BaseSceneBean.DeviceAction();
                mergeDeviceActionItem(actionItem, deviceBean, attributesBean, datumBean);
                mRuleEngineBean.getActions().add(actionItem);
                showData();
            }
        }
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
        if (requestCode == REQUEST_CODE_SELECT_ACTION_TYPE && resultCode == RESULT_OK) {//选择动作类型返回结果
            int type = data.getIntExtra("type", 0);
            switch (type) {
                case 0: {
                    doJumpAddDeviceActions();
                }
                break;
                case 1: {
                    startActivityForResult(new Intent(this, SceneActionDelaySettingActivity.class), REQUEST_CODE_SET_DELAY_ACTION);
                }
                break;
                case 2: {
                    startActivityForResult(new Intent(this, RuleEngineActionHotelWelcomeActivity.class), REQUEST_CODE_HOTEL_WELCOME_ACTION);
                }
                break;
            }
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
        if (requestCode == REQUEST_CODE_SET_DELAY_ACTION && resultCode == RESULT_OK) {//延时动作添加返回
            int seconds = data.getIntExtra("seconds", 0);
            BaseSceneBean.DelayAction delayAction = new BaseSceneBean.DelayAction();
            delayAction.setRightValue(String.valueOf(seconds));
            mRuleEngineBean.getActions().add(delayAction);
            showData();
        }
        if (requestCode == REQUEST_CODE_EDIT_DELAY_ACTION && resultCode == RESULT_OK) {//延时动作编辑返回
            int seconds = data.getIntExtra("seconds", 0);
            int position = data.getIntExtra("position", -1);
            if (position >= 0) {
                BaseSceneBean.Action action = mRuleEngineBean.getActions().get(position);
                if (action instanceof BaseSceneBean.DelayAction) {
                    action.setRightValue(String.valueOf(seconds));
                    showData();
                }
            }
        }
        if (requestCode == REQUEST_CODE_HOTEL_WELCOME_ACTION && resultCode == RESULT_OK) {//酒店欢迎语动作添加返回
            BaseSceneBean.WelcomeAction welcomeAction = new BaseSceneBean.WelcomeAction();
            mRuleEngineBean.getActions().add(welcomeAction);
            showData();
        }
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
