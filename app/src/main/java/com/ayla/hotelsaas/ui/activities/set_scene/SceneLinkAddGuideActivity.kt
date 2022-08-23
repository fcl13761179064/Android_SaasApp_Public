package com.ayla.hotelsaas.ui.activities.set_scene

import android.content.Intent
import android.os.Bundle
import com.ayla.hotelsaas.application.MyApplication
import com.ayla.hotelsaas.base.BaseViewModelActivity
import com.ayla.hotelsaas.bean.DeviceListBean.DevicesBean
import com.ayla.hotelsaas.databinding.ActivitySceneLinkAddGuideBinding
import com.ayla.hotelsaas.constant.KEYS
import com.ayla.hotelsaas.ext.singleClick
import com.ayla.hotelsaas.widget.common_dialog.SceneStoreSiteDialog
import com.ayla.hotelsaas.ui.activities.local_scene.LocalSceneGatewaySelectActivity
import com.ayla.hotelsaas.ui.activities.local_scene.LocalSceneSettingActivity
import com.ayla.hotelsaas.ui.activities.onekey.SceneAddOneKeyActivity
import com.ayla.hotelsaas.ui.activities.remote_scene.RemoteSceneSettingActivity
import com.ayla.hotelsaas.utils.TempUtils
import com.ayla.hotelsaas.widget.scene_dialog.TooltipBuilder
import org.jetbrains.anko.startActivityForResult
import java.util.*

class SceneLinkAddGuideActivity : BaseViewModelActivity<ActivitySceneLinkAddGuideBinding>() {


    override fun getViewBinding(): ActivitySceneLinkAddGuideBinding? =
        ActivitySceneLinkAddGuideBinding.inflate(layoutInflater)


    /**
     * 仅初始化一些与界面相关的操作
     */
    override fun init(savedInstanceState: Bundle?) {
        val roomId = intent.getLongExtra(KEYS.ROOM_ID, 0)
        binding.constraintLayout.singleClick {//添加一键执行
            startActivityForResult<SceneAddOneKeyActivity>(1001, KEYS.ROOM_ID to roomId)
        }

        binding.constraintLayoutTwo.singleClick {//添加自动化场景
            SceneStoreSiteDialog
                .newInstance(object : SceneStoreSiteDialog.DoneCallback {
                    override fun localStoreSite() {
                        dealToLocalSceneSet()
                    }

                    override fun cloudSerivedialog() {//云端创建
                         dealToRemoteSceneSet()

                    }

                })
                .setTitle("存储类型")
                .show(supportFragmentManager, "scene_name")
        }
    }

    private fun showTooltipDialog(title: String, content: String) {
        TooltipBuilder().setTitle(title).setContent(content).setShowLeftButton(false).show(supportFragmentManager,"tips")
    }

    private fun dealToRemoteSceneSet() {//处理云端联动
        val roomId = intent.getLongExtra(KEYS.ROOM_ID, 0)
        startActivityForResult<RemoteSceneSettingActivity>(1000, KEYS.ROOM_ID to roomId)
    }
    private fun dealToLocalSceneSet() {
        //选择了本地联动
        val gateways: MutableList<DevicesBean> = ArrayList()
        val devicesBean = MyApplication.getInstance().devicesBean ?: return
        for (bean in devicesBean.devices) {
            if (TempUtils.isDeviceGateway(bean)) {
                gateways.add(bean)
            }
        }
        if (gateways.isNotEmpty()) { //存在网关
            if (gateways.size == 1) {
                val gateway = gateways[0]
                if (TempUtils.isDeviceOnline(gateway)) { //网关在线
                    val intent = Intent(this, LocalSceneSettingActivity::class.java)
                    intent.putExtra("targetGateway", gateway.deviceId)
                    startActivityForResult(intent, 1000)
                } else {
                    showTooltipDialog("网关离线", "创建场景需要网关保持在线")
                    return
                }
            } else {
                val intent = Intent(this, LocalSceneGatewaySelectActivity::class.java)
                startActivityForResult(intent, 999)
                return
            }
        } else {
            showTooltipDialog("未添加网关", "添加网关后才能创建场景")
            return
        }

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
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
        if (requestCode == 999 && resultCode == RESULT_OK) {
            val intent = Intent(this, LocalSceneSettingActivity::class.java)
            data?.let {
                intent.putExtra("targetGateway", it.getStringExtra("deviceId"))
            }
            startActivityForResult(intent, 1000)
        }
    }

}