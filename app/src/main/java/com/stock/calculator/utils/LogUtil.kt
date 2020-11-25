package com.stock.calculator.utils

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * user：LuHao
 * time：2019/11/25 10:01
 * describe：日志打印统一管理
 */
object LogUtil {
    private val logContent = StringBuilder()
    fun setTag(tag: String) {
        TAG = tag
    }

    private var TAG = "appjson"
    private val model: Boolean = true
    fun saveLog(filePath: String) {
        try {
            val fileName = StringBuilder("test_log_").append("time_").append(time).append(".txt")
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                //这里换成自定义路径
                val dir = File(filePath)
                if (!dir.exists()) if (!dir.mkdirs()) return
                val fos = FileOutputStream(filePath + fileName)
                fos.write(logContent.toString().toByteArray())
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun d(msg: String?) {
        if (model) Log.d(TAG, msg)
        logContent.append(timeLog).append(msg)
    }

    fun e(msg: String?) {
        if (model) Log.e(TAG, msg)
        logContent.append(timeLog).append(msg)
    }

    fun v(msg: String?) {
        if (model) Log.v(TAG, msg)
        logContent.append(timeLog).append(msg)
    }

    fun i(msg: String) {
        var msg = msg
        if (model) {
            if (TextUtils.isEmpty(msg)) return
            val max_str_length = 1001 - TAG.length
            //大于4000时
            while (msg.length > max_str_length) {
                Log.i(TAG, msg.substring(0, max_str_length))
                msg = msg.substring(max_str_length)
            }
            //剩余部分
            Log.i(TAG, msg)
        }
        logContent.append(timeLog).append(msg)
    }

    fun w(msg: String?) {
        if (model) {
            Log.w(TAG, msg)
        }
        logContent.append(timeLog).append(msg)
    }

    private val time: String
        private get() {
            val timeZone = TimeZone.getTimeZone("GMT+8")
            val df = SimpleDateFormat("yyyy-MM-dd+HH:mm:ss:S", Locale.getDefault())
            df.timeZone = timeZone
            val date = Date(System.currentTimeMillis())
            return df.format(date)
        }
    private val timeLog: String
        private get() = """

            ${time}：
            """.trimIndent()
}