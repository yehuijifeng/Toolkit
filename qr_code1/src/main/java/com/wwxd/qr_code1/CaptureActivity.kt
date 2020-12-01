package com.wwxd.qr_code1

import android.content.pm.PackageManager
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.google.zxing.ResultPoint
import com.google.zxing.ResultPointCallback
import com.wwxd.qr_code1.camera.CameraManager
import com.wwxd.qr_code1.decode.DecodeThread
import com.wwxd.base.BaseActivity
import com.wwxd.base.IDefaultDialogClickListener
import kotlinx.android.synthetic.main.activity_capture.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * user：LuHao
 * time：2020/11/27 16:16
 * describe：二维码扫描
 */
class CaptureActivity : BaseActivity() {
    private var surfaceHolder: SurfaceHolder? = null//透明画布的holder
    private var hasSurface = false//是否初始化完成透明画布
    private var cameraManager: CameraManager? = null//相机管理器
    private var beepManager: BeepManager? = null//声音震动管理器
    private var onCallBack: OnCallBack? = null//透明画布回调
    private var decodeThread: DecodeThread? = null//解码线程
    private var state: State = State.DEF//当前解码状态
    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun isFullWindow(): Boolean {
        return true
    }

    private enum class State {
        DEF,//默认的
        PREVIEW,//预览
        SUCCESS,//成功
        DONE//完成
    }

    override fun getContentView(): Int {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        return R.layout.activity_capture
    }

    override fun init() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        onCallBack = OnCallBack()
        imgFlashLight.setOnClickListener {
            if (cameraManager != null)
                imgFlashLight.isSelected = cameraManager!!.switchFlashLight()
        }
        //有闪光灯就显示手电筒按钮  否则不显示
        imgFlashLight.isSelected =
            if (isSupportCameraLedFlash(packageManager)) {
                false
            } else {
                imgFlashLight.visibility = View.GONE
                false
            }
        hasSurface = false//没有取景框
        beepManager = BeepManager(this)//声音和震动
    }

    override fun onResume() {
        super.onResume()
        cameraManager = CameraManager()//初始化相机
        viewfinderView?.setCameraManager(cameraManager)
        surfaceHolder = surfaceView.holder
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder?.addCallback(onCallBack)
        }
    }

    /**
     * 初始化Camera
     */
    private fun initCamera(surfaceHolder1: SurfaceHolder?) {
        try {
            if (surfaceHolder1 == null || cameraManager == null || cameraManager!!.isOpen)
                return
            // 打开Camera硬件设备
            cameraManager?.openDriver(surfaceHolder1)
            //创建一个解码线程来打开预览
            if (decodeThread == null) {
                decodeThread = DecodeThread(ViewfinderResultPointCallback())
                decodeThread!!.start()
                cameraManager?.startPreview()//开始预览
                state = State.PREVIEW
                cameraManager?.requestPreviewFrame(decodeThread!!)
                viewfinderView.drawViewfinder()//画取景框
            }
        } catch (e: Exception) {
            getDefaultDialog().getBuilder()
                .isBackDismiss(false)
                .isNoCancle(true)
                .isShowTiltle(false)
                .setContent(getString(R.string.str_sao_error))
                .setOkText(getString(R.string.str_retry))
                .setOkClick(object : IDefaultDialogClickListener {
                    override fun onClick(v: View) {
                        initCamera(surfaceHolder)
                    }
                })
                .show()
        }
    }

    /**
     * 完全退出
     */
    private fun quitSynchronously() {
        try {
            state = State.DONE
            cameraManager?.stopPreview()
            //等半秒
            decodeThread = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendDecode(decodeEvent: DecodeEvent) {
        // 尽可能快的解码，以便可以在解码失败时，开始另一次解码
        intent.putExtra(Constant.CODED_CONTENT, decodeEvent.result)
        setResult(RESULT_OK, intent)
        finish()

    }

    /**
     * 取景器结果点回调
     */
    private inner class ViewfinderResultPointCallback : ResultPointCallback {
        override fun foundPossibleResultPoint(point: ResultPoint) {
            viewfinderView.addPossibleResultPoint(point)
        }
    }

    override fun onPause() {
        quitSynchronously()
        beepManager?.close()
        cameraManager?.closeDriver()
        if (!hasSurface) {
            surfaceHolder?.removeCallback(onCallBack)
        }
        super.onPause()
    }

    //透明画布回调后初始化相机
    private inner class OnCallBack : SurfaceHolder.Callback {
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
    }


    //判断设备是否支持闪光灯
    private fun isSupportCameraLedFlash(pm: PackageManager?): Boolean {
        if (pm != null) {
            val features = pm.systemAvailableFeatures
            for (f in features) {
                if (f != null && PackageManager.FEATURE_CAMERA_FLASH == f.name)
                    return true
            }
        }
        return false
    }
}