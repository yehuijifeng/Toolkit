package com.wwxd.toolkit.QR_code.decode

import com.google.zxing.Result

// 解析图片的回调
interface DecodeImgCallback {
    fun onImageDecodeSuccess(result: Result)
    fun onImageDecodeFailed()
}