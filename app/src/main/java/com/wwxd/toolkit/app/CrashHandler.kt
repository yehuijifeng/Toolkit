package com.wwxd.toolkit.app

import android.os.Environment
import com.wwxd.utils.AppUtil
import com.wwxd.utils.DateUtil
import com.wwxd.utils.LogUtil
import com.wwxd.toolkit.activity.LoadingActivity
import com.wwxd.toolkit.file.AppFile
import java.io.*

/**
 * Created by yehuijifeng
 * on 2015/11/26.
 * 全局捕获异常类
 */
object CrashHandler : Thread.UncaughtExceptionHandler {
    //系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    fun init() {
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当有异常发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread?, throwable: Throwable?) {
        if (saveCrashContentFile(throwable)) {
            AppUtil.reStartApp(LoadingActivity::class)
        } else {
            if (throwable != null)
                LogUtil.e("qyqx_error : " + throwable.message)
            mDefaultHandler!!.uncaughtException(thread, throwable)
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param throwable 错误信息
     * @return 返回是否记录成功， 便于将文件传送到服务器
     */
    private fun saveCrashContentFile(throwable: Throwable?): Boolean {
        try {
            if (throwable == null) return false
            val sb = StringBuilder()
            sb.append("PackageName").append("====>").append(AppUtil.packageName()).append("\n")
            sb.append("AppName").append("====>").append(AppUtil.appName()).append("\n")
            sb.append("HannelName").append("====>").append(AppUtil.hannelName()).append("\n")
            sb.append("PhoneBrand").append("====>").append(AppUtil.phoneBrand()).append(" ")
                .append(AppUtil.phoneModel()).append("\n")
            sb.append("PhoneSDK").append("====>").append(AppUtil.phoneSDK()).append("\n")
            sb.append("VersionName").append("====>").append(AppUtil.versionName()).append("\n")
            sb.append("VersionCode").append("====>").append(AppUtil.versionCode()).append("\n")
            val writer: Writer = StringWriter()
            val printWriter = PrintWriter(writer)
            throwable.printStackTrace(printWriter)
            var cause = throwable.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val result = writer.toString()
            LogUtil.e("qyqx_error : $result")
            sb.append(result)
            val time: String = DateUtil.format(
                DateUtil.getServerTime(),
                DateUtil.FORMAT,
                DateUtil.getServerTimeZone()
            )
            val fileName: StringBuilder = StringBuilder("crash_").append(time).append("_date_")
                .append(DateUtil.getServerTime())
                .append("_version_")
                .append(AppUtil.versionCode())
                .append(".txt")
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                //这里换成自定义路径
                val path: String = AppFile.LOG_ERROR.ObtainAppFilePath()
                val dir = File(path)
                if (!dir.exists()) if (!dir.mkdirs()) return false
                val fos = FileOutputStream(path + fileName)
                fos.write(sb.toString().toByteArray())
                fos.close()
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

}