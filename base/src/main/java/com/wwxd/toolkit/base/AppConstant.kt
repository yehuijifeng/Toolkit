package com.wwxd.toolkit.base


/**
 * user：LuHao
 * time：2020/11/25 14:49
 * describe：常量
 */
object AppConstant {
    var application: BaseApp? = null
    fun setApp(baseApp: BaseApp) {
        application = baseApp
    }

    fun getApp(): BaseApp {
        return application!!
    }

    const val fileProvider = "com.wwxd.toolkit.fileProvider" //存储文件位置配置信息
    const val sharedPreferences_key = "sharedpreferences_key";//sharedpreferences的key
}