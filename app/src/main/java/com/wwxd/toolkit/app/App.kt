package com.wwxd.toolkit.app

import com.wwxd.base.BaseApp
import com.wwxd.utils.SharedPreferencesUtil
import com.wwxd.utils.WindowsUtil

/**
 * user：LuHao
 * time：2019/10/27 11:46
 * describe：程序管理
 */
class App : BaseApp() {
    //监听每一个activity的生命周期变化
    private var activityLifecycleListener: ActivityLifecycleListener? = null

    override fun isTop(): Boolean {
        if (activityLifecycleListener != null)
            return activityLifecycleListener!!.isTop
        else
            return false
    }

    override fun init() {
        //监听所有activity生命周期
        if (activityLifecycleListener == null) {
            activityLifecycleListener = ActivityLifecycleListener()
            registerActivityLifecycleCallbacks(activityLifecycleListener)
        }
        //初始化sharedpreferences
        SharedPreferencesUtil.initSharedPreferences(instance!!)
        //全局捕获异常
        CrashHandler.init()
    }

    var navigationBarH = 0

    override fun getNavigationBarHeight(): Int {
        if (navigationBarH == 0)
            navigationBarH = WindowsUtil.getNavigationBarHeight()
        return navigationBarH
    }

}