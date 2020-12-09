package com.wwxd.toolkit.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.wwxd.base.NoDoubleClickListener
import com.wwxd.toolkit.R
import com.wwxd.toolkit.activity.PrivacyActivity
import com.wwxd.toolkit.listener.IAgreementListener
import com.wwxd.utils.AppUtil
import com.wwxd.utils.StringUtil.getString

/**
 * user：LuHao
 * time：20200/02/28 10:00
 * describe：首次进入app的协议弹窗
 */
class AgreementDialog(context: Context) : AlertDialog(context) {
    private var root: View
    private var iAgreementListener: IAgreementListener? = null

    init {
        setOnKeyListener(OnKeyListener())
        root = View.inflate(context, R.layout.dialog_agreement, null)
        val textTitle = root.findViewById<TextView>(R.id.textTitle)
        textTitle.setText(
            StringBuilder(context.getString(R.string.str_agreement_title_tips)).append(
                AppUtil.appName()
            )
        )
        val textContent = root.findViewById<TextView>(R.id.textContent)
        val btnCancel = root.findViewById<TextView>(R.id.btnCancel)
        val btnOk = root.findViewById<TextView>(R.id.btnOk)
        textContent.text = getAgreementContent()
        textContent.highlightColor = ContextCompat.getColor(context, R.color.transparent)
        textContent.movementMethod = LinkMovementMethod.getInstance()
        btnCancel.setOnClickListener(OnClick())
        btnOk.setOnClickListener(OnClick())
    }

    private inner class OnClick : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View) {
            dismiss()
            if (iAgreementListener != null) {
                if (v.id == R.id.btnOk) {
                    iAgreementListener!!.confirm()
                } else {
                    iAgreementListener!!.clean()
                }
            }
        }
    }

    //显示用户协议和隐私政策
    private fun getAgreementContent(): Spanned {
        val xieyi = getString(R.string.str_agreement_dialog_content)
        val spanableInfo = SpannableString(xieyi)
        val PrivacyPolicy = getString(R.string.str_private_agreement)
        if (xieyi.contains(PrivacyPolicy)) {
            val startIndex = xieyi.indexOf(PrivacyPolicy)
            val endIndex = startIndex + PrivacyPolicy.length
            spanableInfo.setSpan(
                OnTextSpanClickableSpan(),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spanableInfo
    }

    //用户协议
    private inner class OnTextSpanClickableSpan : ClickableSpan() {
        /**
         * 重写父类点击事件
         */
        override fun onClick(v: View) {
            val intent = Intent(context, PrivacyActivity::class.java)
            context.startActivity(intent)
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = true // 设置文字下划线不显示
            ds.color = ContextCompat.getColor(context, R.color.color_26d5d0)
        }
    }

    //监听返回按键
    private inner class OnKeyListener : DialogInterface.OnKeyListener {
        override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
            return event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    //显示dialog
    fun showView(iAgreementListener: IAgreementListener) {
        this.iAgreementListener = iAgreementListener
        try {
            setCanceledOnTouchOutside(false)
            show()
            setContentView(root)
            if (window != null) window!!.setBackgroundDrawableResource(android.R.color.transparent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}