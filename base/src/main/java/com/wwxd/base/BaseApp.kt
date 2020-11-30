package com.wwxd.base

import android.app.Application

/**
 * user：LuHao
 * time：2019/10/27 11:46
 * describe：程序管理
 */
abstract class BaseApp : Application() {

    var instance: BaseApp? = null

    abstract fun isTop(): Boolean
    abstract fun init()
    abstract fun getNavigationBarHeight(): Int
    override fun onCreate() {
        super.onCreate()
        if (instance == null) {
            instance = this
            AppConstant.setApp(instance!!)
            init()
        }
    }
}