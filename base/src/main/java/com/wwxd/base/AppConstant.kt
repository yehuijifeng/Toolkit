package com.wwxd.base


/**
 * user：LuHao
 * time：2020/11/25 14:49
 * describe：常量
 */
object AppConstant {
    var application: BaseApp? = null
    fun setApp(baseApp: BaseApp) {
        application = baseApp
    }

    fun getApp(): BaseApp {
        return application!!
    }

    const val fileProvider = "com.wwxd.toolkit.fileProvider" //存储文件位置配置信息
    const val sharedPreferences_key = "sharedpreferences_key";//sharedpreferences的key
    const val serviceEmail = "928186846@qq.com"
    const val serviceTel = "15800729572"
    const val MAX_IMAGE_NUM = "MAX_IMAGE_NUM"
    const val LOOK_IMAGES = "LOOK_IMAGES"
    const val LOOK_IMAGES_INDEX = "LOOK_IMAGES_INDEX"
    const val CROP_IMAGE = "CROP_IMAGE"
    const val CROP_IMAGE_SAVE_PATH = "CROP_IMAGE_SAVE_PATH"
    const val OCR_RESULT = "OCR_RESULT"
    const val OCR_LicensePlate = "OCR_LicensePlate"
    const val OCR_BusinessLicense = "OCR_BusinessLicense"
    const val OCR_DrivingLicense = "OCR_DrivingLicense"
    const val OCR_General = "OCR_General"
    const val OCR_Accurate = "OCR_Accurate"
    const val OCR_IDCard = "OCR_IDCard"
    const val OCR_BankCard = "OCR_BankCard"

}