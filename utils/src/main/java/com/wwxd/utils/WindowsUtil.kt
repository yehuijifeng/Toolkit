package com.wwxd.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.WindowManager
import com.wwxd.base.AppConstant

/**
 * user：LuHao
 * time：2019/11/11 10:16
 * describe：窗口测量工具
 */
object WindowsUtil {

    //获得状态栏的高度
    fun getStatusBarHeight(): Int {
        val resourceId: Int = AppConstant.getApp().getResources()
            .getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            return AppConstant.getApp().getResources().getDimensionPixelSize(resourceId)
        }
        return 0
    }


    //底部状态栏NavigationBar的高度
    fun getNavigationBarHeight(): Int {
        if (existNavigationBar()) {
            val resources: Resources = AppConstant.getApp().getResources()
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    private var hasNavigationBar = -1

    //获取是否存在NavigationBar
    private fun existNavigationBar(): Boolean {
        if (hasNavigationBar == -1) {
            try {
                val resources: Resources = AppConstant.getApp().getResources()
                val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
                if (id > 0) {
                    hasNavigationBar = if (resources.getBoolean(id)) 1 else 0
                }
                @SuppressLint("PrivateApi") val systemPropertiesClass =
                    Class.forName("android.os.SystemProperties")
                val m = systemPropertiesClass.getMethod("get", String::class.java)
                val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
                if ("1" == navBarOverride) {
                    hasNavigationBar = 0
                } else if ("0" == navBarOverride) {
                    hasNavigationBar = 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return hasNavigationBar == 1
    }

    private var windowWidth = 0

    /**
     * 屏幕的宽度，包含左右侧边栏
     *
     * @param context Context for get WindowManager
     * @return Display
     */
    fun getWindowWidth(context: Context): Int {
        if (windowWidth > 0) return windowWidth
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val outPoint = Point()
        defaultDisplay.getRealSize(outPoint)
        windowWidth = outPoint.x
        return windowWidth
    }

    private var windowHeight = 0

    /**
     * 屏幕的高度，包含上下导航栏
     *
     * @param context Context for get WindowManager
     * @return Display
     */
    fun getWindowHeight(context: Context): Int {
        if (windowHeight > 0) return windowHeight
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val defaultDisplay = windowManager.defaultDisplay
        val outPoint = Point()
        defaultDisplay.getRealSize(outPoint)
        windowHeight = outPoint.y
        return windowHeight
    }

    private var windowRatio = 0.0

    //获得手机屏幕的宽高比
    fun getWindowRatio(activity: Activity): Double {
        if (windowRatio > 0) return windowRatio
        val metric = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metric)
        val width = metric.widthPixels.toFloat() // 屏幕宽度（像素）
        val height = metric.heightPixels.toFloat() // 屏幕高度（像素）
        //        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5，小米4的是3.0）
//        float densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240,小米4的是480）
        val ratio = width / height
        windowRatio = CalculateUtil.retain(ratio.toDouble(), 2)
        return windowRatio
    }
}