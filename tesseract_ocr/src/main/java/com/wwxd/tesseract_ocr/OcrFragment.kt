package com.wwxd.tesseract_ocr

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.googlecode.tesseract.android.TessBaseAPI
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseFragment
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.*
import com.wwxd.utils.glide.GlideUtil
import com.wwxd.utils.photo.Image
import com.wwxd.utils.rxandroid.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_ocr.*
import java.io.File
import java.io.InputStream
import kotlin.concurrent.thread


/**
 * user：LuHao
 * time：2020/12/1 10:39
 * describe：图像文字提取ocr
 */
class OcrFragment : BaseFragment() {
    private val cameraCode = 111
    private var cameraPath = ""
    private var cropPath = ""

    // 默认识别语言
    private val DEFAULT_LANGUAGE_ENG = "eng"

    // 默认识别语言
    private val DEFAULT_LANGUAGE_CHI = "chi_sim"

    //语言包后缀
    private val LANGUAGE_SUFFIX = ".traineddata"

    // 数据包的路径,tessdata名字不能变
    private val TESSBASE_PATH = AppFile.DOCUMENTS_FILE.ObtainAppFilePath() + "tessdata/"
    private val loadTessData_Sim = DEFAULT_LANGUAGE_ENG + LANGUAGE_SUFFIX
    private val loadTessData_Tra = DEFAULT_LANGUAGE_CHI + LANGUAGE_SUFFIX

    //是否将语言包放入
    private var isLoadTessData = 0

    override fun getContentView(): Int {
        return R.layout.fragment_ocr
    }

    override fun init(view: View) {
        btnPhoto.setOnClickListener {
            if (isLoadTessData == 2)
                PhoneUtil.toPhotos(this, 1)
            else ToastUtil.showLongToast(getString(R.string.str_ocr_init_ing))
        }
        btnCamera.setOnClickListener {
            if (isLoadTessData == 2)
                if (!PermissionsUtil.lacksPermission(Manifest.permission.CAMERA))
                    startCramera()
                else
                    PermissionsUtil.requestPermissions(this, Manifest.permission.CAMERA, cameraCode)
            else ToastUtil.showLongToast(getString(R.string.str_ocr_init_ing))
        }
        thread {
            if (FileUtil.isDirectory(TESSBASE_PATH)
                && FileUtil.isFile(TESSBASE_PATH + loadTessData_Sim)
                && FileUtil.isFile(TESSBASE_PATH + loadTessData_Tra)
            ) {
                isLoadTessData = 2
            } else {
                if (!FileUtil.isDirectory(TESSBASE_PATH)) {
                    if (FileUtil.createFileDirectory(TESSBASE_PATH, false)) {
                        loadTessDataFile()
                    }
                } else {
                    loadTessDataFile()
                }
            }
        }
    }

    //加载语言包
    private fun loadTessDataFile() {
        if (!FileUtil.isFile(TESSBASE_PATH + loadTessData_Sim)) {
            val input: InputStream = AppConstant.getApp().assets.open(loadTessData_Sim)
            val file = FileUtil.insertFile(input, TESSBASE_PATH + loadTessData_Sim)
            if (FileUtil.isFile(file))
                isLoadTessData += 1
        } else isLoadTessData += 1
        if (!FileUtil.isFile(TESSBASE_PATH + loadTessData_Tra)) {
            val input: InputStream = AppConstant.getApp().assets.open(loadTessData_Tra)
            val file = FileUtil.insertFile(input, TESSBASE_PATH + loadTessData_Tra)
            if (FileUtil.isFile(file))
                isLoadTessData += 1
        } else isLoadTessData += 1
    }

