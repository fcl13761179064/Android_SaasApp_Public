package com.ayla.hotelsaas


import com.ayla.hotelsaas.autoserivice.IAppModuleService
import com.blankj.utilcode.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * 项目名称: KmmZrApp
 * @ClassName:
 * @Description:
 * @Author: finlay
 * @CreateDate: 2022/12/2 16:13
 */
@AutoService(IAppModuleService::class)
class StartMoudleServiceImpl : IAppModuleService {

    override fun getRemark(name: String){
      ToastUtils.showShort(name)
    }

}