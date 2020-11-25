package com.stock.calculator.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.stock.calculator.constant.AppConstant

/**
 * Created by yehuijifeng
 * on 2015年10月27日
 * 键值对存储工具类
 */
object SharedPreferencesUtil {
    private var mSharedPreferences: SharedPreferences? = null
    private var editor: Editor? = null

    fun initSharedPreferences(context: Application) {
        mSharedPreferences = context.getSharedPreferences(AppConstant.sharedPreferences_key, Context.MODE_PRIVATE)
    }

    private val edit: Editor?
        private get() {
            if (editor == null) {
                editor = mSharedPreferences!!.edit()
            }
            return editor
        }

    fun clearKey(key: String?) {
        edit!!.remove(key)
        edit!!.apply()
    }

    fun saveString(key: String?, value: String?) {
        edit!!.putString(key, value)
        edit!!.apply()
    }

    fun getString(key: String?): String? {
        return mSharedPreferences!!.getString(key, "")
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return mSharedPreferences!!.getString(key, defaultValue)
    }

    fun saveInt(key: String?, value: Int) {
        edit!!.putInt(key, value)
        edit!!.apply()
    }

    fun getInt(key: String?): Int {
        return mSharedPreferences!!.getInt(key, 0)
    }

    fun getInt(key: String?, defaultValue: Int): Int {
        return mSharedPreferences!!.getInt(key, defaultValue)
    }

    fun saveFloat(key: String?, value: Float) {
        edit!!.putFloat(key, value)
        edit!!.apply()
    }

    fun getFloat(key: String?): Float {
        return mSharedPreferences!!.getFloat(key, 0.0f)
    }

    fun getFloat(key: String?, defaultValue: Float): Float {
        return mSharedPreferences!!.getFloat(key, defaultValue)
    }

    fun saveBoolean(key: String?, value: Boolean) {
        edit!!.putBoolean(key, value)
        edit!!.apply()
        mSharedPreferences!!.getBoolean(key, false)
    }

    fun getBoolean(key: String?): Boolean {
        return mSharedPreferences!!.getBoolean(key, false)
    }

    fun getBoolean(key: String?, defaultValue: Boolean): Boolean {
        return mSharedPreferences!!.getBoolean(key, defaultValue)
    }

    fun saveLong(key: String?, value: Long) {
        edit!!.putLong(key, value)
        edit!!.apply()
    }

    fun getLong(key: String?): Long {
        return mSharedPreferences!!.getLong(key, 0)
    }

    fun getLong(key: String?, defaultValue: Long): Long {
        return mSharedPreferences!!.getLong(key, defaultValue)
    }

    val all: Map<String, *>
        get() = mSharedPreferences!!.all
}