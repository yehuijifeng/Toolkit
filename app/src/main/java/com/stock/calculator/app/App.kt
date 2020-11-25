package com.stock.calculator.app

import android.app.Application
import com.stock.calculator.constant.AppConstant
import com.stock.calculator.utils.FileUtil
import com.stock.calculator.utils.SharedPreferencesUtil
import com.stock.calculator.utils.WindowsUtil

/**
 * user：LuHao
 * time：2019/10/27 11:46
 * describe：程序管理
 */
class App : Application() {
    //监听每一个activity的生命周期变化
    private var activityLifecycleListener: ActivityLifecycleListener? = null


    /* get/set方法 */
    //屏幕的宽,高
    var windowWidth = 0
        get() {
            if (field == 0) {
                field = WindowsUtil.getWindowWidth(this)
            }
            return field
        }
        private set
    var windowHeight = 0
        get() {
            if (field == 0) {
                field = WindowsUtil.getWindowHeight(this)
            }
            return field
        }
        private set

    private var instance: App? = null

    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
            AppConstant.setApp(instance!!)
            //监听所有activity生命周期
            if (activityLifecycleListener == null) {
                activityLifecycleListener = ActivityLifecycleListener()
                registerActivityLifecycleCallbacks(activityLifecycleListener)
            }
            //初始化sharedpreferences
            SharedPreferencesUtil.initSharedPreferences(instance!!)
            //初始化文件
            FileUtil.initLoad(instance!!)
            //全局捕获异常
            CrashHandler.init()
        }
    }

    //app是否退到了后台；true，还在前台；false，已经退到了后台
    val isAppTop: Boolean
        get() = if (activityLifecycleListener != null) activityLifecycleListener!!.isTop else false

}