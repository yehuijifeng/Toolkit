package com.wwxd.utils

import android.graphics.Bitmap
import android.text.TextUtils
import com.edmodo.cropper.CropImageView
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseActivity
import kotlinx.android.synthetic.main.activity_crop.*


/**
 * user：LuHao
 * time：2020/12/1 15:58
 * describe：图片剪裁
 */
class CropActivity : BaseActivity() {
    private var rotation = 0f
    override fun getContentView(): Int {
        return R.layout.activity_crop
    }

    override fun init() {
        val bitmap = AppSession.getSession<Bitmap>(AppConstant.CROP_IMAGE)
        val savePath = AppSession.getSession<String>(AppConstant.CROP_IMAGE_SAVE_PATH)
        if (bitmap == null || TextUtils.isEmpty(savePath)) {
            finish()
            return
        }
        AppSession.clearSession(AppConstant.CROP_IMAGE)
        AppSession.clearSession(AppConstant.CROP_IMAGE_SAVE_PATH)
        civCrop.setImageBitmap(bitmap)
        // 当触摸时候才显示网格线
        civCrop.setGuidelines(CropImageView.GUIDELINES_ON_TOUCH)
        btnCrop.setOnClickListener {
            // 获取裁剪成的图片
            val croppedImage: Bitmap = civCrop.getCroppedImage()
            if (FileUtil.saveImageFilePath(croppedImage, savePath!!)) {
                AppSession.setSession(AppConstant.CROP_IMAGE, croppedImage)
                AppSession.setSession(AppConstant.CROP_IMAGE_SAVE_PATH, savePath)
                setResult(RESULT_OK)
                finish()
            }
        }
        btnRotating.setOnClickListener {
            civCrop.setPivotX(civCrop.getWidth() / 2f)
            civCrop.setPivotY(civCrop.getHeight() / 2f)//支点在图片中心
            rotation += 90f
            if(rotation==360f)
                rotation=0f
            civCrop.setRotation(rotation)
            civCrop.requestLayout()
        }
    }
}