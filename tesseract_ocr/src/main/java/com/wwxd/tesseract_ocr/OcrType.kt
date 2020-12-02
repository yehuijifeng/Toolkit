package com.wwxd.tesseract_ocr

import android.content.Context
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.*
import java.io.File

/**
 * user：LuHao
 * time：2020/12/2 11:31
 * describe：识别类型
 */
enum class OcrType {
    LicensePlate {//车牌，每日200次免费调用量
        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
        val params = OcrRequestParams()
        params.putParam("detect_direction", true)
        params.imageFile = imageFile//需要识别的文件
        // 调用通用文字识别服务
        OCR.getInstance(context)
            .recognizeLicensePlate(
                params,
                OnOcrResultListener<OcrResponseResult>(iOcrListener)
            )
        }
    },
    BusinessLicense {//营业执照，每日200次免费调用量
        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
            // 通用文字识别参数设置
            val params = OcrRequestParams()
            params.putParam("detect_direction", true)
            params.imageFile = imageFile//需要识别的文件
            // 调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeBusinessLicense(
                    params,
                    OnOcrResultListener<OcrResponseResult>(iOcrListener)
                )
        }
    },
    DrivingLicense {
        //驾驶证识别，每日200次免费调用量
        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
            // 通用文字识别参数设置
            val params = OcrRequestParams()
            params.putParam("detect_direction", true)
            params.imageFile = imageFile//需要识别的文件
            // 调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeDrivingLicense(
                    params,
                    OnOcrResultListener<OcrResponseResult>(iOcrListener)
                )
        }
    },
    General { //一般识别，每日 50000 次免费调用量
        override fun startOcr(
            context: Context,
            imageFile: File,
            iOcrListener: IOcrListener
        ) {
            // 通用文字识别参数设置
            val params = GeneralBasicParams()
            params.setDetectDirection(true)//检查方向
            params.imageFile = imageFile//需要识别的文件
            // 调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeGeneralBasic(params, OnOcrResultListener<GeneralResult>(iOcrListener))
        }
    },
    Accurate {    //精准识别，每日 500 次免费调用量
        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
            val params = GeneralBasicParams()
            params.setDetectDirection(true)//检查方向
            params.imageFile = imageFile
            // 调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeAccurateBasic(params, OnOcrResultListener<GeneralResult>(iOcrListener))
        }
    },
    IDCard {  //身份证识别，每日 500 次免费调用量
        private var frontStr = IDCardParams.ID_CARD_SIDE_FRONT

        //设置正反面
        override fun setFront(isFront: Boolean) {
            if (isFront)
                frontStr = IDCardParams.ID_CARD_SIDE_FRONT
            else
                frontStr = IDCardParams.ID_CARD_SIDE_BACK
        }

        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
            val params = IDCardParams()
            params.imageFile = imageFile
            params.idCardSide = frontStr//要设置正反面
            // 调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeIDCard(params, OnOcrResultListener<IDCardResult>(iOcrListener))
        }
    },
    BankCard { //银行卡识别，每日 500 次免费调用量
        override fun startOcr(context: Context, imageFile: File, iOcrListener: IOcrListener) {
            //通用文字识别参数设置
            val params = BankCardParams()
            params.imageFile = imageFile//需要识别的文件
            //调用通用文字识别服务
            OCR.getInstance(context)
                .recognizeBankCard(params, OnOcrResultListener<BankCardResult>(iOcrListener))
        }
    };
    //    Webimage,//网络图片识别，每日 500 次免费调用量
//    Excel,//表格文字识别，每日 50 次免费调用量
//    Number,//数字识别,每日 200 次免费调用量
//    a,//手写文字识别，每日50次免费调用量
//    Card4,//护照识别，一次性赠送 500 次免费调用量；2块30次

    abstract fun startOcr(
        context: Context,
        imageFile: File,
        iOcrListener: IOcrListener
    )

    open fun setFront(isFront: Boolean) {
    }

    //识别回调
    private inner class OnOcrResultListener<T>(val iOcrListener: IOcrListener) :
        OnResultListener<T> {

        override fun onResult(result: T) {
            val sb = StringBuilder()
            if (result is GeneralResult) {
                //普通识别
                // 调用成功，返回GeneralResult对象
                for (wordSimple in result.wordList) {
                    // wordSimple不包含位置信息
                    sb.append(wordSimple.words).append("\n")
                }
            } else if (result is BankCardResult) {
                //银行卡识别
                val type: String
                when (result.bankCardType) {
                    BankCardResult.BankCardType.Unknown -> type = "未知"
                    BankCardResult.BankCardType.Debit -> type = "借记卡"
                    BankCardResult.BankCardType.Credit -> type = "信用卡"
                    else -> type = "未知"
                }
                sb.append("银行卡类型：").append(type).append("\n")
                sb.append("银行：").append(result.bankName).append("\n")
                sb.append("卡号：").append(result.bankCardNumber)
            } else if (result is IDCardResult) {
                //身份证识别
                sb.append("身份证正反面：")
                    .append(if (result.idCardSide == IDCardParams.ID_CARD_SIDE_FRONT) "人头" else "国徽")
                    .append("\n")
                if (result.riskType != null)
                    sb.append("风险类型：").append(result.riskType).append("\n")
                if (result.imageStatus != null)
                    sb.append("头像：").append(result.imageStatus).append("\n")
                if (result.name != null && result.name.words != null)
                    sb.append("姓名：").append(result.name.words).append("\n")
                if (result.gender != null && result.gender.words != null)
                    sb.append("性别：").append(result.gender.words).append("\n")
                if (result.ethnic != null && result.ethnic.words != null)
                    sb.append("民族：").append(result.ethnic.words).append("\n")
                if (result.idNumber != null && result.idNumber.words != null)
                    sb.append("身份证号码：").append(result.idNumber.words).append("\n")
                if (result.birthday != null && result.birthday.words != null)
                    sb.append("出生年月：").append(result.birthday.words).append("\n")
                if (result.address != null && result.address.words != null)
                    sb.append("地址：").append(result.address.words).append("\n")
                if (result.issueAuthority != null && result.issueAuthority.words != null)
                    sb.append("签发机关：").append(result.issueAuthority.words).append("\n")
                if (result.signDate != null && result.signDate.words != null)
                    sb.append("签署日期：").append(result.signDate.words).append("\n")
                if (result.expiryDate != null && result.expiryDate.words != null)
                    sb.append("有效日期：").append(result.expiryDate.words)
            } else if (result is OcrResponseResult) {
                //驾驶证识别
                sb.append(result.jsonRes)
            }
            iOcrListener.onSuccess(sb.toString())
        }

        override fun onError(error: OCRError) {
            iOcrListener.onError(error)
        }
    }
}