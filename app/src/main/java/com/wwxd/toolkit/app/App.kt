package com.wwxd.toolkit.app

import com.wwdx.toolkit.utils.FileUtil
import com.wwdx.toolkit.utils.SharedPreferencesUtil
import com.wwdx.toolkit.utils.WindowsUtil
import com.wwxd.toolkit.base.BaseApp
import com.wwxd.toolkit.file.AppFile

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
        //初始化文件
        if (FileUtil.checkSdCard()) {
            for (appFile in AppFile.values()) {
                appFile.getFilePath()
            }
        }
        //全局捕获异常
        CrashHandler.init()
    }

    var winWidth = 0
    var winHeight = 0
    override fun getWindowWidth(): Int {
        //屏幕的宽,高
        if (winWidth == 0)
            winWidth = WindowsUtil.getWindowWidth(this)
        return winWidth
    }

    override fun getWindowHeight(): Int {
        if (winHeight == 0)
            winHeight = WindowsUtil.getWindowHeight(this)
        return winHeight
    }

}