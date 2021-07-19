package com.ayla.hotelsaas.widget;

import android.graphics.Rect;
import android.net.wifi.ScanResult;
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
import com.ayla.hotelsaas.databinding.LayoutFilterWifiDialogBinding;
import com.ayla.hotelsaas.ui.ApWifiConnectToA2GagtewayActivity;
import com.ayla.hotelsaas.utils.RecycleViewDivider;
import com.blankj.utilcode.util.SizeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class FilterWifiDialog extends DialogFragment {
    LayoutFilterWifiDialogBinding binding;

    List data = new ArrayList<ScanResult>();

    private int defaultIndex = -1;//默认选中的下标

    private Callback callback;

    private @DrawableRes
    int iconRes;

    private String title, subTitle;
    private int LocationType;
    private CheckableSupport currentSupport;
    private esss adapter;
    private List<CheckableSupport> supports;

    public static FilterWifiDialog newInstance() {
        Bundle args = new Bundle();
        FilterWifiDialog fragment = new FilterWifiDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = LayoutFilterWifiDialogBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public void setMeasuredDimension(Rect childrenBounds, int wSpec, int hSpec) {
                super.setMeasuredDimension(childrenBounds, wSpec, View.MeasureSpec.makeMeasureSpec(SizeUtils.dp2px(300), View.MeasureSpec.AT_MOST));
            }
        });
        supports = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            currentSupport = new CheckableSupport(data.get(i));
            if (i == defaultIndex) {
                currentSupport.setChecked(true);
            }
            supports.add(currentSupport);
        }
        adapter = new esss(R.layout.filter_itme_picker_enum, supports);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (int i = 0; i < supports.size(); i++) {
                    supports.get(i).setChecked(false);
                }
                currentSupport = (CheckableSupport) adapter.getData().get(position);
                defaultIndex = position;
                currentSupport.setChecked(true);
                adapter.notifyDataSetChanged();
            }

        });
        binding.rv.setAdapter(adapter);
        binding.rv.addItemDecoration(new RecycleViewDivider(getContext(), LinearLayoutManager.VERTICAL, 3, R.color.all_bg_color));
     /*   binding.rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int size = SizeUtils.dp2px(10);
                int position = parent.getChildAdapterPosition(view);

                outRect.set(0, (position == 0) ? size : 0, 0, size);
            }
        });*/
       /* if (LocationType == 1000) {
            binding.imageView2.setVisibility(View.INVISIBLE);
        } else {
            binding.imageView2.setImageResource(iconRes);
            binding.imageView2.setVisibility(View.VISIBLE);
        }*/

        binding.imageView2.setVisibility(View.VISIBLE);
        binding.textView3.setText(subTitle);
        binding.SmartRefreshLayout.setEnableLoadMore(false);
        binding.SmartRefreshLayout.setEnableRefresh(true);
        binding.SmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                setRefreshData();
            }
        });
        binding.tvConfire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    dismissAllowingStateLoss();
                    if (defaultIndex == -1) {
                        return;
                    }
                    ScanResult data = (ScanResult) supports.get(defaultIndex).getData();
                    callback.onCallback(data);
                }
            }
        });
    }


    private void setRefreshData() {
        WifiUtils wifiUtil = WifiUtils.getInstance(getContext());
        List<ScanResult> scanResultList = wifiUtil.getWifiScanResult();
        supports = new ArrayList<>();
        for (int i = 0; i < scanResultList.size(); i++) {
            currentSupport = new CheckableSupport(scanResultList.get(i));
            supports.add(currentSupport);
        }
        if (adapter != null && adapter.getData() != null) {
            adapter.getData().clear();
            adapter.setNewData(supports);
            adapter.loadMoreComplete();
            if (binding.SmartRefreshLayout.isRefreshing()) {
                binding.SmartRefreshLayout.finishRefresh();
            }
        }
    }

    public class TestDividerItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

//        //如果不是第一个，则设置top的值。
            if (parent.getChildAdapterPosition(view) != 0) {
                //这里直接硬编码为1px
                outRect.top = 1;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        params.windowAnimations = R.style.main_menu_animstyle;
        params.width = (int) (getResources().getDisplayMetrics().widthPixels / 1.2);
        params.gravity = Gravity.CENTER;
        getDialog().getWindow().setAttributes(params);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public FilterWifiDialog setData(List data) {
        this.data = data;
        return this;
    }

    public FilterWifiDialog setIconRes(int iconRes) {
        this.iconRes = iconRes;
        return this;
    }

    public FilterWifiDialog setLocationIconRes(int iconRes, int LocationType) {
        this.LocationType = LocationType;
        this.iconRes = iconRes;
        return this;
    }

    public FilterWifiDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public FilterWifiDialog setSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public FilterWifiDialog setDefaultIndex(int defaultIndex) {
        this.defaultIndex = defaultIndex;
        return this;
    }


    public FilterWifiDialog setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    private static class esss extends BaseQuickAdapter<CheckableSupport, BaseViewHolder> {


        public esss(int layoutResId, @Nullable List<CheckableSupport> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, CheckableSupport item) {
            String ssid = ((ScanResult) item.getData()).SSID;
            helper.setText(R.id.tv, ssid);
            helper.setVisible(R.id.iv, item.isChecked());
            int level = ((ScanResult) item.getData()).level;
            String capabilities = ((ScanResult) item.getData()).capabilities;
            if (capabilities.contains("WEP")) {
                helper.setVisible(R.id.iv_wifi_password, true);

            } else if (capabilities.contains("PSK")) {
                helper.setVisible(R.id.iv_wifi_password, true);

            } else if (capabilities.contains("EAP")) {

                helper.setVisible(R.id.iv_wifi_password, true);
            } else if (capabilities.contains("WPA")) {

                helper.setVisible(R.id.iv_wifi_password, true);
            } else if (capabilities.contains("WPA2")) {

                helper.setVisible(R.id.iv_wifi_password, true);
            } else {  //不加密
                helper.setVisible(R.id.iv_wifi_password, false);
            }
            if (level < -70) {
                helper.setImageResource(R.id.iv_wifi_info, R.mipmap.wifi_info_one);
            } else if (level < -50 && level > -70) {
                helper.setImageResource(R.id.iv_wifi_info, R.mipmap.wifi_info_two);
            } else {
                helper.setImageResource(R.id.iv_wifi_info, R.mipmap.ap_wifi_three);
            }

        }
    }

    public interface Callback<T> {
        void onCallback(T object);
    }
}
