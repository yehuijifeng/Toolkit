package com.wwxd.toolkit.QR_code.decode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.wwdx.toolkit.utils.AppFile
import com.wwdx.toolkit.utils.AppUtil.isAndroidQ
import com.wwdx.toolkit.utils.DateUtil.getServerTime
import com.wwdx.toolkit.utils.FileUtil.insertFile
import com.wwdx.toolkit.utils.LogUtil.e
import com.wwxd.toolkit.base.AppConstant.getApp
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
            val imageExif = getImageExifInterface(imgUri)
            var imageWidth = imageExif!![0]
            var imageHeight = imageExif[1]
            val rotate = imageExif[2]
            if (rotate == 90 || rotate == 270) {
                imageHeight = imageWidth
                imageWidth = imageHeight
            }
            if (imageWidth == 0 || imageHeight == 0) return
            //对于手机自己的图片才生效
            val imageSampleSize = getImageSampleSize(imageWidth, imageHeight)
            val options = BitmapFactory.Options()
            options.inSampleSize = imageSampleSize[2]
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888 //optional
            options.inInputShareable = true //当系统内存不够时候图片自动被回收
            val input = getApp().contentResolver.openInputStream(imgUri) ?: return
            var bitmap = BitmapFactory.decodeStream(input, null, options) ?: return
            if (Math.abs(rotate) > 0) //旋转图片
                bitmap = rotaingImageView(rotate, bitmap)
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

    /**
     * 旋转图片
     *
     * @param angle  旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    private fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        // 旋转图片 动作
        val bitmap1 = bitmap
        val matrix = Matrix()
        //第二种方法
        val bitmapWidth = bitmap1.width
        val bitmapHeight = bitmap1.height
        //中心旋转
        matrix.setRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, bitmapWidth, bitmapHeight, matrix, true)
    }

    //计算图片的宽、高、旋转角度
    //该方法可以获得手机拍照和图片的旋转角度，但无法获得其他来源图片的宽高和旋转角度
    private fun getImageExifInterface(imageUri: Uri): IntArray? {
        val imageExif = IntArray(3)
        try {
            var input = getApp().contentResolver.openInputStream(imageUri)
            if (input != null) {
                val exifInterface: ExifInterface
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    exifInterface = ExifInterface(input)
                } else {
                    val file = insertFile(
                        input,
                        AppFile.IMAGE_CACHE.ObtainAppFilePath() + "QR_code_" + getServerTime() + ".jpg"
                    )
                    if (file != null) {
                        exifInterface = ExifInterface(file.absolutePath)
                        if (!file.delete()) {
                            e("File delete error!")
                        }
                    } else {
                        return null
                    }
                }
                imageExif[0] = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0)
                imageExif[1] = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0)
                if (imageExif[0] == 0 || imageExif[1] == 0) {
                    //说明该图片不是本机图片
                    input = getApp().contentResolver.openInputStream(imageUri)
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    options.inPurgeable = true // 同时设置才会有效
                    options.inInputShareable = true //当系统内存不够时候图片自动被回收
                    BitmapFactory.decodeStream(input, null, options)
                    imageExif[0] = options.outWidth
                    imageExif[1] = options.outHeight
                    imageExif[2] = 0
                } else {
                    var rotate = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    rotate = when (rotate) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        ExifInterface.ORIENTATION_NORMAL -> 0
                        else -> 0
                    }
                    imageExif[2] = rotate
                }
                input?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imageExif
    }

    //获得图片缩放比例
    private fun getImageSampleSize(imageWidth: Int, imageHeight: Int): IntArray {
        var imageWidth = imageWidth
        var imageHeight = imageHeight
        val imageExif = IntArray(3)
        val maxImageHeight = 1920
        val maxImageWidth = 1080
        var inSampleSize = 1
        if (imageHeight > maxImageHeight) {
            val bili = imageHeight.toFloat() / maxImageHeight
            imageHeight = maxImageHeight
            imageWidth = (imageWidth / bili).toInt()
            inSampleSize = Math.ceil(bili.toDouble()).toInt()
        } else if (imageWidth > maxImageWidth) {
            val bili = imageWidth.toFloat() / maxImageWidth
            imageWidth = maxImageWidth
            imageHeight = (imageHeight / bili).toInt()
            inSampleSize = Math.ceil(bili.toDouble()).toInt()
        }
        imageExif[0] = imageWidth
        imageExif[1] = imageHeight
        imageExif[2] = inSampleSize
        return imageExif
    }

}