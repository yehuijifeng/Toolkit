package com.wwxd.utils.http

import com.wwxd.utils.R
import com.wwxd.utils.StringUtil
import io.reactivex.rxjava3.core.ObservableEmitter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

/**
 * user：LuHao
 * time：2019/11/26 14:46
 * describe：get/post请求返回监听
 */
class ResponseCallBack(private val emitter: ObservableEmitter<String>) : Callback {

    override fun onFailure(call: Call, e: IOException) {
        //请求失败
        emitter.onError(Throwable(StringUtil.getString(R.string.str_request_error)))
    }

    override fun onResponse(call: Call, response: Response) {
        //请求成功
        try {
            if (response.isSuccessful && response.body != null) {
                emitter.onNext(response.body!!.string())
            } else {
                emitter.onError(Throwable(StringUtil.getString(R.string.str_request_error)))
            }
        } catch (e: Exception) {
            emitter.onError(Throwable(e.message))
        }
    }
}