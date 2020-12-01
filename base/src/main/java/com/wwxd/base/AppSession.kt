package com.wwxd.base

import kotlin.reflect.KClass

/**
 * user：LuHao
 * time：2020/12/1 17:25
 * describe：传递session使用
 */
object AppSession {
    private val sessionMap = HashMap<String, Any>()

    fun containsSession(key: String): Boolean {
        return sessionMap.containsKey(key)
    }

    fun <T> getSession(key: String): T? {
        if (containsSession(key))
            return sessionMap[key] as T
        else
            return null
    }

    fun setSession(key: String, value: Any) {
        sessionMap[key] = value
    }

    fun clearSession(key: String) {
        sessionMap.remove(key)
    }

}