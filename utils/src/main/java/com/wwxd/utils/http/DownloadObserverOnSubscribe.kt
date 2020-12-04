package com.wwxd.utils.http

import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import okhttp3.Call

/**
 * user：LuHao
 * time：2019/11/26 14:56
 * describe：子线程表示的，下载文件，异步请求
 */
class DownloadObserverOnSubscribe(
    private val call: Call,
    private val saveFilePath: String,
    private val iHttpDownload: IHttpDownload?
) : ObservableOnSubscribe<String> {
    override fun subscribe(emitter: ObservableEmitter<String>) {
        val downloadCallBack = DownloadCallBack(emitter, saveFilePath, iHttpDownload)
        call.enqueue(downloadCallBack)
    }
}