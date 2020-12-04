package com.wwxd.utils.http

/**
 * user：LuHao
 * time：2019/12/23 11:47
 * describe：下载成功/失败
 */
interface IHttpDownload {
    //下载进度
    fun onProgress(progress: Int)
    fun success(saveFilePath: String)
    fun failure(error: String)
}