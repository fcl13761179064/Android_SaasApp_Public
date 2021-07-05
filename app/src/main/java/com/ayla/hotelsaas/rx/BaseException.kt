package com.ayla.base.rx

/*
    定义通用异常
 */
class BaseException(val code:Int,val msg:String) :Throwable(){

    //判断是否是登录过期
    fun isLoginExpire() : Boolean{
        return code == 121001 || code == 121002
                || code == 121004 || code == 121007
                || code == 122001 || code == 122002
                || code == 122003 || code == 125001
    }
}
