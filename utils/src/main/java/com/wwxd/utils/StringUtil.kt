package com.wwxd.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import com.wwxd.base.AppConstant
import java.text.DecimalFormat
import java.util.regex.Pattern

/**
 * user：LuHao
 * time：2019/8/19 13:34
 * describe：字符串工具类
 */
object StringUtil {
    /**
     * 加载字体库
     *
     * @param typefaceName 字体库资源
     */
    fun getTypeface(context: Context, typefaceName: String): Typeface {
        return Typeface.createFromAsset(context.assets, typefaceName)
    }

    /**
     * 获取资源文件中的字符串
     *
     * @param id 资源id
     */
    fun getString(id: Int): String {
        return try {
            AppConstant.getApp().getResources().getString(id)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 获取资源文件中的字符串数组
     *
     * @param id
     * @return
     */
    fun getStrings(id: Int): Array<String> {
        return try {
            AppConstant.getApp().getResources().getStringArray(id)
        } catch (e: Exception) {
            arrayOf()
        }
    }

    // 判断一个字符是否是中文
    private fun isChinese(c: Char): Boolean {
        return c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5 // 根据字节码判断
    }

    /**
     * 复制文本
     */
    fun copy(content: String) {
        // 得到剪贴板管理器
        try {
            val cmb = AppConstant.getApp()
                .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cmb.setPrimaryClip(ClipData.newPlainText("", content.trim { it <= ' ' }))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 粘贴文本
     */
    fun paste(): String {
        // 得到剪贴板管理器
        val cmb =
            AppConstant.getApp().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return if (cmb.primaryClip != null) {
            cmb.primaryClip.toString()
        } else ""
    }

    // 判断一个字符串是否含有中文
    fun isChinese(str: String?): Boolean {
        if (str == null) return false
        for (c in str.toCharArray()) {
            if (isChinese(c)) return true // 有一个中文字符就返回
        }
        return false
    }

    //检查两个字符串的相似度
    fun checkSimilarity(str1: String, str2: String): Boolean {
        val d: Array<IntArray> //矩阵
        val n = str1.length
        val m = str2.length
        var i: Int //遍历str1的
        var j: Int //遍历str2的
        var ch1: Char //str1的
        var ch2: Char //str2的
        var temp: Int //记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) {
            return true
        }
        if (m == 0) {
            return true
        }
        d = Array(n + 1) { IntArray(m + 1) }
        i = 0
        while (i <= n) {
            //初始化第一列
            d[i][0] = i
            i++
        }
        j = 0
        while (j <= m) {
            //初始化第一行
            d[0][j] = j
            j++
        }
        i = 1
        while (i <= n) {
            //遍历str1
            ch1 = str1[i - 1]
            //去匹配str2
            j = 1
            while (j <= m) {
                ch2 = str2[j - 1]
                temp = if (ch1 == ch2) {
                    0
                } else {
                    1
                }
                //左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp)
                j++
            }
            i++
        }
        val ld = d[n][m]
        val aaa = 1 - ld.toDouble() / Math.max(n, m)
        return aaa < 0.7
    }

    //比较字符串的相似度，得到最小值
    private fun min(one: Int, two: Int, three: Int): Int {
        var min = one
        if (two < min) {
            min = two
        }
        if (three < min) {
            min = three
        }
        return min
    }

    //去掉字符串中的空格，换行、
    // \t：是补全当前字符串长度到8的整数倍，最少1个最多8个空格
    // \r：光标重新回到本行开头
    // \\s: 空格
    // \n：换行
    fun replaceBlank(str: String): String {
        return if (TextUtils.isEmpty(str)) "" else str.replace("\r|\n|\\s|\t".toRegex(), " ")
    }

    //替换字符串中特定字符
    fun replaceRegex(str: String, regex: String): String {
        if (TextUtils.isEmpty(str)) return ""
        val str1 = str.replace(regex.toRegex(), "")
        return str1
    }

    //多个空格替换成一个空格
    fun checkSpace(content: String?): String {
        if (TextUtils.isEmpty(content)) return ""
        val p = Pattern.compile(" +")
        val m = p.matcher(content)
        val content1 = m.replaceAll(" ")
        return content1
    }

    //多个换行替换成一个换行
    fun checkEnter(content: String?): String {
        if (TextUtils.isEmpty(content)) return ""
        val p = Pattern.compile("\\n+")
        val m = p.matcher(content)
        val content1 = m.replaceAll("\n")
        return content1
    }

    //获得缩略数字表示
    fun getNumStr(amusingCount: Long): String {
        val df = DecimalFormat("0.#")
        return if (amusingCount >= 100000000) {
            df.format(amusingCount / 100000000) + getString(R.string.str_num_yi)
        } else if (amusingCount >= 10000) {
            df.format(amusingCount / 10000) + getString(R.string.str_num_wan)
        } else if (amusingCount >= 1000) {
            df.format(amusingCount / 1000) + getString(R.string.str_num_qian)
        } else {
            amusingCount.toString() + ""
        }
    }
}