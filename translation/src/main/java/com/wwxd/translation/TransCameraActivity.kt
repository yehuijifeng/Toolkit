package com.wwxd.translation

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.baidu.translate.asr.data.Language
import com.baidu.translate.ocr.OcrCallback
import com.baidu.translate.ocr.OcrClient
import com.baidu.translate.ocr.OcrClientFactory
import com.baidu.translate.ocr.entity.OcrResult
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseActivity
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.*
import com.wwxd.utils.glide.GlideUtil
import com.wwxd.utils.photo.Image
import kotlinx.android.synthetic.main.activity_trans_camera.*


/**
 * user：LuHao
 * time：2020/12/7 17:05
 * describe：拍照翻译，每月可享用1万次免费调用量
 */
class TransCameraActivity : BaseActivity() {
    private var client: OcrClient? = null
    private var lauageFromType = Language.English
    private var lauageToType = Language.Chinese
    private val cameraCode = 111
    private var cameraPath = ""

    override fun getContentView(): Int {
        return R.layout.activity_trans_camera
    }

    override fun init() {
        client = OcrClientFactory.create(this, BuildConfig.app_id, BuildConfig.app_secret)
        spinnerCountriesFrom.setAdapter(OnSpinnerAdapterFrom())
        spinnerCountriesFrom.setOnItemSelectedListener(OnItemSelectedListenerFrom())
        spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageFromType), true)
        spinnerCountriesTo.setAdapter(OnSpinnerAdapterTo())
        spinnerCountriesTo.setOnItemSelectedListener(OnItemSelectedListenerTo())
        spinnerCountriesTo.setSelection(Language.values().indexOf(lauageToType), true)
        btnCamera.setOnClickListener {
            startCramera()
        }
        btnPhoto.setOnClickListener {
            PhoneUtil.toPhotos(this, 1)
        }
    }

    //临时拍照路径
    private fun startCramera() {
        if (!PermissionsUtil.lacksPermission(Manifest.permission.CAMERA)) {
            cameraPath =
                AppFile.IMAGE_CACHE.ObtainAppFilePath() + "ocr_" + DateUtil.getServerTime() + ".jpg"
            PhoneUtil.toCamera(this, cameraPath)
        } else
            PermissionsUtil.requestPermissions(this, Manifest.permission.CAMERA, cameraCode)
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
                                PermissionsUtil.goToSetting(this@TransCameraActivity)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PhoneUtil.CODE_FOR_PHOTO -> {
                if (resultCode == RESULT_OK && data != null) {
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
                if (AppSession.containsSession(AppConstant.CROP_IMAGE)
                ) {
                    val bitmap = AppSession.getSession<Bitmap>(AppConstant.CROP_IMAGE)
                    if (bitmap == null) return
                    getOcrResult(bitmap)
                }
            }
        }
    }

    //选择语言
    private inner class OnSpinnerAdapterFrom :
        ArrayAdapter<Language>(this, R.layout.item_lauage_spinner, Language.values()) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerFrom : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val type = Language.values()[position]
            if (lauageToType == type) {
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageToType), true)
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageFromType), true)
                lauageToType = lauageFromType
                lauageFromType = type
            } else if (!Language.isAsrAvailable(type.abbreviation)) {//判断某个语种是否支持语音识别
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageFromType), true)
                ToastUtil.showLongToast(
                    String.format(
                        getString(R.string.str_translation_available),
                        type.language
                    )
                )
            } else
                lauageFromType = type
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    //选择语言
    private inner class OnSpinnerAdapterTo :
        ArrayAdapter<Language>(this, R.layout.item_lauage_spinner, Language.values()) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val convertView1 = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_lauage_spinner, parent, false)
            val textSpinner = convertView1.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView1 = convertView
            if (convertView1 == null) {
                convertView1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_lauage_spinner_two, parent, false)
            }
            val textSpinner = convertView1!!.findViewById<TextView>(R.id.textSpinner)
            textSpinner.setText(Language.values()[position].language)
            return convertView1
        }
    }

    //选择语言监听
    private inner class OnItemSelectedListenerTo : AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val type = Language.values()[position]
            if (lauageFromType == type) {
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageFromType), true)
                spinnerCountriesFrom.setSelection(Language.values().indexOf(lauageToType), true)
                lauageFromType = lauageToType
                lauageToType = type
            } else if (!Language.isAsrAvailable(type.abbreviation)) {//判断某个语种是否支持语音识别
                spinnerCountriesTo.setSelection(Language.values().indexOf(lauageToType), true)
                ToastUtil.showLongToast(
                    String.format(
                        getString(R.string.str_translation_available),
                        type.language
                    )
                )
            } else
                lauageToType = type
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    /**
     * 异步请求获取OCR翻译结果
     * @param from     源语言方向
     * @param to       目标语言方向
     * @param bitmap     图片文件
     * @param callback 识别回调
     */
    private fun getOcrResult(bitmap: Bitmap) {
        showLoadingView(getString(R.string.str_ocr_ing))
        client!!.getOcrResult(
            lauageFromType.abbreviation,
            lauageToType.abbreviation,
            bitmap,
            OnOcrCallback()
        )
    }

    private inner class OnOcrCallback : OcrCallback {
        override fun onOcrResult(result: OcrResult) {
            val content = StringBuilder()
            if (result.error == 0) {
                addCameraUseNum()
                content.append("识别的原文：").append(result.sumSrc).append("\n")
                content.append("翻译的结果：").append(result.sumDst)
            } else {
                content.append(result.errorMsg)
            }
            etContent.setText(content)
            closeLoadingView()
        }
    }

    private fun addCameraUseNum() {
        SharedPreferencesUtil.saveInt(
            TransContanst.TransCameraNum,
            SharedPreferencesUtil.getInt(TransContanst.TransCameraNum, 0) + 1
        )
    }
}