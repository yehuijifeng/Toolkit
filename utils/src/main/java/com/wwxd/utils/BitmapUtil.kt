package com.wwxd.utils

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * user：LuHao
 * time：2020/12/3 11:49
 * describe：图片处理器
 */
object BitmapUtil {

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
}