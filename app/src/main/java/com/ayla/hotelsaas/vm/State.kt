package com.ayla.hotelsaas.vm

/**
 *
 * @Author:         HuaZhongWei
 * @CreateDate:     2020/4/16 16:52
 *
 * @Description:    页面状态
 *
 */

enum class State {
    //标题
    TITLE,

    //加载
    LOADING,

    //内容，加载完成
    CONTENT,

    //错误
    ERROR,

    //网络错误
    NET_ERROR,

    //空
    EMPTY
}