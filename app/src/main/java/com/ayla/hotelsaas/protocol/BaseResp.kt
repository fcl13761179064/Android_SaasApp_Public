package com.ayla.base.data.protocol

/*
    基础响应对象
    @status:响应状态码
    @message:响应文字消息
    @data:具体响应业务对象
 */
data class BaseResp<out T>(val code:Int, val msg:String?, val data:T)

data class BasePage<out T>(val currentPage:Int, val pageSize:Int, val totalCount:Int,val totalPages:Int,val pages:T)

