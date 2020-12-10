package com.wwxd.utils.glide

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.renderscript.RSRuntimeException
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.wwxd.utils.R
import jp.wasabeef.glide.transformations.internal.FastBlur
import jp.wasabeef.glide.transformations.internal.RSBlur
import java.io.File
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*

/**
 * user：LuHao
 * time：2019/11/19 17:08
 * describe：glide加载图片
 */
object GlideUtil {
    /**
     * ---------------------缓存策略
     * diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
     * ALL，内存、磁盘全部缓存；
     * NONE，全部不缓存；
     * DATA，原图缓存磁盘；
     * RESOURCE，解码后的图缓存到磁盘；
     * AUTOMATIC，自动模式，根据源去自行选择缓存模式
     *
     *
     * --------------------跳过内存缓存
     * skipMemoryCache(true)
     * true 跳过内存缓存
     * false 不跳过
     *
     *
     * --------------------图片展示方式
     * fitCenter()//铺满
     * centerCrop()//剪切，显示部分
     *
     *
     * --------------------图片默认底图
     * placeholder(res)
     *
     *
     * --------------------图片默认加载失败填充图
     * error(res)
     *
     *
     * --------------------不加载过渡动画
     * dontAnimate()
     *
     *
     * --------------------设置图片长宽
     * override(width, height)
     *
     *
     * --------------------加载缩略图。0-1之间
     * thumbnail(sizeMultiplier)
     *
     *
     * --------------------设置图片圆角
     * transform(new CenterCrop(), new GlideRoundTransform(round, cornerTypes))
     *
     *
     * --------------------设置图片圆形
     * transform(new FitCenter(), new GlideRoundTransform(round, cornerTypes))
     *
     *
     */
    private var glideRoundTransform //设置圆角
            : GlideRoundTransform? = null

    /**
     * 设置圆形
     */
    private var glideCircleTransform //设置圆形
            : GlideCircleTransform? = null
        private get() {
            if (field == null) {
                field = GlideCircleTransform()
            }
            return field
        }

    /**
     * 设置圆角
     *
     * @param round       圆角角度
     * @param cornerTypes 圆角方向，八个角
     */
    private fun getGlideRoundTransform(
        round: Float,
        cornerTypes: Array<CornerType>
    ): GlideRoundTransform? {
        if (glideRoundTransform == null) {
            glideRoundTransform = GlideRoundTransform(round, cornerTypes)
        } else {
            glideRoundTransform!!.setRadiusAndCornerTypes(round, cornerTypes)
        }
        return glideRoundTransform
    }

    /**
     * 设置圆角
     *
     * @param round 圆角角度
     */
    private fun getGlideRoundTransform(round: Float): GlideRoundTransform? {
        return getGlideRoundTransform(round, arrayOf(CornerType.ALL))
    }

    /**
     * 将圆角角度传换成dp
     */
    private fun getRoundDp(context: Context, round: Float): Float {
        return if (cacheDipsMap.containsKey(round)) cacheDipsMap[round]!! else {
            val scale = context.resources.displayMetrics.density
            val round1 = round * scale + 0.5f
            cacheDipsMap[round] = round1
            round1
        }
    }

    private val cacheDipsMap: MutableMap<Float, Float> = HashMap()

