package com.ayla.hotelsaas.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.view.View
import com.ayla.hotelsaas.base.BaseViewModelActivity
import com.ayla.hotelsaas.databinding.ActivitySceneLinkAddGuideBinding
import com.ayla.hotelsaas.page.common.Keys
import com.ayla.hotelsaas.page.weidge.SceneStoreSiteDialog
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

class SceneLinkAddGuideActivity : BaseViewModelActivity<ActivitySceneLinkAddGuideBinding>() {


    override fun getViewBinding(): ActivitySceneLinkAddGuideBinding? = ActivitySceneLinkAddGuideBinding.inflate(layoutInflater)


    /**
     * 仅初始化一些与界面相关的操作
     */
    override fun init(savedInstanceState: Bundle?) {
        val roomId = intent.getLongExtra(Keys.ROOM_ID,0)
        binding.constraintLayout.setOnClickListener(View.OnClickListener {//添加一键执行
            startActivityForResult<SceneAddOneKeyActivity>(1001, Keys.ROOM_ID to roomId)
        })

        binding.constraintLayoutTwo.setOnClickListener(View.OnClickListener {//添加自动化场景
            SceneStoreSiteDialog
                    .newInstance(object : SceneStoreSiteDialog.DoneCallback {
                        override fun localStoreSite() {
                            startActivity<SceneCloudLinkActivity>()
                        }

                        override fun cloudSerivedialog() {//云端创建
                            startActivity<GatewaySelectActivity>()
                        }

                    })
                    .setTitle("存储类型")
                    .show(supportFragmentManager, "scene_name")
        })
    }

    /**
     * 处理特殊事件，比如登录异常
     */
    override fun handleSpecialEvent(code: Int) {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

}