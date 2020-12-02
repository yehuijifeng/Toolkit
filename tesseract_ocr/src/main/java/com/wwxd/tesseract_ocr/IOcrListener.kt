package com.wwxd.tesseract_ocr

import com.baidu.ocr.sdk.exception.OCRError

/**
 * user：LuHao
 * time：2020/12/2 15:44
 * describe：图片识别结果
 */
interface IOcrListener {
    fun onSuccess(content:String)
    fun onError(error: OCRError)
}