package com.wwxd.utils.http

import android.text.TextUtils
import com.wwxd.utils.R
import com.wwxd.utils.StringUtil
import io.reactivex.rxjava3.core.ObservableEmitter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * user：LuHao
 * time：2019/11/26 14:46
 * describe：下载返回监听
 */
class DownloadCallBack(
    private val emitter: ObservableEmitter<String>,
    private val saveFilePath: String,
    private val iHttpDownload: IHttpDownload?
) : Callback {
    override fun onFailure(call: Call, e: IOException) {
        //请求失败
        emitter.onError(e)
    }

    override fun onResponse(call: Call, response: Response) {
        //请求成功
        if (!TextUtils.isEmpty(saveFilePath) && response.isSuccessful && response.body != null) {
            try {
                val file = File(saveFilePath)
                if (file.exists()) {
                    emitter.onNext(saveFilePath)
                    return
                }
                val inputStream = response.body!!.byteStream()
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val total = response.body!!.contentLength()
                val buffer = ByteArray(1024)
                var len: Int
                var sum: Long = 0
                while (bis.read(buffer).also { len = it } != -1) {
                    fos.write(buffer, 0, len)
                    sum += len.toLong()
                    val progress = (sum * 1.0f / total * 100).toInt()
                    // 下载中更新进度条
                    iHttpDownload?.onProgress(progress)
                }
                fos.flush()
                fos.close()
                bis.close()
                inputStream.close()
                emitter.onNext(saveFilePath)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        } else {
            emitter.onError(Throwable(StringUtil.getString(R.string.str_download_error)))
        }
    }
}