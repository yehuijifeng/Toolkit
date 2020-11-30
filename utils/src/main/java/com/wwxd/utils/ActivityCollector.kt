package com.wwxd.utils

import android.app.Activity
import android.os.Process
import java.util.*

/**
 * Created by Yehuijifeng
 * on 2015/10/27.
 * activity的收藏夹
 */
object ActivityCollector {
    //保存整个app所有活动的activity
    private val activityMap = LinkedHashMap<String, Activity>()
    private fun getKey(activity: Activity): String {
        return getKey(activity.javaClass)
    }

    private fun getKey(clazz: Class<*>?): String {
        return clazz?.simpleName ?: ""
    }

    //获得最上层的activity
    val activityTop: Activity?
        get() {
            if (activityMap.size == 0) return null
            val collection: Collection<Activity> = activityMap.values
            val activities: Array<Any> = collection.toTypedArray()
            return activities[activities.size - 1] as Activity
        }

    //添加activity
    fun addActivity(activity: Activity?) {
        if (activity == null) return
        activityMap[getKey(
            activity
        )] = activity
    }

    //从集合中移除activity
    fun removeActivity(activity: Activity?) {
        if (activity != null) activityMap.remove(
            getKey(activity)
        )
    }

    //当前activity是否在栈顶
    fun checkActivityTop(activity: Activity?): Boolean {
        if (activity == null) return false
        val topActivity = activityTop ?: return false
        return activity === topActivity
    }

    //该操作用于清空当前app栈中所有activity，用于退出程序所用
    @Synchronized
    fun finishAll() {
        val activities = ArrayList(activityMap.values)
        for (activity in activities) {
            activity.finish()
        }
    }

    //该操作用于清空当前app栈中所有activity，保留目标activity
    fun finishAllByKeep(activity: Activity?) {
        if (activity == null) return
        val activities = ArrayList(activityMap.values)
        for (activity1 in activities) if (activity !== activity1) activity1.finish()
    }

    //该操作用于清空当前app栈中所有activity，保留目标activity
    fun finishAllByKeep(clazz: Class<*>?) {
        val activities = ArrayList(activityMap.values)
        val keepClassName = getKey(clazz)
        for (activity in activities) if (!keepClassName.equals(
                getKey(activity),
                ignoreCase = true
            )
        ) activity.finish()
    }

    //finish 目标activity头上的所有activity
    fun finishByTarget(activity: Activity?) {
        if (activity == null) return
        val activities = ArrayList(activityMap.values)
        for (i in activities.indices.reversed()) {
            val activity1 = activities[i]
            if (activity1 === activity) return else activity1.finish()
        }
    }

    //finish 目标activity头上的所有activity
    fun finishByTarget(clazz: Class<*>?) {
        if (clazz == null) return
        val activities = ArrayList(activityMap.values)
        val key = getKey(clazz)
        for (i in activities.indices.reversed()) {
            val activity1 = activities[i]
            val key1 = getKey(activity1)
            if (key1 == key) return else activity1.finish()
        }
    }

    //finish多个activity
    fun finishActivitys(vararg classs: Class<*>?) {
        for (clazz in classs) {
            if (clazz == null) continue
            val activity = activityMap[getKey(
                clazz
            )]
            activity?.finish()
        }
    }

    //当前栈中是否包含目标activity
    fun isContain(activity: Activity?): Boolean {
        return if (activity == null) false else activityMap.containsValue(activity)
    }

    //当前栈中是否包含目标activity
    fun isContain(clazz: Class<*>?): Boolean {
        return if (clazz == null) false else activityMap.containsKey(
            getKey(
                clazz
            )
        )
    }

    /**
     * 杀死该应用进程
     */
    fun killProcess() {
        finishAll()
        //杀死该应用进程
        Process.killProcess(Process.myPid())
        System.exit(1)
    }
}