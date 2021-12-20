package com.ayla.hotelsaas.rx

import com.ayla.base.rx.BaseException
import com.ayla.hotelsaas.common.ResultCode
import com.ayla.hotelsaas.page.ext.isLoginExpire
import com.ayla.hotelsaas.protocol.BaseResp
import rx.Observable
import rx.functions.Func1

/**
 * @ClassName:  RespConvert
 * @Description:
 * @Author: vi1zen
 * @CreateDate: 2020/9/29 14:02
 */
@Suppress("UNCHECKED_CAST")
class RespConvert<T> : Func1<T,Observable<T>>{
    override fun call(t: T): Observable<T> {
        if(t is BaseResp<*>){
            if(t.code != ResultCode.SUCCESS){
                return Observable.error(BaseException(t.code,if(t.isLoginExpire()) "登录已过期" else t.msg ?: "服务异常"))
            }
        }
        return Observable.just(t)
    }
}