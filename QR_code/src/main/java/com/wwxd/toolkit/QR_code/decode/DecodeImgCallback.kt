package com.wwxd.toolkit.QR_code.decode

import com.google.zxing.Result

/**
 * Created by yzq on 2017/10/18.
 *
 *
 * 解析图片的回调
 */
interface DecodeImgCallback {
    fun onImageDecodeSuccess(result: Result)
    fun onImageDecodeFailed()
}