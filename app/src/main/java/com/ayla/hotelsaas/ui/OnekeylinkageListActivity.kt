package com.ayla.hotelsaas.ui

import androidx.recyclerview.widget.RecyclerView
import com.ayla.hotelsaas.R
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseMvpActivity
import com.ayla.hotelsaas.mvp.present.OneKeyPresenter
import com.ayla.hotelsaas.mvp.view.OneKeyView
import kotlinx.android.synthetic.main.activity_onekeylinkagelist_select.*

class OnekeylinkageListActivity  : BaseMvpActivity<OneKeyView, OneKeyPresenter>(),OneKeyView{

    override fun getLayoutId(): Int {
        return  R.layout.activity_onekeylinkagelist_select
    }

    override fun initView() {
        recyclerview?.setRecyclerListener(
                RecyclerView.RecyclerListener {
       val getmOneKeyRelueBean = MyApplication.getInstance().getmOneKeyRelueBean()

                })
    }

    override fun initListener() {
    }

    override fun initPresenter(): OneKeyPresenter {

        return OneKeyPresenter()
    }

    override fun runSceneSuccess(needWarming: Boolean) {
    }

    override fun runSceneFailed() {

    }
}