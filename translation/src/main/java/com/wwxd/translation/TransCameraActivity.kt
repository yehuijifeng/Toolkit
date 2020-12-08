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
import com.google.gson.JsonParser
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseActivity
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.*
import com.wwxd.utils.glide.GlideUtil
import com.wwxd.utils.http.Api
import com.wwxd.utils.http.IHttpResponse
import com.wwxd.utils.http.OkHttp
import com.wwxd.utils.photo.Image
import kotlinx.android.synthetic.main.activity_trans_camera.*
import java.io.File
import kotlin.random.Random


/**
 * user：LuHao
 * time：2020/12/7 17:05
 * describe：拍照翻译，每月可享用1万次免费调用量
 */
class TransCameraActivity : BaseActivity() {
    private var lauageFromType = Language.English
    private var lauageToType = Language.Chinese
    private val cameraCode = 111
    private var cameraPath = ""

    override fun getContentView(): Int {
        return R.layout.activity_trans_camera
    }

    override fun init() {
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
        return AppFile.IMAGE_CACHE.ObtainAppFilePath() + "crop_" + DateUtil.getServerTime() + ".jpeg"
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
                if (AppSession.containsSession(AppConstant.CROP_IMAGE_SAVE_PATH)
                ) {
                    val imageFilePath =
                        AppSession.getSession<String>(AppConstant.CROP_IMAGE_SAVE_PATH)
                    val file = File(imageFilePath)
                    if (!FileUtil.isFile(file)) return
                    getOcrResult(file)
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
    private fun getOcrResult(file: File) {
        showLoadingView(getString(R.string.str_ocr_ing))
        Api.TransVoice.params["image"] = file
        Api.TransVoice.params["from"] = lauageFromType.abbreviation
        Api.TransVoice.params["to"] = lauageToType.abbreviation
        Api.TransVoice.params["appid"] = BuildConfig.app_id
        val random = Random.nextLong(1000000000L, 9999999999L)
        Api.TransVoice.params["salt"] = random //随机数
        Api.TransVoice.params["cuid"] = "APICUID"
        Api.TransVoice.params["mac"] = "mac"
        val signStr = java.lang.StringBuilder()
        signStr.append(Api.TransVoice.params["appid"])
        signStr.append(MD5Util.getFileMD5String(file))
        signStr.append(Api.TransVoice.params["salt"])
        signStr.append(Api.TransVoice.params["cuid"])
        signStr.append(Api.TransVoice.params["mac"])
        signStr.append(BuildConfig.app_secret)
        Api.TransVoice.params["sign"] = MD5Util.md5(signStr.toString()) //随机数
        Api.TransVoice.iHttpResponse = object : IHttpResponse {
            override fun onSuccess(json: String) {
                val content = StringBuilder()
                val jsonObject = JsonParser.parseString(json).asJsonObject
                if (jsonObject.has("error_code")) {
                    val code = jsonObject["error_code"].asInt
                    if (code == 0) {
                        addCameraUseNum()
                        if (jsonObject.has("data")) {
                            val jsonObject1 = jsonObject["data"].asJsonObject
                            if (jsonObject1 != null) {
                                if (jsonObject1.has("sumSrc")) {
                                    content.append(getString(R.string.str_camera_trans_src))
                                        .append("\n")
                                        .append(jsonObject1["sumSrc"].asString)
                                        .append("\n")
                                        .append("\n")
                                }
                                if (jsonObject1.has("sumDst")) {
                                    content.append(getString(R.string.str_camera_trans_dst))
                                        .append("\n")
                                        .append(jsonObject1["sumDst"].asString)
                                }
                            }
                        }
                    } else {
                        if (jsonObject.has("error_msg")) {
                            content.append(jsonObject["error_msg"].asString)
                        }
                    }
                    addCameraUseNum()
                }
                etContent.setText(content)
                closeLoadingView()
            }

            override fun onFailure(error: String) {
                etContent.setText(error)
                closeLoadingView()
            }
        }
        OkHttp.requestPost(Api.TransVoice)
    }

    private fun addCameraUseNum() {
        SharedPreferencesUtil.saveInt(
            TransContanst.TransCameraNum,
            SharedPreferencesUtil.getInt(TransContanst.TransCameraNum, 0) + 1
        )
    }
}