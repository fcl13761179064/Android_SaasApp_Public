package com.ayla.hotelsaas.viewmodel

import androidx.lifecycle.ViewModel
import com.ayla.hotelsaas.data.net.RetrofitHelper


/**
 * @ClassName:  MainViewModel
 * @Description:主页ViewModel
 * @Author: vi1zen
 * @CreateDate: 2021/3/26 13:56
 */
class MainViewModel : ViewModel() {

    private val api = RetrofitHelper.getApiService();

}