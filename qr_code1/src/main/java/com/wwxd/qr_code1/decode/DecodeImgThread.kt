package com.wwxd.qr_code1.decode

import android.graphics.Bitmap
import android.net.Uri
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.wwxd.utils.FileUtil
import java.util.*

/**
 * Created by yzq on 2017/10/17.
 * 解析二维码图片
 * 解析是耗时操作，要放在子线程
 */
class DecodeImgThread(
    private val imgUri: Uri?, /*回调*/
    private val callback: DecodeImgCallback?
) : Thread() {

    /*图片路径*/
    override fun run() {
        super.run()
        if (imgUri == null || callback == null) return
        //对图片进行裁剪，防止oom
        try {
            val bitmap = FileUtil.uriToBitmap(imgUri)
            if (bitmap != null) {
                val multiFormatReader = MultiFormatReader()
                // 解码的参数
                val hints = Hashtable<DecodeHintType, Any?>(2)
                // 可以解析的编码类型
                val decodeFormats = Vector<BarcodeFormat>()
                // 扫描的类型  一维码和二维码
                decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
                decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
                decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
                hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
                // 设置继续的字符编码格式为UTF8
                hints[DecodeHintType.CHARACTER_SET] = "UTF8"
                // 设置解析配置参数
                multiFormatReader.setHints(hints)
                // 开始对图像资源解码
                val bitmapLuminanceSource = BitmapLuminanceSource(bitmap)
                val hybridBinarizer = HybridBinarizer(bitmapLuminanceSource)
                val binaryBitmap = BinaryBitmap(hybridBinarizer)
                val rawResult = multiFormatReader.decodeWithState(binaryBitmap)
                if (rawResult != null) {
                    callback.onImageDecodeSuccess(rawResult)
                } else {
                    callback.onImageDecodeFailed()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class BitmapLuminanceSource(bitmap: Bitmap) :
        LuminanceSource(bitmap.width, bitmap.height) {
        private val bitmapPixels: ByteArray
        override fun getMatrix(): ByteArray {
            // 返回我们生成好的像素数据
            return bitmapPixels
        }

        override fun getRow(y: Int, row: ByteArray): ByteArray {
            // 这里要得到指定行的像素数据
            System.arraycopy(bitmapPixels, y * width, row, 0, width)
            return row
        }

        init {
            // 首先，要取得该图片的像素数组内容
            val data = IntArray(bitmap.width * bitmap.height)
            bitmapPixels = ByteArray(bitmap.width * bitmap.height)
            bitmap.getPixels(data, 0, width, 0, 0, width, height)

            // 将int数组转换为byte数组，也就是取像素值中蓝色值部分作为辨析内容
            for (i in data.indices) {
                bitmapPixels[i] = data[i].toByte()
            }
        }
    }


}