    /**
     * 显示圆角图片
     */
    fun showRound(imageView: ImageView?, res: Int, round: Float) {
        if (imageView == null || res == 0) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(res)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(imageView: ImageView?, url: String?, round: Float) {
        if (imageView == null || TextUtils.isEmpty(url)) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_round_image_error)
            .placeholder(R.drawable.bg_round_image_back)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRoundFit(imageView: ImageView?, url: String?, round: Float) {
        if (imageView == null || TextUtils.isEmpty(url)) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_round_image_error)
            .placeholder(R.drawable.bg_round_image_back)
            .transform(FitCenter(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(imageView: ImageView?, url: String?, width: Int, height: Int, round: Float) {
        if (imageView == null || TextUtils.isEmpty(url)) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(url)
            .override(width, height)
            .error(R.drawable.bg_round_image_error)
            .placeholder(R.drawable.bg_round_image_back)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(
        imageView: ImageView?,
        drawable: Drawable?,
        width: Int,
        height: Int,
        round: Float
    ) {
        if (imageView == null || drawable == null) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(drawable)
            .override(width, height)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(imageView: ImageView?, uri: Uri?, round: Float) {
        if (imageView == null || uri == null) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(uri)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(
        imageView: ImageView?,
        uri: Uri?,
        width: Int,
        height: Int,
        round: Float,
        loadRes: Int
    ) {
        if (imageView == null || uri == null) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(uri)
            .override(width, height)
            .placeholder(loadRes)
            .error(loadRes)
            .transform(CenterCrop(), getGlideRoundTransform(scale))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    fun show(
        imageView: ImageView?, width: Int,
        height: Int, res: Int
    ) {
        if (imageView == null || res == 0) return
        Glide.with(imageView.context)
            .load(res)
            .override(width, height)
            .transform(CenterCrop())
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(
        imageView: ImageView?,
        url: String?,
        round: Float,
        cornerTypes: Array<CornerType>
    ) {
        if (imageView == null || TextUtils.isEmpty(url)) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_round_image_error)
            .placeholder(R.drawable.bg_round_image_back)
            .transform(CenterCrop(), getGlideRoundTransform(scale, cornerTypes))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆角图片
     */
    fun showRound(
        imageView: ImageView?,
        url: String?,
        width: Int,
        height: Int,
        round: Float,
        cornerTypes: Array<CornerType>
    ) {
        if (imageView == null || TextUtils.isEmpty(url)) return
        val scale = getRoundDp(imageView.context, round)
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_round_image_error)
            .placeholder(R.drawable.bg_round_image_back)
            .override(width, height)
            .transform(CenterCrop(), getGlideRoundTransform(scale, cornerTypes))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    /**
     * 显示圆形图片
     */
    fun showCircular(imageView: ImageView, url: String?) {
        try {
            Glide.with(imageView.context)
                .load(url)
                .error(R.drawable.bg_circular_image_error)
                .placeholder(R.drawable.bg_circular_image_back)
                .transform(CenterCrop(), glideCircleTransform)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示圆形图片
     */
    fun showCircular(imageView: ImageView, res: Int) {
        Glide.with(imageView.context)
            .load(res)
            .transform(CenterCrop(), glideCircleTransform)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    fun getUri(uriId: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, uriId)
    }

    fun show(imageView: ImageView, uriId: Long) {
        val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, uriId)
        show(imageView, uri)
    }

    /**
     * 显示图片
     */
    fun show(imageView: ImageView, uri: Uri) {
        Glide.with(imageView.context)
            .load(uri)
            .transform(FitCenter())
            .error(R.drawable.bg_def_image_error)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    /**
     * 显示图片
     */
    fun show(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_def_image_error)
            .placeholder(R.drawable.bg_def_image_back)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
    }

    /**
     * 显示图片
     */
    fun show(imageView: ImageView, file: File) {
        Glide.with(imageView.context)
            .load(file)
            .error(R.drawable.bg_def_image_error)
            .placeholder(R.drawable.bg_def_image_back)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }

    //清除glide的缓存
    fun cleanCache(context: Context) {
        OnCleanCacheThread(context).start()
        Glide.get(context).clearMemory()
    }

    fun showBlurImage(imageView: ImageView, url: String?) {
        //设置图片控件的高斯模糊效果
        Glide.with(imageView.context)
            .load(url)
            .error(R.drawable.bg_circular_image_error)
            .placeholder(R.color.transparent)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .transform(BlurTransformation(imageView.context, 25), glideCircleTransform)
            .into(imageView)
    }

    private class OnCleanCacheThread(private val context: Context) : Thread() {
        override fun run() {
            Glide.get(context).clearDiskCache()
        }
    }

    private class BlurTransformation(context: Context, radius: Int) : BitmapTransformation() {
        private val STRING_CHARSET_NAME = "UTF-8"
        private val ID = "com.kevin.glidetest.BlurTransformation"
        private val CHARSET = Charset.forName(STRING_CHARSET_NAME)
        private val ID_BYTES = ID.toByteArray(CHARSET)
        private val DEFAULT_DOWN_SAMPLING = 1
        private val mContext: Context
        private val mBitmapPool: BitmapPool
        private val mRadius: Int
        private val mSampling: Int
        override fun transform(
            pool: BitmapPool,
            toTransform: Bitmap,
            outWidth: Int,
            outHeight: Int
        ): Bitmap {
            val width = toTransform.width
            val height = toTransform.height
            val scaledWidth = width / mSampling
            val scaledHeight = height / mSampling
            var bitmap = mBitmapPool[scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888]
            val canvas = Canvas(bitmap)
            canvas.scale(1 / mSampling.toFloat(), 1 / mSampling.toFloat())
            val paint = Paint()
            paint.flags = Paint.FILTER_BITMAP_FLAG
            canvas.drawBitmap(toTransform, 0f, 0f, paint)
            bitmap = try {
                RSBlur.blur(mContext, bitmap, mRadius)
            } catch (e: RSRuntimeException) {
                FastBlur.blur(bitmap, mRadius, true)
            }
            //return BitmapResource.obtain(bitmap, mBitmapPool);
            return bitmap
        }

        override fun hashCode(): Int {
            return ID.hashCode()
        }

        override fun equals(obj: Any?): Boolean {
            return obj is BlurTransformation
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            messageDigest.update(ID_BYTES)
        }

        init {
            mContext = context.applicationContext
            mBitmapPool = Glide.get(context).bitmapPool
            mRadius = radius
            mSampling = DEFAULT_DOWN_SAMPLING
        }
    }
}