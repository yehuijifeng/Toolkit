package com.wwxd.toolkit.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import kotlin.reflect.KClass

/**
 * user：LuHao
 * time：2020/11/25 14:05
 * describe：activity的基类
 */
abstract class BaseActivity : AppCompatActivity() {
    //layout资源文件
    protected abstract fun setContentView(): Int
    protected abstract fun init()

    //是否全屏
    protected open fun isFullWindow(): Boolean {
        return false
    }

    //底部导航栏的背景色
    protected open fun getNavigationBarColor(): Int {
        return R.color.getNavigationBarColor
    }

    //顶部状态栏背景色
    protected open fun getStatusBarColor(): Int {
        return R.color.getStatusBarColor
    }

    //顶部状态栏字体高亮显示；
    //true，浅色，白色；
    //false，深色，黑色
    protected open fun statusBarTextColor(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //屏幕方向垂直
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //是否全屏
        if (isFullWindow()) {
            //去除导航栏栏
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                window.statusBarColor = Color.TRANSPARENT
            } else {
                //去除状态栏
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        } else {
            //设置顶部状态栏字体颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置状态栏颜色
                window.statusBarColor =
                    ResourcesCompat.getColor(resources, getStatusBarColor(), null)
                //底部导航栏的颜色
                window.navigationBarColor =
                    ResourcesCompat.getColor(resources, getNavigationBarColor(), null)
            }
        }
        //通知栏字体颜色；true,白色;false,黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (statusBarTextColor())
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            else
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(setContentView())
        init()
    }

    protected fun startActivity(cla: KClass<*>) {
        startActivity(cla, null)
    }

    protected fun startActivity(cls: KClass<*>, bundle: Bundle?) {
        val intent = Intent(this@BaseActivity, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
    }

    protected fun startActivityForResult(cls: KClass<*>, requestCode: Int) {
        startActivityForResult(cls, null, requestCode)
    }

    protected fun startActivityForResult(cls: KClass<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this@BaseActivity, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }
}