package com.ayla.hotelsaas.ui

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
import com.ayla.hotelsaas.localBean.BaseSceneBean
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_onekeylinkagelist_select.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

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
        mOneKeyAdapter.setEmptyView(R.layout.empty_scene_page)
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

    }

    override fun initListener() {
        mOneKeyAdapter.setOnItemClickListener(BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            val onekeyBean: BaseSceneBean = adapter.data.get(position) as BaseSceneBean
            startActivity<SceneSettingActivity>()
            EventBus.getDefault().post(OneKeyRulerBean(onekeyBean))
            finish()
        })
    }


}

