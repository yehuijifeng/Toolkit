package com.wwxd.utils.http

import android.text.TextUtils
import com.wwxd.utils.R
import com.wwxd.utils.StringUtil
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * user：LuHao
 * time：2019/11/26 14:17
 * describe：异步请求的回调，需要变成主线程表示
 * string: 泛型是用于json直接解析成该对象
 */
class ResponseObserver(api: Api) : Observer<String> {
    private val iHttpResponse: IHttpResponse?

    init {
        iHttpResponse = api.iHttpResponse
    }

    override fun onSubscribe(d: Disposable) {
        //订阅
    }

    override fun onNext(json: String?) {
        //发送数据
        if (iHttpResponse != null) {
            val error = StringUtil.getString(R.string.str_request_error)
            if (TextUtils.isEmpty(json)) {
                iHttpResponse.onFailure(error)
            } else {
                iHttpResponse.onSuccess(json!!)
            }
        }
    }

    override fun onError(e: Throwable) {
        //错误
        if (iHttpResponse != null) {
            iHttpResponse.onFailure(e.message!!)
        }
    }

    override fun onComplete() {
        //完成
    }

}