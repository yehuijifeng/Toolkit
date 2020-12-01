package com.wwxd.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.wwxd.base.AppConstant.getApp

/**
 * user：LuHao
 * time：2019/11/10 16:57
 * describe：权限管理器
 */
object PermissionsUtil {
//    //请求权限回调
//    fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>?,
//        grantResults: IntArray?
//    ) {
//    }

    private const val MANUFACTURER_HUAWEI = "Huawei" //华为
    private const val MANUFACTURER_VIVO = "vivo"
    private const val MANUFACTURER_OPPO = "OPPO"
    private const val MANUFACTURER_XIAOMI = "Xiaomi" //小米
    private const val MANUFACTURER_SAMSUNG = "samsung" //三星
    private const val MANUFACTURER_MEIZU = "Meizu" //魅族
    const val permissionSettingForResult = 8888

    //检查sd卡权限
    fun getSdCardPermissions(): Array<String> {
        return if (AppUtil.isAndroidQ()) {
            arrayOf(
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    //检查定位权限
    fun getAddress(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,  //精准定位
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    // 判断是否缺少权限。
    // true，缺少
    // false，有权限
    fun lacksPermission(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                getApp(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            return true
        }
        return false
    }

    // 判断是否缺少权限。true，缺少；false，已有权限
    fun lacksPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    getApp(),
                    permission
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return true
            }
        }
        return false
    }


    //去请求权限
    fun requestPermissions(activity: Activity, permission: String, callBack: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), callBack)
    }

    fun requestPermissions(fragment: Fragment, permission: String, callBack: Int) {
        fragment.requestPermissions(arrayOf(permission), callBack)
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, callBack: Int) {
        ActivityCompat.requestPermissions(activity, permissions, callBack)
    }

    fun requestPermissions(fragment: Fragment, permissions: Array<String>, callBack: Int) {
        fragment.requestPermissions(permissions, callBack)
    }

    /**
     * 跳转到相应品牌手机系统权限设置页，如果跳转不成功，则跳转到应用详情页
     * 这里需要改造成返回true或者false，应用详情页:true，应用权限页:false
     */
    fun goToSetting(activity: Activity) {
        when (Build.MANUFACTURER) {
            MANUFACTURER_HUAWEI -> Huawei(activity)
            MANUFACTURER_MEIZU -> Meizu(activity)
            MANUFACTURER_XIAOMI -> Xiaomi(activity)
            MANUFACTURER_OPPO -> OPPO(activity)
            else -> openAppDetailSetting(activity)
        }
    }

    /**
     * 华为跳转权限设置页
     */
    private fun Huawei(activity: Activity) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.packageName)
            val comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            )
            intent.component = comp
            activity.startActivityForResult(intent, permissionSettingForResult)
        } catch (e: Exception) {
            openAppDetailSetting(activity)
        }
    }

    /**
     * 魅族跳转权限设置页，测试时，点击无反应，具体原因不明
     */
    private fun Meizu(activity: Activity) {
        try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", activity.packageName)
            activity.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailSetting(activity)
        }
    }

    /**
     * 小米，功能正常
     */
    private fun Xiaomi(activity: Activity) {
        try { // MIUI 8 9
            val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
            localIntent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            localIntent.putExtra("extra_pkgname", activity.packageName)
            activity.startActivityForResult(localIntent, permissionSettingForResult)
        } catch (e: Exception) {
            try { // MIUI 5/6/7
                val localIntent = Intent("miui.intent.action.APP_PERM_EDITOR")
                localIntent.setClassName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
                )
                localIntent.putExtra("extra_pkgname", activity.packageName)
                activity.startActivityForResult(localIntent, permissionSettingForResult)
            } catch (e1: Exception) { // 否则跳转到应用详情
                openAppDetailSetting(activity)
            }
        }
    }

    /**
     * OPPO
     *
     * @param activity
     */
    private fun OPPO(activity: Activity) {
        try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("packageName", activity.packageName)
            val comp = ComponentName(
                "com.color.safecenter",
                "com.color.safecenter.permission.PermissionManagerActivity"
            )
            intent.component = comp
            activity.startActivity(intent)
        } catch (e: Exception) {
            openAppDetailSetting(activity)
        }
    }

    //app的默认跳转页
    private fun openAppDetailSetting(activity: Activity) {
        try {
            //防止应用详情页也找不到，捕获异常后跳转到设置，这里跳转最好是两级，太多用户也会觉得麻烦，还不如不跳
            val localIntent = Intent()
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", activity.packageName, null)
            activity.startActivityForResult(localIntent, permissionSettingForResult)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_SETTINGS)
            activity.startActivity(intent)
        }
    }

}