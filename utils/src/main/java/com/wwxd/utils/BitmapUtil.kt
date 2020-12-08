package com.wwxd.utils

import android.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import com.wwxd.base.AppConstant
import java.io.File


/**
 * user：LuHao
 * time：2020/12/3 11:49
 * describe：图片处理器
 */
object BitmapUtil {
    /**
     * 将新添加的文件通知更新系统相册
     */
    fun updatePhonePictures(imageFilePath: String): Boolean {
        return try {
            val file = File(imageFilePath)
            MediaStore.Images.Media.insertImage(
                AppConstant.getApp().getContentResolver(),
                file.absolutePath,
                file.name,
                AppUtil.appName()
            )
            AppConstant.getApp()
                .sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //等比缩放
    fun zoomBitmap(bitmap: Bitmap, w: Int, h: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidht = w.toFloat() / width
        val scaleHeight = h.toFloat() / height
        matrix.postScale(scaleWidht, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    //等比缩放到屏幕等宽
    fun zoomBitmapWindowWidth(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleWidht = DisplayUtil.getWindowWidth().toFloat() / width
        matrix.postScale(scaleWidht, scaleWidht)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    //等比缩放到屏幕等宽
    fun zoomBitmapWindowHeight(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        val scaleHeight = DisplayUtil.getWindowHeight().toFloat() / height
        matrix.postScale(scaleHeight, scaleHeight)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    //将资源文件转化为bitmap
    fun createBitmap(bitmapRes: Int): Bitmap {
        return BitmapFactory.decodeResource(
            AppConstant.getApp().getResources(),
            bitmapRes,
            null
        )
    }
}