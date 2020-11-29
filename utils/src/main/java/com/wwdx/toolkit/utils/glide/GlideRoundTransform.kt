package com.wwdx.toolkit.utils.glide

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.wwxd.toolkit.BuildConfig
import java.security.MessageDigest

/**
 * user：LuHao
 * time：2019/11/19 18:10
 * describe：画圆角
 */
class GlideRoundTransform(//半径
    private var radius: Float, cornerTypes: Array<CornerType>
) : BitmapTransformation() {
    //画的角度
    private var cornerTypes: Array<CornerType>
    fun setRadiusAndCornerTypes(dp: Float, cornerTypes: Array<CornerType>) {
        radius = dp
        this.cornerTypes = cornerTypes
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        return roundCrop(pool, toTransform, radius, *cornerTypes)!!
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        val ID = BuildConfig.LIBRARY_PACKAGE_NAME + "GlideRoundedCornersTransform." + 1
        val ID_BYTES = ID.toByteArray(CHARSET)
        messageDigest.update(ID_BYTES)
    }

    /**
     * 裁剪图片
     *
     * @param pool   glide对象
     * @param source 图片
     * @return 裁剪后的图片
     */
    private fun roundCrop(
        pool: BitmapPool,
        source: Bitmap?,
        mRadius: Float,
        vararg cornerTypes: CornerType
    ): Bitmap? {
        if (source == null) {
            return null
        }
        val width = source.width
        val height = source.height
        val result = pool[width, height, Bitmap.Config.ARGB_8888]
        val canvas = Canvas(result)
        val paint = Paint()
        paint.shader =
            BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        val path = Path()
        drawRoundRect(canvas, paint, path, width, height, mRadius, *cornerTypes)
        return result
    }

    /**
     * 画圆角
     *
     * @param canvas      画布
     * @param paint       油漆
     * @param path        画笔
     * @param width       宽
     * @param height      高
     * @param mRadius     圆角角度
     * @param cornerTypes 画哪些角
     */
    private fun drawRoundRect(
        canvas: Canvas,
        paint: Paint,
        path: Path,
        width: Int,
        height: Int,
        mRadius: Float,
        vararg cornerTypes: CornerType
    ) {
        //rids 圆角的半径，依次为左上角xy半径，右上角xy半径，右下角xy半径，左下角xy半径
        val rids: FloatArray
        if (cornerTypes == null || cornerTypes.size == 0) rids = floatArrayOf(
            mRadius,
            mRadius,
            mRadius,
            mRadius,
            mRadius,
            mRadius,
            mRadius,
            mRadius
        ) else {
            rids = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            for (cornerType in cornerTypes) {
                when (cornerType) {
                    CornerType.ALL -> {
                        rids[0] = mRadius
                        rids[1] = mRadius
                        rids[2] = mRadius
                        rids[3] = mRadius
                        rids[4] = mRadius
                        rids[5] = mRadius
                        rids[6] = mRadius
                        rids[7] = mRadius
                    }
                    CornerType.TOP_LEFT -> {
                        rids[0] = mRadius
                        rids[1] = mRadius
                    }
                    CornerType.TOP_RIGHT -> {
                        rids[2] = mRadius
                        rids[3] = mRadius
                    }
                    CornerType.BOTTOM_RIGHT -> {
                        rids[4] = mRadius
                        rids[5] = mRadius
                    }
                    CornerType.BOTTOM_LEFT -> {
                        rids[6] = mRadius
                        rids[7] = mRadius
                    }
                    else -> {
                        rids[0] = mRadius
                        rids[1] = mRadius
                        rids[2] = mRadius
                        rids[3] = mRadius
                        rids[4] = mRadius
                        rids[5] = mRadius
                        rids[6] = mRadius
                        rids[7] = mRadius
                    }
                }
            }
        }
        drawPath(rids, canvas, paint, path, width, height)
    }

    /**
     * 画圆角
     *
     * @param rids   四个角的角度
     * @param canvas 画布
     * @param paint  油漆
     * @param path   画笔
     * @param width  宽
     * @param height 高
     */
    private fun drawPath(
        rids: FloatArray,
        canvas: Canvas,
        paint: Paint,
        path: Path,
        width: Int,
        height: Int
    ) {
        path.addRoundRect(RectF(0F, 0F, width.toFloat(), height.toFloat()), rids, Path.Direction.CW)
        //canvas.clipPath(path);
        canvas.drawPath(path, paint)
    }

    init {
        //通过默认角度算出半径
        this.cornerTypes = cornerTypes
    }
}