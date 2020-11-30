package com.wwdx.toolkit.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.wwxd.toolkit.base.AppConstant

/**
 * Created by Yehuijifeng
 * on 2015/10/27.
 * dp/px/sp互换
 */
object DisplayUtil {
    private var dm: DisplayMetrics? = null
    fun getDisplayMetrics(): DisplayMetrics {
        if (dm == null) {
            dm = DisplayMetrics()
            val wm = AppConstant.getApp().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(dm)
        }
        return dm!!
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    fun px2dip(pxValue: Float): Float {
        val scale: Float = getDisplayMetrics().density
        return pxValue / scale + 0.5f
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dip2px(dipValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, getDisplayMetrics())
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(pxValue: Float): Float {
        val fontScale: Float = getDisplayMetrics().scaledDensity
        return pxValue / fontScale + 0.5f
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(spValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, getDisplayMetrics())
    }

    fun getWindowWidth(): Int {
        return getDisplayMetrics().widthPixels
    }

    fun getWindowHeight(): Int {
        return getDisplayMetrics().heightPixels
    }
}