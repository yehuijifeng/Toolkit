package com.wwxd.tesseract_ocr

import com.wwxd.base.AppConstant
import com.wwxd.base.BaseActivity
import com.wwxd.utils.StringUtil
import com.wwxd.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * user：LuHao
 * time：2020/12/1 18:02
 * describe：扫描结果
 */
class ResultActivity : BaseActivity() {
    override fun getContentView(): Int {
        return R.layout.fragment_result
    }

    override fun init() {
        val resultContent = getString(AppConstant.OCR_RESULT, "")
        etContent.setText(resultContent)
        etContent.setSelection(etContent.text.length)
        etContent.requestFocus()
        btnCopy.setOnClickListener {
            StringUtil.copy(resultContent)
            ToastUtil.showLongToast(getString(R.string.str_copy_success))
        }
    }
}