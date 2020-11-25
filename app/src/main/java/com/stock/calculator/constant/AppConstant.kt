package com.stock.calculator.constant

import com.stock.calculator.app.App

/**
 * user：LuHao
 * time：2020/11/25 14:49
 * describe：常量
 */
object AppConstant {
    var application: App? = null
    fun setApp(app: App) {
        application = app
    }

    fun getApp(): App {
        return application!!
    }

    const val fileProvider = "com.stock.calculator.fileProvider" //存储文件位置配置信息
    const val sharedPreferences_key = "sharedpreferences_key";//sharedpreferences的key
}