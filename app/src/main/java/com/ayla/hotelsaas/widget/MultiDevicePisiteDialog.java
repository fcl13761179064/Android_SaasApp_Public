package com.ayla.hotelsaas.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayla.hotelsaas.R;
import com.ayla.hotelsaas.adapter.CheckableSupport;
import com.ayla.hotelsaas.bean.DeviceLocationBean;
import com.ayla.hotelsaas.databinding.DialogMultiDevicePositeSetBinding;
import com.ayla.hotelsaas.databinding.LayoutItemPickDialogBinding;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MultiDevicePisiteDialog extends DialogFragment {
    DialogMultiDevicePositeSetBinding binding;

    List data = new ArrayList<>();

    private int defaultIndex = 0;//默认选中的下标

    private Callback callback;


    private  String title;
    private CheckableSupport checkableSupport;

    public static MultiDevicePisiteDialog newInstance() {

        Bundle args = new Bundle();

        MultiDevicePisiteDialog fragment = new MultiDevicePisiteDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogMultiDevicePositeSetBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<CheckableSupport> supports = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            CheckableSupport support = new CheckableSupport(data.get(i));
            if (i == defaultIndex) {
                support.setChecked(true);
            }
            supports.add(support);
        }
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
        esss adapter = new esss(R.layout.item_scene_setting_function_datum_set, supports);
        adapter.bindToRecyclerView(binding.rv);
        binding.rv.setAdapter(adapter);
        adapter.setEmptyView(R.layout.empty_hongyan_device);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (callback != null) {
                    checkableSupport = supports.get(position);
                    defaultIndex = position;
                    for (int i = 0; i < supports.size(); i++) {
                        supports.get(i).setChecked(false);
                    }
                    checkableSupport.setChecked(true);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        binding.vDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null && checkableSupport != null && checkableSupport.getData() != null) {
                    DeviceLocationBean data = (DeviceLocationBean) checkableSupport.getData();
                    callback.doConfire(data);
                    dismissAllowingStateLoss();
                }else {
                    if (supports.size()>0){
                        DeviceLocationBean data = (DeviceLocationBean)supports.get(0).getData();
                        callback.doConfire(data);
                        dismissAllowingStateLoss();
                    }else {
                        DeviceLocationBean data = new DeviceLocationBean();
                                data.setRegionName("阳台");
                                data.setRegionId(1379984424178946048L);
                        callback.doConfire(data);
                        dismissAllowingStateLoss();
                    }
                }

            }
        });

        binding.rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dismissAllowingStateLoss();
            }
        });

        binding.tvName.setText(title);
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = getResources().getDisplayMetrics().widthPixels;
        params.gravity = Gravity.BOTTOM;
        getDialog().getWindow().setAttributes(params);
    }

    public MultiDevicePisiteDialog setData(List data) {
        this.data = data;
        return this;
    }

    public MultiDevicePisiteDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public MultiDevicePisiteDialog setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
        return this;
    }

    public MultiDevicePisiteDialog setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private static class esss extends BaseQuickAdapter<CheckableSupport, BaseViewHolder> {


        public esss(int layoutResId, @Nullable List<CheckableSupport> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CheckableSupport item) {
            helper.setText(R.id.tv_function_name, ((DeviceLocationBean) item.getData()).getRegionName());
            helper.setChecked(R.id.cb_function_checked, item.isChecked());
        }
    }

    public interface Callback {
        void doConfire(DeviceLocationBean object);
    }
}
