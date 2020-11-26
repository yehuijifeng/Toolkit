package com.wwdx.toolkit.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.NotificationManagerCompat
import com.wwxd.toolkit.BuildConfig
import com.wwxd.toolkit.R
import com.wwxd.toolkit.base.AppConstant
import kotlin.reflect.KClass

/**
 * user：LuHao
 * time：2019/10/28 14:44
 * describe：app工具类
 */
object AppUtil {
    //是否是androidQ以上版本
    fun isAndroidQ(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }

    //获取应用程序名称
    fun appName(): String {
        return AppConstant.getApp().resources.getString(R.string.app_name)
    }

    //手机品牌
    fun phoneBrand(): String {
        return Build.BRAND
    }

    //app展示版本号
    fun versionName(): String {
        return BuildConfig.VERSION_NAME
    }

    //app版本号
    fun versionCode(): Int {
        return BuildConfig.VERSION_CODE
    }

    //手机型号
    fun phoneModel(): String {
        return Build.MODEL
    }

    //手机android版本号
    fun phoneSDK(): String {
        return Build.VERSION.RELEASE//注意此处为ApplicationInfo，因为设置的meta-data是在application标签中}
    }

    //获得mataData标签
    fun mataData(): Bundle? {
        try {
            val packageManager = AppConstant.getApp().packageManager
            if (packageManager != null) {
                //注意此处为ApplicationInfo，因为设置的meta-data是在application标签中
                val applicationInfo = packageManager.getApplicationInfo(
                    AppConstant.getApp().packageName,
                    PackageManager.GET_META_DATA
                )
                return applicationInfo.metaData
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private var packageInfo: PackageInfo? = null

    //获得mataData标签
    private fun packageInfo(): PackageInfo? {
        if (packageInfo == null || TextUtils.isEmpty(packageInfo!!.packageName)) {
            val packageManager = AppConstant.getApp().packageManager
            if (packageManager != null) {
                try {
                    packageInfo = packageManager.getPackageInfo(
                        AppConstant.getApp().packageName,
                        0
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (packageInfo == null) packageInfo = PackageInfo()
        return packageInfo
    }

    //获取渠道 获取application中指定的meta-data
    fun hannelName(): String? {
        val bundle = mataData()
        return if (bundle != null) bundle.getString("channel_name") else "other"
    }

    //app包名
    fun packageName(): String {
        return packageInfo!!.packageName
    }

    //获得设备id
    fun phoneId(): String {
        return Settings.System.getString(
            AppConstant.getApp().contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    /**
     * 重启app
     */
    fun reStartApp(activity: KClass<*>) {
        try {
            ActivityCollector.finishAll()
            ToastUtil.cancelToast()
            val intent = Intent(AppConstant.getApp(), activity.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val restartIntent = PendingIntent.getActivity(AppConstant.getApp(), 0, intent, 0)
            //退出程序
            val mgr = AppConstant.getApp().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent) // 1秒钟后重启应用
        } catch (e: Exception) {
            Process.killProcess(Process.myPid())
        }
    }

    //退出app
    fun exitApp() {
        ToastUtil.cancelToast()
        ActivityCollector.finishAll()
        Process.killProcess(Process.myPid())
    }

    //手机号加密显示
    fun showPhonePwd(phone: String): String {
        if (!TextUtils.isEmpty(phone) && phone.length > 6) {
            phone.substring(0, 3) + "****" + phone.substring(7)
        } else {
            phone.replace(phone.substring(1, phone.length - 1), "****")
        }
        return phone
    }

    //打开网络设置界面
    fun openSettingNetWork(context: Context) {
        // 跳转到系统的网络设置界面
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }

    //打开通知设置界面
    fun openSettingNotice(context: Context) {
        // 跳转到系统的网络设置界面
        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName())
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, AppConstant.getApp().applicationInfo.uid)
        } else {
            intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName(), null)
            intent.data = uri
        }
        context.startActivity(intent)
    }

    //是否启用了消息通知;1,没有开启；0，开启
    fun isNotificationsEnabled(): Int {
        val notification = NotificationManagerCompat.from(AppConstant.getApp())
        return if (notification.areNotificationsEnabled()) 0 else 1
    }

    /**
     * 去应用市场
     */
    fun openAppStore(context: Context) {
        try {
            val uri = Uri.parse("market://details?id=" + packageName())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}