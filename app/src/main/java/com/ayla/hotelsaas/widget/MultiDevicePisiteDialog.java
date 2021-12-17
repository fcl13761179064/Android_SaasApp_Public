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

    private int defaultIndex = -1;//默认选中的下标

    private Callback callback;



    private String title;

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
        esss adapter = new esss(R.layout.activity_multi_device_bind, supports);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dismissAllowingStateLoss();
                if (callback != null) {
                    callback.onCallback(data.get(position));
                }
            }
        });
        binding.rv.setAdapter(adapter);
        binding.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });
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
            helper.setText(R.id.tv, item.getData().toString());
            helper.setVisible(R.id.iv, item.isChecked());
        }
    }

    public interface Callback<T> {
        void onCallback(T object);
    }
}
