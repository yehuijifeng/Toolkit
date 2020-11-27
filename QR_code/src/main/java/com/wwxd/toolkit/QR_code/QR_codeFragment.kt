package com.wwxd.toolkit.QR_code

import android.text.TextUtils
import android.view.View
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.wwdx.toolkit.utils.AppFile
import com.wwdx.toolkit.utils.DateUtil
import com.wwdx.toolkit.utils.FileUtil
import com.wwxd.toolkit.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_qr_code.*
import java.util.*
import kotlin.collections.HashMap


/**
 * user：LuHao
 * time：2020/11/26 18:16
 * describe：二维码扫描
 */
class QR_codeFragment : BaseFragment() {
    //6.0版本或以上需请求权限

    override fun setContentView(): Int {
        return R.layout.fragment_qr_code
    }

    override fun init(view: View) {
    }


    /**
     * 生成二维码
     */
    private fun QREncode() {
        if (TextUtils.isEmpty(etUrl.text)) return
        val content = etUrl.text.toString() //二维码内容
        val width = 200 // 图像宽度
        val height = 200 // 图像高度
        val format = "png" // 图像类型
        val hints: HashMap<EncodeHintType, Any?> = HashMap()
        //内容编码格式
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        // 指定纠错等级
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置二维码边的空度，非负数
        hints[EncodeHintType.MARGIN] = 1
        val bitMatrix =
            MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
        val codeFilePath =
            AppFile.IMAGE_SAVE.ObtainAppFilePath() + "/QR_Code_" + DateUtil.getServerTime() + ".png"

    }
}