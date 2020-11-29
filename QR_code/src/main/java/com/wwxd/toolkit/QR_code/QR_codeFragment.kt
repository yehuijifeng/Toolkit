package com.wwxd.toolkit.QR_code

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.Result
import com.wwdx.toolkit.utils.*
import com.wwdx.toolkit.utils.glide.GlideUtil
import com.wwdx.toolkit.utils.photo.Image
import com.wwdx.toolkit.utils.photo.PhotoActivity
import com.wwdx.toolkit.utils.photo.PhotoConstant
import com.wwdx.toolkit.utils.rxandroid.schedulers.AndroidSchedulers
import com.wwxd.toolkit.QR_code.decode.DecodeImgCallback
import com.wwxd.toolkit.QR_code.decode.DecodeImgThread
import com.wwxd.toolkit.QR_code.encode.CodeCreator
import com.wwxd.toolkit.base.BaseFragment
import com.wwxd.toolkit.base.IDefaultDialogClickListener
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_qr_code.*
import java.io.File
import java.util.*


/**
 * user：LuHao
 * time：2020/11/26 18:16
 * describe：二维码扫描
 */
class QR_codeFragment : BaseFragment() {

    private val cameraCode = 111
    private val fileCode = cameraCode + 1
    private val imageCode = fileCode + 1

    override fun setContentView(): Int {
        return R.layout.fragment_qr_code
    }

    override fun init(view: View) {
        btnPicture.setOnClickListener {//拍照
            if (PermissionsUtil.lacksPermission(Manifest.permission.CAMERA)) {
                startQRCodeActivty()
            } else {
                PermissionsUtil.requestPermissions(
                    this,
                    Manifest.permission.CAMERA,
                    cameraCode
                )
            }
        }
        btnPhoto.setOnClickListener {//相册
            if (!PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
                PermissionsUtil.requestPermissions(
                    this,
                    PermissionsUtil.getSdCardPermissions(),
                    fileCode
                )
            } else {
                startPhotoDeCode()
            }
        }
        btnCode.setOnClickListener {
            if (!TextUtils.isEmpty(etUrl.text)) {
                val content = etUrl.text.toString()
                hideSoftInputFromWindow(etUrl)
                showLoadingView()
                Observable
                    .create(OnCreateCodeBitmapObservableOnSubscribe(content))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(OnCreateCodeBitmapObserver())
            }
        }
    }

    //生成二维码
    private class OnCreateCodeBitmapObservableOnSubscribe(val content: String) :
        ObservableOnSubscribe<Bitmap> {
        override fun subscribe(emitter: ObservableEmitter<Bitmap>) {
            emitter.onNext(CodeCreator.createQRCode(content))
        }
    }

    //生成完毕，主线程
    private inner class OnCreateCodeBitmapObserver : Observer<Bitmap> {
        override fun onSubscribe(d: Disposable) {}
        override fun onNext(bitmap: Bitmap?) {
            closeLoadingView()
            if (bitmap != null) {
                imgCode.visibility = View.VISIBLE
                imgCode.setImageBitmap(bitmap)
                imgCode.setOnClickListener {
                    //保存二维码到手机
                    getDefaultDialog().getBuilder()
                        .isNoCancle(false)
                        .isBackDismiss(false)
                        .isShowTiltle(false)
                        .setContent(getString(R.string.str_save_code_image))
                        .setOkText(getString(R.string.str_save))
                        .setOkClick(object : IDefaultDialogClickListener {
                            override fun onClick(v: View) {
                                val filePath =
                                    AppFile.IMAGE_SAVE.ObtainAppFilePath() + "QR_Code_" + DateUtil.getServerTime() + ".jpg"
                                if (FileUtil.saveImageFilePath(bitmap, filePath)) {
                                    val file = File(filePath)
                                    val uri = MediaStore.Images.Media.insertImage(
                                        getBaseActivity().contentResolver,
                                        file.absolutePath,
                                        file.name,
                                        AppUtil.appName()
                                    )
                                    getBaseActivity().sendBroadcast(
                                        Intent(
                                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                            Uri.parse(uri)
                                        )
                                    )
                                    ToastUtil.showFailureToast(getString(R.string.str_save_code_image_success))
                                } else {
                                    ToastUtil.showFailureToast(getString(R.string.str_save_failed))
                                }
                            }
                        })
                        .setCancelText(getString(R.string.str_cancel))
                        .show()
                }
            } else {
                imgCode.visibility = View.GONE
                ToastUtil.showFailureToast(getString(R.string.str_code_create_error))
                imgCode.setOnClickListener(null)
            }
        }

        override fun onError(e: Throwable) {}
        override fun onComplete() {}
    }

