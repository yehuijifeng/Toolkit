package com.wwxd.toolkit.base

import android.app.ProgressDialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by yehuijifeng
 * on 2016/1/7.
 * 加载loading
 */
class LoadingView(context: Context) : ProgressDialog(context, R.style.default_dialog_style) {
    private var root: View? = null
    private var imgIcon: ImageView? = null
    private var textContent: TextView? = null
    private var animation: Animation? = null

    /**
     * 关闭dialog
     */
    private fun dismissLoadingDialog() {
        if (imgIcon != null) imgIcon!!.clearAnimation()
        dismiss()
    }

    private fun initView(context: Context) {
        root = View.inflate(context, R.layout.dialog_loading, null)
        textContent = root!!.findViewById(R.id.textContent)
        imgIcon = root!!.findViewById(R.id.imgIcon)
        animation = RotateAnimation(
            0F,
            360F,
            Animation.RELATIVE_TO_SELF,
            0.5F,
            Animation.RELATIVE_TO_SELF,
            0.5F
        ) //子心旋转
        //线性插值器，根据时间百分比设置属性百分比
        val linearInterpolator = LinearInterpolator()
        //setInterpolator表示设置旋转速率。
        animation!!.setInterpolator(linearInterpolator)
        animation!!.setRepeatCount(-1) //-1表示循环运行
        animation!!.setDuration(2000)
        setCanceledOnTouchOutside(false)
    }

    private var builder: Builder? = null
    fun getBuilder(): Builder? {
        if (builder == null) builder = Builder()
        builder!!.init()
        return builder
    }

    inner class Builder {
        private var isCancelable = false
        private var content: String? = null
        private var res = 0

        //初始化
        fun init() {
            isCancelable = false
            content = context.getString(R.string.str_please_wait)
            res = R.drawable.ic_loading_view
        }

        //是否可以返回取消
        fun isCancelable(bl: Boolean): Builder {
            isCancelable = bl
            return this
        }

        //loading文字
        fun content(content: String?): Builder {
            if (!TextUtils.isEmpty(content)) this.content = content
            return this
        }

        //loading图片
        fun icon(res: Int): Builder {
            this.res = res
            return this
        }

        fun show() {
            showLoadingDialog(isCancelable, content, res)
        }

        fun close() {
            dismissLoadingDialog()
        }
    }

    /**
     * 显示loading
     *
     * @param isCancelable 是否可以返回取消
     * @param loadingStr   loading文字
     * @param drawable     loading图片
     */
    private fun showLoadingDialog(isCancelable: Boolean, loadingStr: String?, drawable: Int) {
        if (isShowing) return
        if (!TextUtils.isEmpty(loadingStr)) textContent!!.text = loadingStr
        if (drawable > 0) imgIcon!!.setImageResource(drawable)
        setCancelable(isCancelable)
        show()
        setContentView(root!!)
        imgIcon!!.startAnimation(animation)
    }

    init {
        initView(context)
    }
}