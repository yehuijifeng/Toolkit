package com.wwxd.utils.glide

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.wwxd.toolkit.BuildConfig
import java.security.MessageDigest

/**
 * user：LuHao
 * time：2019/11/20 10:08
 * describe：画圆形
 */
class GlideCircleTransform : BitmapTransformation() {
    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return circleCrop(pool, toTransform)!!
    }

    private fun circleCrop(pool: BitmapPool, source: Bitmap?): Bitmap? {
        if (source == null) return null
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squared = Bitmap.createBitmap(source, x, y, size, size)
        val result = pool[size, size, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader =
            BitmapShader(squared, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val ID = BuildConfig.LIBRARY_PACKAGE_NAME + "GlideRoundedCornersTransform." + 1
        val ID_BYTES = ID.toByteArray(CHARSET)
        messageDigest.update(ID_BYTES)
    }
}