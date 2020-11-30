package com.wwxd.utils.photo

import android.content.Context
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.wwxd.utils.photo.ImageLookViewAttacher.*

class ImageLookView : AppCompatImageView, IImageLookView {

    private var mAttacher: ImageLookViewAttacher?
    private var mPendingScaleType: ScaleType? = null
    private var isGif = false


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attr: AttributeSet?) : super(context, attr) {
        init()
    }

    constructor(context: Context, attr: AttributeSet?, defStyle: Int) : super(
        context,
        attr,
        defStyle
    ) {
        init()
    }

    private fun init() {
        super.setScaleType(ScaleType.MATRIX)
        mAttacher = ImageLookViewAttacher(this)
        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType)
            mPendingScaleType = null
        }
    }

    fun setGif(gif: Boolean) {
        isGif = gif
    }

    override fun canZoom(): Boolean {
        return if (isGif) false else mAttacher!!.canZoom()
    }

    override val displayRect: RectF
        get() = mAttacher!!.displayRect!!
    override var minScale: Float
        get() = mAttacher!!.minScale
        set(minScale) {
            mAttacher!!.minScale = minScale
        }
    override var midScale: Float
        get() = mAttacher!!.midScale
        set(midScale) {
            mAttacher!!.midScale = midScale
        }
    override var maxScale: Float
        get() = mAttacher!!.maxScale
        set(maxScale) {
            mAttacher!!.maxScale = maxScale
        }
    override val scale: Float
        get() = mAttacher!!.scale


    override fun setAllowParentInterceptOnEdge(allow: Boolean) {
        mAttacher!!.setAllowParentInterceptOnEdge(allow)
    }

    // setImageBitmap calls through to this method
    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        mAttacher?.update()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        mAttacher?.update()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        mAttacher?.update()
    }

    override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener?) {
        mAttacher!!.setOnMatrixChangeListener(listener)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        mAttacher!!.setOnLongClickListener(l)
    }

    override fun setOnPhotoTapListener(listener: OnPhotoTapListener?) {
        mAttacher!!.setOnPhotoTapListener(listener)
    }

    override fun setOnViewTapListener(listener: OnViewTapListener?) {
        mAttacher!!.setOnViewTapListener(listener)
    }


    override fun setZoomable(zoomable: Boolean) {
        mAttacher!!.setZoomable(zoomable)
    }

    override fun zoomTo(scale: Float, focalX: Float, focalY: Float) {
        mAttacher!!.zoomTo(scale, focalX, focalY)
    }

    override fun onDetachedFromWindow() {
        mAttacher!!.cleanup()
        super.onDetachedFromWindow()
    }

    override fun getScaleType(): ScaleType {
        return mAttacher!!.getScaleType()
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (null != mAttacher && scaleType != null) {
            mAttacher!!.setScaleType(scaleType)
        } else {
            mPendingScaleType = scaleType
        }
    }

    init {
        super.setScaleType(ScaleType.MATRIX)
        mAttacher = ImageLookViewAttacher(this)
        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType!!)
            mPendingScaleType = null
        }
    }
}