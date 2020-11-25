package com.stock.calculator.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.stock.calculator.R
import com.stock.calculator.app.App
import com.stock.calculator.constant.AppConstant

/**
 * Created by LuHao on 2017/3/13.
 * 吐司提示
 */
object ToastUtil {

    // 实现单例
    private var toastDefault: Toast? = null
    private var toastStatus: Toast? = null
    private var toast_default_layout: View? = null
    private var textDefaultContent: TextView? = null
    private var toast_status_layout: View? = null
    private var imgStatus: ImageView? = null
    private var textStatusContent: TextView? = null

    //系统默认短时间土司
    @Synchronized
    fun showShortToast(str: String?) {
        try {
            if (toastDefault != null) {
                toastDefault!!.cancel()
            }
                toastDefault = Toast(AppConstant.getApp())
                toastDefault!!.duration = Toast.LENGTH_SHORT
                toastDefault!!.setGravity(Gravity.CENTER, 0, 0)
                toastDefault!!.view = toastDefaultLayout
                detaultText!!.text = str
                toastDefault!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //系统默认长时间土司
    @Synchronized
    fun showLongToast(str: String?) {
        try {
            if (toastDefault != null) {
                toastDefault!!.cancel()
            }
                toastDefault = Toast(AppConstant.getApp())
                toastDefault!!.duration = Toast.LENGTH_LONG
                toastDefault!!.setGravity(Gravity.CENTER, 0, 0)
                toastDefault!!.view = toastDefaultLayout
                detaultText!!.text = str
                toastDefault!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //成功土司
    @Synchronized
    fun showSuccessToast(str: String?) {
        try {
            if (toastStatus == null) {
                    toastStatus = Toast(AppConstant.getApp())
                    toastStatus!!.duration = Toast.LENGTH_LONG
                    toastStatus!!.setGravity(Gravity.CENTER, 0, 0)
                    toastStatus!!.view = toastStatusLayout
                    statusText!!.text = str
                    statusImage!!.setImageResource(R.drawable.ic_toast_success)
                    toastStatus!!.show()
            } else {
                toastStatus!!.cancel()
                toastStatus = Toast(AppConstant.getApp())
                toastStatus!!.duration = Toast.LENGTH_SHORT
                toastStatus!!.setGravity(Gravity.CENTER, 0, 0)
                toastStatus!!.view = toastStatusLayout
                statusText!!.text = str
                statusImage!!.setImageResource(R.drawable.ic_toast_success)
                toastStatus!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //失败土司
    @Synchronized
    fun showFailureToast(str: String?) {
        try {
            if (toastStatus == null) {
                        toastStatus = Toast(AppConstant.getApp())
                        toastStatus!!.duration = Toast.LENGTH_LONG
                        toastStatus!!.setGravity(Gravity.CENTER, 0, 0)
                        toastStatus!!.view = toastStatusLayout
                        statusText!!.text = str
                        statusImage!!.setImageResource(R.drawable.ic_toast_failure)
                        toastStatus!!.show()
            } else {
                toastStatus!!.cancel()
                toastStatus = Toast(AppConstant.getApp())
                toastStatus!!.duration = Toast.LENGTH_SHORT
                toastStatus!!.setGravity(Gravity.CENTER, 0, 0)
                toastStatus!!.view = toastStatusLayout
                statusText!!.text = str
                statusImage!!.setImageResource(R.drawable.ic_toast_failure)
                toastStatus!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //创建默认toast的layout
    @get:Synchronized
    private val toastDefaultLayout: View?
        private get() {
            if (toast_default_layout == null) {
                toast_default_layout =
                    LayoutInflater.from(AppConstant.getApp()).inflate(R.layout.toast_default, null)
                textDefaultContent = toast_default_layout!!.findViewById(R.id.textContent)
            }
            return toast_default_layout
        }

    //创建默认toast的文字view
    @get:Synchronized
    private val detaultText: TextView?
        private get() {
            if (textDefaultContent == null) {
                textDefaultContent = toastDefaultLayout!!.findViewById(R.id.textContent)
            }
            return textDefaultContent
        }

    //创建成功/失败的toast的layout
    @get:Synchronized
    private val toastStatusLayout: View?
        private get() {
            if (toast_status_layout == null) {
                toast_status_layout =
                    LayoutInflater.from(AppConstant.getApp()).inflate(R.layout.toast_status, null)
                textStatusContent = toast_status_layout!!.findViewById(R.id.textContent)
                imgStatus = toast_status_layout!!.findViewById(R.id.imgStatus)
            }
            return toast_status_layout
        }

    //创建默认toast的文字view
    @get:Synchronized
    private val statusText: TextView?
        private get() {
            if (textStatusContent == null) {
                textStatusContent = toastStatusLayout!!.findViewById(R.id.textContent)
            }
            return textStatusContent
        }

    //创建默认toast的文字view
    @get:Synchronized
    private val statusImage: ImageView?
        private get() {
            if (imgStatus == null) {
                imgStatus = toastStatusLayout!!.findViewById(R.id.imgStatus)
            }
            return imgStatus
        }

    //清理土司，在退出app的时候才清理
    fun cancelToast() {
        if (toastDefault != null) toastDefault!!.cancel()
        if (toastStatus != null) toastStatus!!.cancel()
        textDefaultContent = null
        toast_default_layout = null
        toastDefault = null
        toastStatus = null
    }
}