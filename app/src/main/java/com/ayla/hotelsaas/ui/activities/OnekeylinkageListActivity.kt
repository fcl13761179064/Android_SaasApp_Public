package com.ayla.hotelsaas.ui.activities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.adapter.OneKeyRuleEngineListAdapter
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BasicActivity
import com.ayla.hotelsaas.events.OneKeyRulerBean
import com.ayla.hotelsaas.bean.scene_bean.BaseSceneBean
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_onekeylinkagelist_select.*
import org.greenrobot.eventbus.EventBus




class OnekeylinkageListActivity : BasicActivity() {

    private val mOneKeyAdapter by lazy { OneKeyRuleEngineListAdapter() }

    override fun getLayoutId(): Int {
        return R.layout.activity_onekeylinkagelist_select
    }

    override fun getLayoutView(): View? {
        return null
    }


    override fun initView() {
        mOneKeyAdapter.bindToRecyclerView(recyclerview)
        recyclerview.setLayoutManager(LinearLayoutManager(this))
        mOneKeyAdapter.setEmptyView(R.layout.scene_one_key_empty_page_status)
        recyclerview.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                val size = SizeUtils.dp2px(10f)
                val position = parent.getChildAdapterPosition(view)
                outRect[0, if (position == 0) size else 0, 0] = size
            }
        })

        val mOneKeyRelueBean = MyApplication.getInstance().getmOneKeyRelueBean()
        mOneKeyAdapter.addData(mOneKeyRelueBean);
        //获取头布局尾布局的View
        //获取头布局尾布局的View
        val headView: View = layoutInflater.inflate(R.layout.onekey_show_head_desc, null)
        //添加头布局尾布局
        mOneKeyAdapter.addHeaderView(headView);
    }

    override fun initListener() {
        mOneKeyAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val onekeyBean: BaseSceneBean = adapter.data.get(position) as BaseSceneBean
            EventBus.getDefault().post(OneKeyRulerBean(onekeyBean))
            finish()
        })
    }


}

