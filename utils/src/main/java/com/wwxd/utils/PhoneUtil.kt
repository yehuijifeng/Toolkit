package com.wwxd.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.utils.AppUtil.appName
import com.wwxd.utils.AppUtil.packageName
import com.wwxd.utils.FileUtil.toUri
import com.wwxd.utils.ToastUtil.showFailureToast
import com.wwxd.utils.ToastUtil.showLongToast
import com.wwxd.utils.photo.PhotoActivity
import java.io.File

/**
 * Created by 浩 on 2016/12/19.
 * 手机工具
 */
object PhoneUtil {
    const val CODE_FOR_PHOTO = 2000
    const val CODE_FOR_CAMERA = CODE_FOR_PHOTO + 1
    const val CODE_FOR_CROP = CODE_FOR_CAMERA + 1
    const val CODE_FOR_VIDEO = CODE_FOR_CROP + 1
    const val CODE_FOR_FILE = CODE_FOR_VIDEO + 1
    const val CODE_FOR_ALBUMS = CODE_FOR_FILE + 1

    /**
     * 发送邮件
     *
     * @param context
     */
    fun sendHelpEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822" // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(AppConstant.serviceEmail)) // 接收人
        val title = "[" + appName() + "] " + context.getString(R.string.str_help_email_title)
        intent.putExtra(Intent.EXTRA_SUBJECT, title) // 主题;
        intent.putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.str_help_email_content)
        ) // 正文  我遇到的问题如下：
        context.startActivity(Intent.createChooser(intent, title))
    }

    /**
     * 发送邮件
     *
     * @param context
     * @param toEmail 接收人邮箱
     * @param title   主题
     * @param content 正文
     */
    fun sendEmail(context: Context, toEmail: String, title: String?, content: String?) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822" // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail)) // 接收人
        intent.putExtra(Intent.EXTRA_SUBJECT, title) // 主题
        intent.putExtra(Intent.EXTRA_TEXT, content) // 正文
        context.startActivity(Intent.createChooser(intent, title))
    }

    /**
     * 拨打电话
     *
     * @param phone 电话号码
     */
    fun toTel(context: Context, phone: String) {
        if (TextUtils.isEmpty(phone)) return
        try {
            if (isTelephonyEnabled(context)) {
                val intent = Intent(Intent.ACTION_DIAL)
                val uri = Uri.parse("tel:$phone")
                intent.data = uri
                //                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent)
            } else {
                showFailureToast(context.getString(R.string.str_tel_error))
            }
        } catch (e: Exception) {
            showFailureToast(context.getString(R.string.str_tel_error))
        }
    }

    //检查是否可以打电话
    private fun isTelephonyEnabled(context: Context): Boolean {
        val tm = context.getSystemService(Activity.TELEPHONY_SERVICE)
        return tm != null && (tm as TelephonyManager).simState == TelephonyManager.SIM_STATE_READY
    }

    /**
     * 去相册
     */
    fun toPhotos(activity: Activity, num: Int) {
        try {
            val intent = Intent(activity, PhotoActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(AppConstant.MAX_IMAGE_NUM, num)
            intent.putExtras(bundle)
            activity.startActivityForResult(intent, CODE_FOR_PHOTO)
        } catch (e: Exception) {
            showLongToast(R.string.str_open_photo_error)
        }
    }

    /**
     * 去相册
     */
    fun toPhotos(fragment: Fragment, num: Int) {
        try {
            val intent = Intent(fragment.context, PhotoActivity::class.java)
            val bundle = Bundle()
            bundle.putInt(AppConstant.MAX_IMAGE_NUM, num)
            intent.putExtras(bundle)
            fragment.startActivityForResult(intent, CODE_FOR_PHOTO)
        } catch (e: Exception) {
            showLongToast(R.string.str_open_photo_error)
        }
    }

    /**
     * 去相机
     *
     * @param imageFilePath 预设值图片存放地址
     */
    fun toCamera(activity: Activity, imageFilePath: String) {
        try {
            val mTmpFile = File(imageFilePath)
            val uri = toUri(mTmpFile)
            if (uri == null) {
                showLongToast(R.string.str_open_camera_error)
                return
            }
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            activity.startActivityForResult(intent, CODE_FOR_CAMERA)
        } catch (e: Exception) {
            showLongToast(R.string.str_open_camera_error)
        }
    }

    /**
     * 去相机
     *
     * @param imageFilePath 预设值图片存放地址
     */
    fun toCamera(fragment: Fragment, imageFilePath: String) {
        try {
            val mTmpFile = File(imageFilePath)
            val uri = toUri(mTmpFile)
            if (uri == null) {
                showLongToast(R.string.str_open_camera_error)
                return
            }
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            fragment.startActivityForResult(intent, CODE_FOR_CAMERA)
        } catch (e: Exception) {
            showLongToast(R.string.str_open_camera_error)
        }
    }

    /**
     * 去系统视频
     */
    fun toVideo(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.type = "video/*"
        activity.startActivityForResult(intent, CODE_FOR_VIDEO)
    }

    /**
     * 去本地sd卡找资源
     */
    fun toFile(activity: Activity) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.type = "file/*"
        activity.startActivityForResult(intent, CODE_FOR_FILE)
    }

    /**
     * 去本地sd卡找资源
     */
    fun toFile(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.type = "file/*"
        fragment.startActivityForResult(intent, CODE_FOR_FILE)
    }

    fun toCrop(activity: Activity?, uri: Uri?, saveImagePath: String) {
        toCrop(activity, null, uri, saveImagePath)
    }

    fun toCrop(fragment: Fragment?, uri: Uri?, saveImagePath: String) {
        toCrop(null, fragment, uri, saveImagePath)
    }

    /**
     * 剪裁图片
     *
     * @param uri           图片的uri
     * @param saveImagePath 剪裁后的图片保存位置
     */
    private fun toCrop(
        activity: Activity?,
        fragment: Fragment?,
        uri: Uri?,
        saveImagePath: String
    ) {
        if (uri == null) return
        val bitmap = FileUtil.uriToBitmap(uri)
        if (bitmap != null) {
            AppSession.setSession(AppConstant.CROP_IMAGE, bitmap)
            AppSession.setSession(AppConstant.CROP_IMAGE_SAVE_PATH, saveImagePath)
            if (fragment != null) {
                val intent = Intent(fragment.context, CropActivity::class.java)
                fragment.startActivityForResult(intent, CODE_FOR_CROP)
            } else if (activity != null) {
                val intent = Intent(activity, CropActivity::class.java)
                activity.startActivityForResult(intent, CODE_FOR_CROP)
            }
        }
    }

    //去手机应用商店评分
    fun getAppStore(context: Context) {
        try {
            val uri = Uri.parse("market://details?id=" + packageName())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val isMIUI: Boolean
        get() = "xiaomi".equals(Build.MANUFACTURER, ignoreCase = true)
    val isEMUI: Boolean
        get() = "HUAWEI".equals(Build.MANUFACTURER, ignoreCase = true)
    val isOPPO: Boolean
        get() = "OPPO".equals(Build.MANUFACTURER, ignoreCase = true)
    val isVIVO: Boolean
        get() = "vivo".equals(Build.MANUFACTURER, ignoreCase = true)
}