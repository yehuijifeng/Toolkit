package com.wwxd.toolkit.QR_code

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.zxing.Result
import com.google.zxing.ResultPoint
import com.google.zxing.ResultPointCallback
import com.wwxd.toolkit.QR_code.android.CaptureActivityHandler
import com.wwxd.toolkit.QR_code.android.FinishListener
import com.wwxd.toolkit.QR_code.camera.CameraManager
import com.wwxd.toolkit.QR_code.common.Constant
import com.wwxd.toolkit.QR_code.decode.DecodeImgCallback
import com.wwxd.toolkit.QR_code.decode.DecodeImgThread
import com.wwxd.toolkit.QR_code.decode.ImageUtil
import com.wwxd.toolkit.base.BaseActivity
import kotlinx.android.synthetic.main.activity_capture.*

/**
 * user：LuHao
 * time：2020/11/27 16:16
 * describe：二维码扫描
 */
class CaptureActivity : BaseActivity(), SurfaceHolder.Callback, View.OnClickListener {
    private var hasSurface = false
    private var inactivityTimer: InactivityTimer? = null
    private var cameraManager: CameraManager? = null
    private var handler: CaptureActivityHandler? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var beepManager: BeepManager? = null

    fun drawViewfinder() {
        viewfinderView!!.drawViewfinder()
    }

    override fun setContentView(): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return R.layout.activity_capture
    }

    override fun init() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        /*先获取配置信息*/
        surfaceView.setOnClickListener(this)
        viewfinderView.setOnClickListener(this)
        flashLightLayout.setOnClickListener(this)
        albumLayout.setOnClickListener(this)
        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(packageManager)) {
            flashLightLayout.setVisibility(View.VISIBLE)
        } else {
            flashLightLayout.setVisibility(View.GONE)
        }
        hasSurface = false
        inactivityTimer = InactivityTimer(this)//防止休眠
        beepManager = BeepManager(this)//声音和震动
    }

    /*切换手电筒图片*/
    fun switchFlashImg(flashState: Int) {
        if (flashState == Constant.FLASH_OPEN) {
            flashLightIv!!.setImageResource(R.drawable.ic_toast_failure)
            flashLightTv!!.text = getString(R.string.str_close_flash)
        } else {
            flashLightIv!!.setImageResource(R.drawable.ic_toast_success)
            flashLightTv!!.text = getString(R.string.str_open_flash)
        }
    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     */
    fun handleDecode(rawResult: Result) {
        inactivityTimer?.onActivity()
        beepManager?.palyBeep()
        val bundle = Bundle()
        bundle.putString(Constant.CODED_CONTENT, rawResult.text)
        intent.putExtras(bundle)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        cameraManager = CameraManager()
        viewfinderView!!.setCameraManager(cameraManager)
        handler = null
        surfaceHolder = surfaceView.holder
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder!!.addCallback(this)
        }
        inactivityTimer!!.onResume()
    }

    /**
     * 初始化Camera
     */
    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        checkNotNull(surfaceHolder) {
            "No SurfaceHolder provided"
        }
        if (cameraManager == null || cameraManager!!.isOpen)
            return
        try {
            // 打开Camera硬件设备
            cameraManager!!.openDriver(surfaceHolder)
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = CaptureActivityHandler(ViewfinderResultPointCallback(), cameraManager)
            }
        } catch (e: Exception) {
            displayFrameworkBugMessageAndExit()
        }
    }

    /**
     * 取景器结果点回调
     */
    private inner class ViewfinderResultPointCallback : ResultPointCallback {
        override fun foundPossibleResultPoint(point: ResultPoint) {
            viewfinderView.addPossibleResultPoint(point)
        }
    }

    /**
     * 显示错误信息
     */
    private fun displayFrameworkBugMessageAndExit() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("扫一扫")
        builder.setMessage("扫描失败")
        builder.setPositiveButton(R.string.button_ok, FinishListener(this))
        builder.setOnCancelListener(FinishListener(this))
        builder.show()
    }

    override fun onPause() {
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        inactivityTimer?.onPause()
        beepManager?.close()
        cameraManager?.closeDriver()
        if (!hasSurface) {
            surfaceHolder?.removeCallback(this)
        }
        super.onPause()
    }

    override fun onDestroy() {
        inactivityTimer?.shutdown()
        super.onDestroy()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
    }

    override fun surfaceChanged(
        holder: SurfaceHolder, format: Int, width: Int,
        height: Int
    ) {
    }

    /*点击事件*/
    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.flashLightLayout) {
            /*切换闪光灯*/
            cameraManager!!.switchFlashLight(handler)
        } else if (id == R.id.albumLayout) {
            /*打开相册*/
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, Constant.REQUEST_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.REQUEST_IMAGE
            && resultCode == RESULT_OK
            && data != null
        ) {
            val path = ImageUtil.getImageAbsolutePath(this, data.data)
            DecodeImgThread(path, object : DecodeImgCallback {
                override fun onImageDecodeSuccess(result: Result) {
                    handleDecode(result)
                }

                override fun onImageDecodeFailed() {
                    Toast.makeText(this@CaptureActivity, "抱歉，解析失败,换个图片试试.", Toast.LENGTH_SHORT)
                        .show()
                }
            }).run()
        }
    }

    /*判断设备是否支持闪光灯*/
    fun isSupportCameraLedFlash(pm: PackageManager?): Boolean {
        if (pm != null) {
            val features = pm.systemAvailableFeatures
            for (f in features) {
                if (f != null && PackageManager.FEATURE_CAMERA_FLASH == f.name) return true
            }
        }
        return false
    }
}