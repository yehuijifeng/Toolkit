package com.wwxd.tesseract_ocr

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.*
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseFragment
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.*
import com.wwxd.utils.glide.GlideUtil
import com.wwxd.utils.photo.Image
import kotlinx.android.synthetic.main.fragment_ocr.*
import java.io.File
import java.util.*


/**
 * user：LuHao
 * time：2020/12/1 10:39
 * describe：图像文字提取ocr
 */
class OcrFragment : BaseFragment() {
    private val cameraCode = 111
    private var cameraPath = ""

    //是否将语言包放入
    private var isLoadOcrToken = ""

    private var thisOcrType: OcrType? = null//当前用户的选择

    override fun getContentView(): Int {
        return R.layout.fragment_ocr
    }

    override fun init(view: View) {
        initOcr()
        btnLicensePlate.setOnClickListener {
            thisOcrType = OcrType.LicensePlate
            startOcrDialog()
        }
        btnBusinessLicense.setOnClickListener {
            thisOcrType = OcrType.BusinessLicense
            startOcrDialog()
        }
        btnGeneral.setOnClickListener {
            thisOcrType = OcrType.General
            startOcrDialog()
        }
        btnAccurate.setOnClickListener {
            thisOcrType = OcrType.Accurate
            startOcrDialog()
        }
        btnBankCard.setOnClickListener {
            thisOcrType = OcrType.BankCard
            startOcrDialog()
        }
        sFont.setOnCheckedChangeListener { buttonView, isChecked ->
            OcrType.IDCard.setFront(isChecked)
            sFont.text = if (isChecked) "人头" else "国徽"
        }
        btnIDCard.setOnClickListener {
            thisOcrType = OcrType.IDCard
            startOcrDialog()
        }
        btnDrivingLicense.setOnClickListener {
            thisOcrType = OcrType.DrivingLicense
            startOcrDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        btnLicensePlate.text = getBtnText(R.string.str_btn_licenseplate, OcrType.LicensePlate)
        btnBusinessLicense.text =
            getBtnText(R.string.str_btn_businesslicense, OcrType.BusinessLicense)
        btnGeneral.text = getBtnText(R.string.str_btn_general, OcrType.General)
        btnAccurate.text = getBtnText(R.string.str_btn_accurate, OcrType.Accurate)
        btnBankCard.text = getBtnText(R.string.str_btn_bankcard, OcrType.BankCard)
        btnIDCard.text = getBtnText(R.string.str_btn_idcard, OcrType.IDCard)
        btnDrivingLicense.text = getBtnText(R.string.str_btn_drivinglicense, OcrType.DrivingLicense)
    }

    private fun getBtnText(res: Int, ocrType: OcrType): String {
        return String.format(Locale.getDefault(), getString(res), ocrType.getOneDayUseNum())
    }

    //去相册
    private fun startPhoto() {
        if (!TextUtils.isEmpty(isLoadOcrToken))
            PhoneUtil.toPhotos(this, 1)
        else
            ToastUtil.showLongToast(getString(R.string.str_ocr_init_ing))
    }

    //临时拍照路径
    private fun startCramera() {
        if (!TextUtils.isEmpty(isLoadOcrToken))
            if (!PermissionsUtil.lacksPermission(Manifest.permission.CAMERA)) {
                cameraPath =
                    AppFile.IMAGE_CACHE.ObtainAppFilePath() + "ocr_" + DateUtil.getServerTime() + ".jpg"
                PhoneUtil.toCamera(this, cameraPath)
            } else
                PermissionsUtil.requestPermissions(this, Manifest.permission.CAMERA, cameraCode)
        else
            ToastUtil.showLongToast(getString(R.string.str_ocr_init_ing))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            cameraCode -> {
                if (!PermissionsUtil.lacksPermission(Manifest.permission.CAMERA))
                    startCramera()
                else
                    getDefaultDialog().getBuilder()
                        .isShowTiltle(false)
                        .isBackDismiss(false)
                        .setContent(getString(R.string.str_camera_permission_error))
                        .setOkText(getString(R.string.str_go_settings))
                        .setCancelText(getString(R.string.str_cancel))
                        .setOkClick(object : IDefaultDialogClickListener {
                            override fun onClick(v: View) {
                                PermissionsUtil.goToSetting(getBaseActivity())
                            }
                        })
                        .show()
            }
        }
    }

    //临时剪切路径
    private fun getCropImagePath(): String {
        return AppFile.IMAGE_CACHE.ObtainAppFilePath() + "crop_" + DateUtil.getServerTime() + ".jpg"
    }

    //初始化
    private fun initOcr() {
        OCR.getInstance(AppConstant.getApp()).initAccessToken(object :
            OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                // 调用成功，返回AccessToken对象
                val token = result.accessToken
                isLoadOcrToken = token
            }

            override fun onError(error: OCRError) {
                ToastUtil.showLongToast(error.localizedMessage)
                // 调用失败，返回OCRError子类SDKError对象
                isLoadOcrToken = ""
            }
        }, AppConstant.getApp())
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PhoneUtil.CODE_FOR_PHOTO -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    val images = data.getParcelableArrayListExtra<Image>(AppConstant.LOOK_IMAGES)
                    if (images != null && images.size > 0) {
                        var imgUri = GlideUtil.getUri(images[0].uriId)
                        if (AppUtil.isAndroidQ())
                            imgUri = MediaStore.setRequireOriginal(imgUri)
                        PhoneUtil.toCrop(this, imgUri, getCropImagePath())
                    }
                }
            }
            PhoneUtil.CODE_FOR_CAMERA -> {
                if (!TextUtils.isEmpty(cameraPath) && FileUtil.isFile(cameraPath)) {
                    PhoneUtil.toCrop(this, FileUtil.toUri(cameraPath), getCropImagePath())
                }
            }
            PhoneUtil.CODE_FOR_CROP -> {
                if (AppSession.containsSession(AppConstant.CROP_IMAGE_SAVE_PATH)
                    && AppSession.containsSession(AppConstant.CROP_IMAGE)
                ) {
                    val filePath = AppSession.getSession<String>(AppConstant.CROP_IMAGE_SAVE_PATH)
                    if (thisOcrType == null
                        || TextUtils.isEmpty(filePath)
                    ) return
                    val imageFile = File(filePath!!)
                    if (!FileUtil.isFile(imageFile)) return
                    showLoadingView(getString(R.string.str_ocr_ing))
                    thisOcrType!!.startOcr(context!!, imageFile, OnOcrListener())
                }
            }
        }
    }

    //识别结果回调
    private inner class OnOcrListener : IOcrListener {
        override fun onSuccess(content: String) {
            val bundle = Bundle()
            bundle.putString(AppConstant.OCR_RESULT, content)
            startActivity(ResultActivity::class, bundle)
            thisOcrType!!.addUseNum()
            thisOcrType = null
            closeLoadingView()
        }

        override fun onError(error: OCRError) {
            closeLoadingView()
            ToastUtil.showFailureToast(error.message)
        }
    }

    //发起识别
    private fun startOcrDialog() {
        getDefaultDialog().getBuilder()
            .isBackDismiss(true)
            .setTitle(getString(R.string.str_start_ocr_title))
            .setContent(
                getString(R.string.str_start_ocr_content)
            )
            .setCancelText(getString(R.string.str_photo))
            .setOkText(getString(R.string.str_camera))
            .setCancelClick(object : IDefaultDialogClickListener {
                override fun onClick(v: View) {
                    startPhoto()
                }
            })
            .setOkClick(object : IDefaultDialogClickListener {
                override fun onClick(v: View) {
                    startCramera()
                }
            })
            .show()
    }


}