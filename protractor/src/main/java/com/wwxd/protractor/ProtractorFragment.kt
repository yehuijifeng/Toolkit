package com.wwxd.protractor

import android.hardware.Camera
import android.view.SurfaceHolder
import android.view.View
import com.wwxd.base.BaseFragment
import com.wwxd.protractor.camera.CameraManager
import kotlinx.android.synthetic.main.fragment_protractor.*


/**
 * user：LuHao
 * time：2020/11/30 11:55
 * describe：量角器
 */
class ProtractorFragment : BaseFragment(), SurfaceHolder.Callback {
    private var hasSurface = false
    private var cameraManager: CameraManager? = null

    override fun getContentView(): Int {
        return R.layout.fragment_protractor
    }

    override fun init(view: View) {
        cameraManager = CameraManager()
        hasSurface = false
        sCamera.isChecked=true
        sCamera.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                surface.setVisibility(View.VISIBLE)
                startPreview()
            } else {
                surface.setVisibility(View.INVISIBLE)
                stopPreview()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startPreview()
    }

    override fun onPause() {
        super.onPause()
        stopPreview()
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder?) {
        if (cameraManager != null && surfaceHolder != null)
            cameraManager!!.openDriver(surfaceHolder)
    }

    private fun startPreview() {
        val surfaceHolder: SurfaceHolder = surface.getHolder()
        if (hasSurface) {
            initCamera(surfaceHolder)
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    private fun stopPreview() {
        if (cameraManager != null) {
            cameraManager!!.stopPreview()
            cameraManager!!.closeDriver()
        }
    }

    override fun surfaceChanged(
        holder: SurfaceHolder?, format: Int, width: Int,
        height: Int
    ) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        hasSurface = false
    }

    override fun onDestroy() {
        if (cameraManager != null)
            cameraManager!!.stopPreview()
        super.onDestroy()
    }

}