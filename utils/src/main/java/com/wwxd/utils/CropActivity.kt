package com.wwxd.utils

import android.graphics.Bitmap
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.wwxd.base.AppConstant
import com.wwxd.base.AppSession
import com.wwxd.base.BaseActivity
import com.wwxd.utils.ImageClipView.InputCondition
import kotlinx.android.synthetic.main.activity_crop.*


/**
 * user：LuHao
 * time：2020/12/1 15:58
 * describe：图片剪裁
 */
class CropActivity : BaseActivity() {
    override fun isFullWindow(): Boolean {
        return true
    }

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
        //构建输入条件
        val condition = InputCondition.Builder() //裁剪框的类型，此处未矩形
            .setClipBorderType(ImageClipView.ClipBorderType.Rectangle) //裁剪框的颜色
            .setClipBorderColor(ContextCompat.getColor(this, R.color.color_999999)) //裁剪框的边线宽度，单位为像素
            .setClipBorderWidth(15) //裁剪框边线的触摸宽度，实际触摸宽度为 边线宽度 + 此处设置的宽度，单位为像素
            .setClipBorderAppendWidth(10) //裁剪框的宽度（外边框），单位像素
            .setClipBorderLayoutMinWidth(50) //裁剪框的高度（外边框），单位像素
            .setClipBorderLayoutMinHeight(50) //是否显示裁剪框的宽高值
            .setShowWidthHeightValue(true) //设置原始的Bitmap
            .setRawBitmap(BitmapUtil.zoomBitmapWindowWidth(bitmap))
            .build()
        icvCrop.onCreate(condition, 0)
        btnCrop.setOnClickListener {
            // 获取裁剪成的图片
            val croppedImage = icvCrop.getClippedBitmap(true)
            if (FileUtil.saveImageFilePath(croppedImage, savePath!!)) {
                AppSession.setSession(AppConstant.CROP_IMAGE, croppedImage)
                AppSession.setSession(AppConstant.CROP_IMAGE_SAVE_PATH, savePath)
                setResult(RESULT_OK)
                finish()
            }
        }
        btnRotating.setOnClickListener {
            icvCrop.setPivotX(icvCrop.getWidth() / 2f)
            icvCrop.setPivotY(icvCrop.getHeight() / 2f)//支点在图片中心
            rotation += 90f
            if (rotation == 360f)
                rotation = 0f
            icvCrop.setRotation(rotation)
            icvCrop.requestLayout()
        }
    }

    override fun onDestroy() {
        icvCrop.onDestroy()
        super.onDestroy()
    }
}