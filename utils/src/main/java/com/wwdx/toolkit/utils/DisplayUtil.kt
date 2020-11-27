package com.wwdx.toolkit.utils

import com.wwxd.toolkit.base.AppConstant

/**
 * Created by Yehuijifeng
 * on 2015/10/27.
 * dp/px/sp互换
 */
object DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    fun px2dip(pxValue: Float): Float {
        val scale: Float = AppConstant.getApp().getResources().getDisplayMetrics().density
        return pxValue / scale + 0.5f
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    fun dip2px(dipValue: Float): Float {
        val scale: Float = AppConstant.getApp().getResources().getDisplayMetrics().density
        return dipValue * scale + 0.5f
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    fun px2sp(pxValue: Float): Float {
        val fontScale: Float = AppConstant.getApp().getResources().getDisplayMetrics().scaledDensity
        return pxValue / fontScale + 0.5f
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(spValue: Float): Float {
        val fontScale: Float = AppConstant.getApp().getResources().getDisplayMetrics().scaledDensity
        return spValue * fontScale + 0.5f
    }
}