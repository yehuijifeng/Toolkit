package com.wwxd.utils.http

import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

/**
 * user：LuHao
 * time：2019/11/26 14:17
 * describe：下载文件请求的回调，需要变成主线程表示
 */
class DownloadObserver(private val iHttpDownload: IHttpDownload?) : Observer<String> {
    override fun onSubscribe(d: Disposable) {
        //订阅
    }

    override fun onNext(saveFilePath: String) {
        //下载成功
        if (iHttpDownload != null)
            iHttpDownload.success(saveFilePath)
    }

    override fun onError(e: Throwable) {
        if (iHttpDownload != null)
            iHttpDownload.failure(e.message!!)
    }

    override fun onComplete() {
        //完成
    }
}