    //去相册选择
    private fun startPhotoDeCode() {
        val bundle = Bundle()
        bundle.putInt(PhotoConstant.MAX_IMAGE_NUM, 1)
        startActivityForResult(PhotoActivity::class, bundle, imageCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == imageCode
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
        ) {
            val images = data.getParcelableArrayListExtra<Image>(PhotoConstant.LOOK_IMAGES)
            if (images == null || images.size == 0) {
                textDecodeContent.visibility = View.GONE
                ToastUtil.showFailureToast(getString(R.string.str_image_decode_error))
            } else {
                var imgUri = GlideUtil.getUri(images[0].uriId)
                if (AppUtil.isAndroidQ())
                    imgUri = MediaStore.setRequireOriginal(imgUri)
                DecodeImgThread(imgUri, object : DecodeImgCallback {
                    override fun onImageDecodeSuccess(result: Result) {
                        //扫描成功，处理反馈信息
                        if (!TextUtils.isEmpty(result.text)) {
                            showCodeContent(result.text)
                        } else {
                            textDecodeContent.visibility = View.GONE
                            ToastUtil.showFailureToast(getString(R.string.str_image_decode_error))
                        }
                    }

                    override fun onImageDecodeFailed() {
                        textDecodeContent.visibility = View.GONE
                        ToastUtil.showFailureToast(getString(R.string.str_image_decode_error))
                    }
                }).run()
            }
        } else if (requestCode == cameraCode
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
        ) {
            val content = data.getStringExtra(Constant.CODED_CONTENT)
            if (!TextUtils.isEmpty(content))
                showCodeContent(content)
            else {
                textDecodeContent.visibility = View.GONE
                ToastUtil.showFailureToast(getString(R.string.str_sao_error))
            }
        }
    }

    private fun showCodeContent(content: String) {
        textDecodeContent.visibility = View.VISIBLE
        textDecodeContent.text = content
        textDecodeContent.setOnClickListener {
            val content1 = textDecodeContent.text.toString()
            if (!TextUtils.isEmpty(content1)) {
                if (content1.toLowerCase(Locale.getDefault()).contains("http")) {
                    //从其他浏览器打开
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    val content_url = Uri.parse(content1)
                    intent.data = content_url
                    startActivity(
                        Intent.createChooser(
                            intent,
                            getString(R.string.str_chooser_tips)
                        )
                    )
                } else {
                    StringUtil.copy(content1)
                    ToastUtil.showLongToast(getString(R.string.str_copy_success))
                }
            }
        }
        textDecodeContent.setOnLongClickListener { view ->
            val content1 = textDecodeContent.text.toString()
            if (!TextUtils.isEmpty(content1)) {
                StringUtil.copy(content1)
                ToastUtil.showLongToast(getString(R.string.str_copy_success))
                true
            } else
                false

        }
    }

    private fun startQRCodeActivty() {
        val intent = Intent(context, CaptureActivity::class.java)
        startActivityForResult(intent, cameraCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraCode) {
            if (PermissionsUtil.lacksPermission(Manifest.permission.CAMERA)) {
                ToastUtil.showLongToast(getString(R.string.str_open_camera_permission))
            } else {
                startQRCodeActivty()
            }
        } else if (requestCode == fileCode) {
            if (PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
                ToastUtil.showLongToast(getString(R.string.str_sd_card_permission_error))
            } else {
                startPhotoDeCode()
            }
        }
    }


}