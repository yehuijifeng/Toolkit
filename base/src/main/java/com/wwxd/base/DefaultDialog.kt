package com.wwxd.base

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.wwxd.toolkit.base.R

/**
 * user：LuHao
 * time：2019/10/28 10:00
 * describe：默认的提示框
 */
class DefaultDialog(context: Context) : AlertDialog(context) {
    private var textTitle: TextView
    private var textContent: TextView
    private var btnCancel: TextView
    private var btnOk: TextView
    private var lyBtnTwo: LinearLayout
    private var btnOkTwo: TextView
    private var root: View
    private var isBackDismiss = false
    private var isClickDismiss = true //是否按返回键关闭；true,关闭；false，不关闭；
    private var builder: Builder

    @Synchronized
    fun getBuilder(): Builder {
        defaultSetting()
        return builder
    }


    //每次调用builder的时候需要初始化
    private fun defaultSetting() {
        builder
            .isBackDismiss(true)
            .isNoCancle(false)
            .isShowTiltle(true)
            .setTitle("")
            .setContent("")
            .setOkText("")
            .setCancelText("")
            .setCancelClick(null)
            .setOkClick(null)
            .setCancelTextColor(R.color.color_242424)
            .setOkTextColor(R.color.white)
            .setCancelBackground(R.drawable.bg_dialog_default_clean_btn)
            .setOkBackground(R.drawable.bg_dialog_default_ok_btn)
    }

    //清除内存
    fun clear() {
        dismiss()
    }

    inner class Builder {
        //是否点击返回键关闭。true,可以点击返回键关闭
        fun isBackDismiss(bl: Boolean): Builder {
            isBackDismiss = bl
            return this
        }

        //是否点击之后弹窗消失
        fun isClickDismiss(bl: Boolean): Builder {
            isClickDismiss = bl
            return this
        }

        /**
         * 显示一个确定按钮还是取消和确定两个按钮
         *
         * @param bl true,一个确定按钮；false，取消和确定按钮
         */
        fun isNoCancle(bl: Boolean): Builder {
            if (bl) {
                lyBtnTwo.visibility = View.INVISIBLE
                btnOkTwo.visibility = View.VISIBLE
            } else {
                lyBtnTwo.visibility = View.VISIBLE
                btnOkTwo.visibility = View.INVISIBLE
            }
            return this
        }

        /**
         * 是否显示标题
         *
         * @param bl true,显示；false，不显示
         */
        fun isShowTiltle(bl: Boolean): Builder {
            textTitle.visibility = if (bl) View.VISIBLE else View.GONE
            return this
        }

        /**
         * 设置标题
         *
         * @param title 标题内容
         */
        fun setTitle(title: String?): Builder {
            var title1 = title
            if (TextUtils.isEmpty(title1)) title1 = ""
            textTitle.text = title1
            return this
        }

        /**
         * 设置内容
         *
         * @param content 内容
         */
        fun setContent(content: String?): Builder {
            var content1 = content
            if (TextUtils.isEmpty(content1)) content1 = ""
            textContent.text = content1
            return this
        }

        /**
         * 显示html内容
         *
         * @param htmlContent 带有html标签的内容
         */
        fun setHtmlContent(htmlContent: String?): Builder {
            var htmlContent1 = htmlContent
            if (TextUtils.isEmpty(htmlContent1)) htmlContent1 = ""
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textContent.text = Html.fromHtml(htmlContent1,1)
            }else{
                textContent.text = Html.fromHtml(htmlContent1)
            }
            return this
        }

        /**
         * 设置确定按钮文字
         *
         * @param ok 文字
         */
        fun setOkText(ok: String?): Builder {
            var ok1 = ok
            if (TextUtils.isEmpty(ok1)) ok1 = ""
            btnOkTwo.text = ok1
            btnOk.text = ok1
            return this
        }

        /**
         * 设置取消按钮文字
         *
         * @param cancle 文字
         */
        fun setCancelText(cancle: String?): Builder {
            var cancle1 = cancle
            if (TextUtils.isEmpty(cancle1)) cancle1 = ""
            btnCancel.text = cancle1
            return this
        }

        /**
         * 设置确定按钮的文字颜色
         *
         * @param color 色值
         */
        fun setOkTextColor(color: Int): Builder {
            if (color == 0) return this
            btnOkTwo.setTextColor(ContextCompat.getColor(context, color))
            btnOk.setTextColor(ContextCompat.getColor(context, color))
            return this
        }

        /**
         * 设置取消按钮的文字颜色
         *
         * @param color 色值
         */
        fun setCancelTextColor(color: Int): Builder {
            if (color == 0) return this
            btnCancel.setTextColor(ContextCompat.getColor(context, color))
            return this
        }

        /**
         * 设置确定按钮的背景颜色
         *
         * @param res 资源
         */
        fun setOkBackground(res: Int): Builder {
            if (res == 0) return this
            btnOkTwo.setBackgroundResource(res)
            btnOk.setBackgroundResource(res)
            return this
        }

        /**
         * 设置取消按钮的背景颜色
         *
         * @param res 色值
         */
        fun setCancelBackground(res: Int): Builder {
            if (res == 0) return this
            btnCancel.setBackgroundResource(res)
            return this
        }

        /**
         * 确定的点击事件
         *
         * @param onClickListener 点击事件
         */
        fun setOkClick(onClickListener: IDefaultDialogClickListener?): Builder {
            btnOkTwo.setOnClickListener(OnDialogClick(onClickListener))
            btnOk.setOnClickListener(OnDialogClick(onClickListener))
            return this
        }

        /**
         * 取消的点击事件
         *
         * @param onClickListener 点击事件
         */
        fun setCancelClick(onClickListener: IDefaultDialogClickListener?): Builder {
            btnCancel.setOnClickListener(OnDialogClick(onClickListener))
            return this
        }

        //只能是dialog
        fun show() {
            showView()
        }

        //确定/取消 按钮点击事件
        private inner class OnDialogClick(private val iDefaultDialogClickListener: IDefaultDialogClickListener?) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                iDefaultDialogClickListener?.onClick(v)
                if (isClickDismiss) dismiss()
            }
        }
    }

    //监听返回按键
    private inner class OnKeyListener : DialogInterface.OnKeyListener {
        override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK && isBackDismiss) {  //表示按返回键时的操作
                    dismiss()
                    return true
                }
                return true //已处理
            }
            return false
        }
    }

    //显示dialog
    private fun showView() {
        try {
            setCanceledOnTouchOutside(false)
            show()
            setContentView(root)
            textContent.post {
                if (textContent.lineCount == 1) {
                    textContent.gravity = Gravity.CENTER
                }
            }
            if (window != null) window!!.setBackgroundDrawableResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        setOnKeyListener(OnKeyListener())
        root = View.inflate(context, R.layout.dialog_default, null)
        textTitle = root.findViewById(R.id.textTitle)
        textContent = root.findViewById(R.id.textContent)
        btnCancel = root.findViewById(R.id.btnCancel)
        btnOk = root.findViewById(R.id.btnOk)
        lyBtnTwo = root.findViewById(R.id.lyBtnTwo)
        btnOkTwo = root.findViewById(R.id.btnOkTwo)
        builder = Builder()
    }
}