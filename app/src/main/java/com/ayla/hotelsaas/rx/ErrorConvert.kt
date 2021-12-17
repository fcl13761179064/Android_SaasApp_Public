package com.ayla.hotelsaas.rx

import com.ayla.base.rx.BaseException
import rx.Observable
import rx.functions.Func1

/**
 * @ClassName:  ErrorConvert
 * @Description:
 * @Author: vi1zen
 * @CreateDate: 2020/10/19 14:39
 */
class ErrorConvert<T> : Func1<Throwable,Observable<T>>{
    override fun call(t: Throwable?): Observable<T> {
        return if(t is BaseException){
            Observable.error(t)
        }else{
            Observable.error(BaseException(-1, t?.message ?: "服务异常，请检查网络后重试"))
        }
    }
}