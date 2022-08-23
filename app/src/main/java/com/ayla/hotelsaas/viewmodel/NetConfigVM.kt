package com.ayla.hotelsaas.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ayla.hotelsaas.vm.AbsViewModel
import com.blankj.utilcode.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NetConfigVM : AbsViewModel() {

    fun getWifiEnable(): MutableLiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(NetworkUtils.getWifiEnabled())
        }
        return result
    }

}