package com.ayla.hotelsaas.ui.activities.remote_scene;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.local_scene.LocalSceneSelectGatewayAdapter;
import com.ayla.hotelsaas.application.MyApplication;
import com.ayla.hotelsaas.base.BaseNewViewModelActivity;
import com.ayla.hotelsaas.bean.DeviceListBean;
import com.ayla.hotelsaas.databinding.ActivityLocalSceneSelectGatewayBinding;
import com.ayla.hotelsaas.vm.AbsViewModel;
import com.ayla.hotelsaas.constant.KEYS;
import com.ayla.hotelsaas.utils.CustomToast;
import com.ayla.hotelsaas.utils.CommonUtils;
import com.ayla.hotelsaas.widget.common_dialog.InputContentDialog;
import com.blankj.utilcode.util.SizeUtils;

import java.util.List;

import butterknife.Unbinder;

/**
 * 添加节点，选择所属网关的页面
 * 可选参数：
 * sourceId <0时 ，不区分网关所属云。
 * 返回 网关的deviceId
 */
public class SelectA6GatewayActivity extends BaseNewViewModelActivity<ActivityLocalSceneSelectGatewayBinding, AbsViewModel> {
    private LocalSceneSelectGatewayAdapter mAdapter;
    private DeviceListBean.DevicesBean currentGateway;
    private Unbinder bind;

    @Nullable
    @Override
    public ActivityLocalSceneSelectGatewayBinding getViewBinding() {
        return ActivityLocalSceneSelectGatewayBinding.inflate(getLayoutInflater());
    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        getBinding().gatewayAppbar.setCenterText("选择网关");
        mAdapter = new LocalSceneSelectGatewayAdapter(R.layout.item_gate_way_list);
        getBinding().rv.setLayoutManager(new LinearLayoutManager(this));
        getBinding().rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(12);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        List<DeviceListBean.DevicesBean> devicesBeans = MyApplication.getInstance().a6GatewayData();
        mAdapter.setNewData(devicesBeans);
        getBinding().rv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            showSetTTSTextDialog("");
            currentGateway = mAdapter.getItem(position);
        });
    }

    private void showSetTTSTextDialog(String content) {
        InputFilter inputLengthFilter = CommonUtils.INSTANCE.getInputLengthFilter(1024);
        InputFilter[] inputFilters = new InputFilter[]{inputLengthFilter};
        new InputContentDialog().setTitle("语音播报文本")
                .setEditHint("请输入要播报的文本")
                .setEditValue(content)
                .setMaxLength(1024)
                .setInputFilter(inputFilters)
                .setOperateListener(new InputContentDialog.OperateListener() {
                    @Override
                    public void confirm(String content) {
                        Intent intent = new Intent();
                        intent.putExtra(KEYS.TTSCONTENT, content);
                        if (currentGateway != null) {
                            intent.putExtra(KEYS.DEVICEID, currentGateway.getDeviceId());
                            intent.putExtra(KEYS.CUID, currentGateway.getCuId());
                        }
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void contentOverMaxLength() {
                        CustomToast.makeText(
                                SelectA6GatewayActivity.this,
                                "字符超出限制",
                                R.drawable.ic_warning
                        );
                    }

                }).show(getSupportFragmentManager(), "TTS");

    }
}