    private fun startCramera() {
        cameraPath =
            AppFile.IMAGE_CACHE.ObtainAppFilePath() + "ocr_" + DateUtil.getServerTime() + ".jpg"
        PhoneUtil.toCamera(this, cameraPath)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PhoneUtil.CODE_FOR_PHOTO -> {
                if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
                    val images = data.getParcelableArrayListExtra<Image>(AppConstant.LOOK_IMAGES)
                    if (images == null || images.size == 0) {
                        ToastUtil.showFailureToast(getString(R.string.str_photo_select_error))
                    } else {
                        var imgUri = GlideUtil.getUri(images[0].uriId)
                        if (AppUtil.isAndroidQ())
                            imgUri = MediaStore.setRequireOriginal(imgUri)
                        cropPath =
                            AppFile.IMAGE_CACHE.ObtainAppFilePath() + "crop_" + DateUtil.getServerTime() + ".jpg"
                        PhoneUtil.toCrop(this, imgUri, cropPath)
                    }
                }
            }
            PhoneUtil.CODE_FOR_CAMERA -> {
                if (TextUtils.isEmpty(cameraPath) || !FileUtil.isFile(cameraPath)) {
                    ToastUtil.showLongToast(getString(R.string.str_camera_image_error))
                } else {
                    cropPath =
                        AppFile.IMAGE_CACHE.ObtainAppFilePath() + "crop_" + DateUtil.getServerTime() + ".jpg"
                    PhoneUtil.toCrop(this, FileUtil.toUri(cameraPath), cropPath)
                }
            }
            PhoneUtil.CODE_FOR_CROP -> {
                if (!AppSession.containsSession(AppConstant.CROP_IMAGE_SAVE_PATH)
                    || !AppSession.containsSession(AppConstant.CROP_IMAGE)
                ) {
                    ToastUtil.showLongToast(getString(R.string.str_crop_error))
                } else {
                    showLoadingView(getString(R.string.str_ocr_ing))
                    Observable
                        .create(
                            OnOcrDecodeObservableOnSubscribe(
                                AppSession.getSession<Bitmap>(
                                    AppConstant.CROP_IMAGE
                                )
                            )
                        )
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(OnOcrDecodeObserver())
                }
            }
        }
    }

    //生成二维码
    private inner class OnOcrDecodeObservableOnSubscribe(val bitmap: Bitmap?) :
        ObservableOnSubscribe<String> {
        private var tessBaseAPI: TessBaseAPI? = null
            get() {
                if (field == null) {
                    field = TessBaseAPI()
                    field!!.setDebug(true)
                    field!!.pageSegMode = TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK //设置识别模式
                }
                return field
            }

        override fun subscribe(emitter: ObservableEmitter<String>) {
            var inspection: String
            if (bitmap == null) {
                inspection = getString(R.string.str_decode_error)
                emitter.onNext(inspection)
            } else {
                tessBaseAPI!!.init(
                    AppFile.DOCUMENTS_FILE.ObtainAppFilePath(),
                    DEFAULT_LANGUAGE_CHI
                ) //eng为识别语言
//        tessBaseAPI.setVariable(
//            TessBaseAPI.VAR_CHAR_WHITELIST,
//            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
//        ) // 识别白名单
//        tessBaseAPI.setVariable(
//            TessBaseAPI.VAR_CHAR_BLACKLIST,
//            "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?"
//        ) // 识别黑名单
                tessBaseAPI!!.setImage(bitmap) //设置需要识别图片的bitmap
                inspection = tessBaseAPI!!.getUTF8Text()
                tessBaseAPI!!.end()
                if (TextUtils.isEmpty(inspection))
                    inspection = getString(R.string.str_decode_error)
                emitter.onNext(inspection)
            }
        }
    }

    //生成完毕，主线程
    private inner class OnOcrDecodeObserver : Observer<String> {
        override fun onSubscribe(d: Disposable) {}

        override fun onError(e: Throwable) {}
        override fun onComplete() {}
        override fun onNext(t: String) {
            closeLoadingView()
            val bundle = Bundle()
            bundle.putString(AppConstant.OCR_RESULT, t)
            startActivity(ResultActivity::class, bundle)
        }
    }


}