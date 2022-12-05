package com.ayla.hotelsaas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ayla.hotelsaas.autoserivice.StartMoudleService
import com.blankj.utilcode.util.ToastUtils
import kotlinx.android.synthetic.main.auto_service.*


class TestAutoServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auto_service)
        test.setOnClickListener{
          StartMoudleService.loadCycleModuleServices()?.getRemark("ddssdsds")

        }
    }
}