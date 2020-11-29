package com.wwxd.toolkit.QR_code.decode

import android.os.Looper
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.ResultPointCallback
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * 这个线程解码图像的所有重担，解码线程
 */
class DecodeThread(resultPointCallback: ResultPointCallback) : Thread() {
    private val hints: Hashtable<DecodeHintType, Any>
    private val handlerInitLatch: CountDownLatch
    var multiFormatReader: MultiFormatReader
        private set

//    fun getHandler(): Handler? {
//        try {
//            handlerInitLatch.await()
//        } catch (ie: InterruptedException) {
//            ie.printStackTrace()
//        }
//        return handler
//    }

    override fun run() {
        Looper.prepare()
        multiFormatReader.setHints(hints)
        handlerInitLatch.countDown()
        Looper.loop()
    }

    init {
        multiFormatReader = MultiFormatReader()
        handlerInitLatch = CountDownLatch(1)
        val decodeFormats = Vector<BarcodeFormat>()
        decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
        decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        hints = Hashtable()
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
        hints[DecodeHintType.NEED_RESULT_POINT_CALLBACK] = resultPointCallback
    }


}