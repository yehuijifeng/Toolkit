package com.wwxd.utils.http

/**
 * user：LuHao
 * time：2019/11/18 14:10
 * describe：网络请求的回调结果
 */
interface IHttpResponse {
    fun onSuccess(json: String)
    fun onFailure(error: String)
}