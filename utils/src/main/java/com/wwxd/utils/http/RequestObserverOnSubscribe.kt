package com.wwxd.utils.http

import android.text.TextUtils
import com.wwxd.utils.R
import com.wwxd.utils.StringUtil
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import okhttp3.Call

/**
 * user：LuHao
 * time：2019/11/26 14:56
 * describe：子线程表示的，请求准备就绪，同步请求
 */
class RequestObserverOnSubscribe(//是否异步
    private val isSync: Boolean, private val call: Call
) : ObservableOnSubscribe<String> {
    override fun subscribe(emitter: ObservableEmitter<String>) {
        if (isSync) {
            val responseCallBack = ResponseCallBack(emitter)
            call.enqueue(responseCallBack)
        } else {
            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    if (response.body != null && !TextUtils.isEmpty(response.body!!.string())) {
                        emitter.onNext(response.body!!.string())
                    }
                } else {
                    emitter.onError(Throwable(StringUtil.getString(R.string.str_request_error)))
                }
            } catch (e: Exception) {
                emitter.onError(Throwable(e.message))
            }
        }
    }
}