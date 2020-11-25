package com.stock.calculator.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * user：LuHao
 * time：2019/10/28 16:24
 * describe：日期工具
 */
object DateUtil {
    var TIME = "HH:mm"
    var FORMAT_SHORT = "MM-dd"
    var FORMAT_SHORT_TIME = "MM-dd HH:mm"
    var FORMAT_SHORT_TIME_TWO = "yyyy-MM-dd HH:mm"
    var FORMAT = "yyyy-MM-dd"
    var FORMAT_LONG = "yyyy-MM-dd HH:mm:ss"
    var FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.SSS" //毫秒
    var FORMAT_FULL_Greenwich = "EEE MMM dd hh:mm:ss z yyyy" //格林尼治时间

    //服务器时间戳
    fun getServerTime(): Long {
        return System.currentTimeMillis()
    }

    fun getServerTime(FORMAT: String?): Long {
        return getTimeStamp(getServerTimeStr(FORMAT), FORMAT, getServerTimeZone())
    }

    //服务器时间
    fun getServerTimeStr(FORMAT: String?): String {
        return format(getServerTime(), FORMAT, getServerTimeZone())
    }

    //服务器的时区
    fun getServerTimeZone(): String {
        return TimeZone.getDefault().displayName// 时区，东时区数字为正，西时区为负
    }

    //获得时区代码
    val zoneOffset: Int
        get() {
            val zoneOffset = Calendar.getInstance()[Calendar.ZONE_OFFSET]
            return zoneOffset / 3600 / 1000 // 时区，东时区数字为正，西时区为负
        }

    /**
     * 时间戳转成格式化时间
     *
     * @param timestamp    时间戳
     * @param format       格式 例如：yyy-MM-dd hh:mm:ss
     * @param timeZoneCode 时区 -12 ~ 12 之间
     * @return 具体格式的时间
     */
    fun format(timestamp: Long, format: String?, timeZoneCode: String?): String {
        val timeZone = TimeZone.getTimeZone(timeZoneCode)
        val df = SimpleDateFormat(format, Locale.getDefault())
        df.timeZone = timeZone
        val date = Date(timestamp)
        return df.format(date)
    }

    /**
     * 格式化时间转成时间戳
     *
     * @param timeStr      格式化时间 例如：2019-11-24 17:37:31
     * @param format       格式 例如：yyy-MM-dd hh:mm:ss
     * @param timeZoneCode 时区 -12 ~ 12 之间
     * @return 时间戳
     */
    fun getTimeStamp(timeStr: String?, format: String?, timeZoneCode: String?): Long {
        return try {
            val timeZone = TimeZone.getTimeZone(timeZoneCode)
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.timeZone = timeZone
            val date = df.parse(timeStr) ?: return 0
            date.time
        } catch (e: Exception) {
            0
        }
    }

    //是否是今天
    fun isToday(time: Long): Boolean {
        val timeStr = format(time, FORMAT, getServerTimeZone())
        val todayStr = format(getServerTime(), FORMAT, getServerTimeZone())
        return timeStr == todayStr
    }

    fun isToday(timeStr: String): Boolean {
        val time = getTimeStamp(timeStr, FORMAT, getServerTimeZone())
        val timeStr1 = format(time, FORMAT, getServerTimeZone())
        val todayStr = format(getServerTime(), FORMAT, getServerTimeZone())
        return timeStr1 == todayStr
    }

    //获取当前日期是星期几;0周日；1周一，以此类推
    fun getWeek(timestamp: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timestamp)
        return calendar[Calendar.DAY_OF_WEEK] - 1
    }

    fun getYear(time: Long): Int {
        val df = SimpleDateFormat("yyyy", Locale.getDefault())
        val returnValue = df.format(Date(time))
        return returnValue.toInt()
    }

    fun getMonth(time: Long): Int {
        val df = SimpleDateFormat("MM", Locale.getDefault())
        val returnValue = df.format(Date(time))
        return returnValue.toInt()
    }

    fun getDay(time: Long): Int {
        val df = SimpleDateFormat("dd", Locale.getDefault())
        val returnValue = df.format(Date(time))
        return returnValue.toInt()
    }
}