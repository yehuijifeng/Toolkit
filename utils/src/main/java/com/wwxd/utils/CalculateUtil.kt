package com.wwxd.utils

import java.math.BigDecimal

/**
 * 精准计算
 */
object CalculateUtil {
    /**
     * 加法
     */
    fun add(var1: Double, var2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(var1))
        val b2 = BigDecimal(java.lang.Double.toString(var2))
        return b1.add(b2).toDouble()
    }

    /**
     * 减法
     */
    fun sub(var1: Double, var2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(var1))
        val b2 = BigDecimal(java.lang.Double.toString(var2))
        return b1.subtract(b2).toDouble()
    }

    /**
     * 乘法
     */
    fun mul(var1: Double, var2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(var1))
        val b2 = BigDecimal(java.lang.Double.toString(var2))
        return b1.multiply(b2).toDouble()
    }

    /**
     * 除法
     *
     * @param scale 精度，到小数点后几位
     */
    fun div(v1: Double, v2: Double, scale: Int): Double {
        require(scale >= 0) { "The scale must be a positive integer or " }
        val b1 = BigDecimal(java.lang.Double.toString(v1))
        val b2 = BigDecimal(java.lang.Double.toString(v2))
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 四舍五入
     *
     * @param scale 精确位数
     * @return
     */
    fun round(v: Double, scale: Int): Double {
        if (scale < 0) return 0.0
        val b = BigDecimal(java.lang.Double.toString(v))
        val one = BigDecimal("1")
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toDouble()
    }

    /**
     * 保留几位小数，其他省略
     *
     * @param retainLength 保留几位数
     */
    @JvmStatic
    fun retain(a: Double, retainLength: Int): Double {
        val bigDecimal = BigDecimal(a)
        return bigDecimal.setScale(retainLength, BigDecimal.ROUND_DOWN).toDouble()
    }
}