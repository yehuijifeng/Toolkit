package com.wwxd.qr_code1.decode

import com.google.zxing.Result
// 解析图片的回调
interface DecodeImgCallback {
    fun onImageDecodeSuccess(result: Result)
    fun onImageDecodeFailed()
}