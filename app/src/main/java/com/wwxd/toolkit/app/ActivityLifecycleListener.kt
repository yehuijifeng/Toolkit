package com.wwxd.toolkit.app

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import com.wwxd.utils.ActivityCollector

/**
 * Created by LuHao on 2017/4/17.
 * activity生命周期监听器
 */
class ActivityLifecycleListener : ActivityLifecycleCallbacks {
    private var showCount //当前app所展示的页面
            = 0

    //app是否退到了后台；true，还在前台；false，已经退到了后台
    var isTop //当前app是否在前台展示
            = false
        private set

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityCollector.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        showCount++
        if (showCount > 0 && !isTop) {
            try {
                //app到了前台
                isTop = true
                //监测通道连通情况
                //清空通知栏
                val notificationManager =
                    activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {
        showCount--
        if (showCount == 0) {
            try {
                //app退到了后台
                isTop = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityCollector.removeActivity(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}