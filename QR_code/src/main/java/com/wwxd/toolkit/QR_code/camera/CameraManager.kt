/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wwxd.toolkit.QR_code.camera

import android.graphics.Rect
import android.hardware.Camera
import android.view.SurfaceHolder
import com.google.zxing.BinaryBitmap
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.wwxd.toolkit.QR_code.Constant
import com.wwxd.toolkit.QR_code.DecodeEvent
import com.wwxd.toolkit.QR_code.camera.OpenCameraInterface.open
import com.wwxd.toolkit.QR_code.decode.DecodeThread
import org.greenrobot.eventbus.EventBus
import java.io.IOException


/**
 * 该对象封装了相机服务对象,预计将是唯一一个和它说话。
 * 实现封装的步骤需要采取preview-sized图像,用于预览和解码。;
 */
class CameraManager {
    private val configManager: CameraConfigurationManager
    private var camera: Camera? = null
    private var autoFocusManager: AutoFocusManager? = null
    private var framingRect: Rect? = null
    private var framingRectInPreview: Rect? = null
    private var initialized = false
    private var previewing = false
    private var requestedCameraId = -1
    private var requestedFramingRectWidth = 0
    private var requestedFramingRectHeight = 0
    private val previewCallback: PreviewCallback

    /**
     * 打开摄像头驱动程序和硬件初始化参数
     *
     * @param holder 物体表面的相机将预览帧
     */
    @Synchronized
    fun openDriver(holder: SurfaceHolder?) {
        var theCamera = camera
        if (theCamera == null) {
            theCamera = if (requestedCameraId >= 0) {
                open(requestedCameraId)
            } else {
                open()
            }
            if (theCamera != null) {
                camera = theCamera
            }
        }
        if (theCamera != null) {
            try {
                theCamera.setPreviewDisplay(holder)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (!initialized) {
                initialized = true
                configManager.initFromCameraParameters(theCamera)
                if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
                    setManualFramingRect(
                        requestedFramingRectWidth,
                        requestedFramingRectHeight
                    )
                    requestedFramingRectWidth = 0
                    requestedFramingRectHeight = 0
                }
            }
            var parameters = theCamera.parameters
            // 保存这些,暂时
            val parametersFlattened = parameters?.flatten()
            try {
                configManager.setDesiredCameraParameters(theCamera)
            } catch (re: RuntimeException) {
                if (parametersFlattened != null) {
                    parameters = theCamera.parameters
                    parameters.unflatten(parametersFlattened)
                    try {
                        theCamera.parameters = parameters
                        configManager.setDesiredCameraParameters(theCamera)
                    } catch (re2: RuntimeException) {
                        re2.printStackTrace()
                    }
                }
            }
        }
    }

    @get:Synchronized
    val isOpen: Boolean
        get() = camera != null

    /**
     * 关闭摄像头驱动程序是否仍在使用
     */
    @Synchronized
    fun closeDriver() {
        if (camera != null) {
            camera!!.release()
            camera = null
            framingRect = null
            framingRectInPreview = null
        }
    }

    /*切换闪光灯*/
    fun switchFlashLight(): Boolean {
        val parameters = camera!!.parameters
        val isOpen: Boolean
        val flashMode = parameters.flashMode
        if (flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
            /*关闭闪光灯*/
            parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
            isOpen = false
        } else {
            /*打开闪光灯*/
            parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            isOpen = true
        }
        camera!!.parameters = parameters
        return isOpen
    }

    /**
     * 问相机硬件开始画预览帧到屏幕上。
     */
    @Synchronized
    fun startPreview() {
        val theCamera = camera
        if (theCamera != null && !previewing) {
            theCamera.startPreview()
            previewing = true
            autoFocusManager = AutoFocusManager(camera!!)
        }
    }

    /**
     * 告诉相机停止预览帧。
     */
    @Synchronized
    fun stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager!!.stop()
            autoFocusManager = null
        }
        if (camera != null && previewing) {
            camera!!.stopPreview()
            previewCallback.decodeThread = null
            previewing = false
        }
    }

    /**
     * 一个预览帧将被返回给处理程序提供
     */
    @Synchronized
    fun requestPreviewFrame(decodeThread: DecodeThread) {
        val theCamera = camera
        if (theCamera != null && previewing) {
            previewCallback.decodeThread = decodeThread
            theCamera.setOneShotPreviewCallback(previewCallback)
        }
    }

    /*取景框*/
    @Synchronized
    fun getFramingRect(): Rect? {
        if (framingRect == null) {
            if (camera == null) {
                return null
            }
            val screenResolution = configManager.screenResolution ?: return null
            val screenResolutionX = screenResolution.x
            val width = (screenResolutionX * 0.6).toInt()
            /*水平居中  偏上显示*/
            val leftOffset = (screenResolution.x - width) / 2
            val topOffset = (screenResolution.y - width) / 5
            framingRect = Rect(leftOffset, topOffset, leftOffset + width, topOffset + width)
        }
        return framingRect
    }

    /**
     * 但坐标的预览帧,不是UI /屏幕。
     *
     * @return [Rect] 表达条形码扫描区域的预览
     */
    @Synchronized
    fun getFramingRectInPreview(): Rect? {
        if (framingRectInPreview == null) {
            val framingRect = getFramingRect() ?: return null
            val rect = Rect(framingRect)
            val cameraResolution = configManager.cameraResolution
            val screenResolution = configManager.screenResolution
            if (cameraResolution == null || screenResolution == null) {
                // Called early, before init even finished
                return null
            }
            //竖屏更改1(cameraResolution.x/y互换)
            rect.left = rect.left * cameraResolution.y / screenResolution.x
            rect.right = rect.right * cameraResolution.y / screenResolution.x
            rect.top = rect.top * cameraResolution.x / screenResolution.y
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y
            framingRectInPreview = rect
        }
        return framingRectInPreview
    }

    /**
     * 允许第三方应用程序指定相机ID,而不是确定自动基于可用摄像机及其取向
     *
     * @param cameraId 相机相机使用的ID。一个负值意味着“没有偏好
     */
    @Synchronized
    fun setManualCameraId(cameraId: Int) {
        requestedCameraId = cameraId
    }

    /**
     * Allows third party apps to specify the scanning rectangle dimensions,
     * rather than determine them automatically based on screen resolution.
     *
     * @param width  The width in pixels to scan.
     * @param height The height in pixels to scan.
     */
    @Synchronized
    private fun setManualFramingRect(width: Int, height: Int) {
        var width1 = width
        var height1 = height
        if (initialized) {
            val screenResolution = configManager.screenResolution
            if (screenResolution == null) return
            if (width1 > screenResolution.x) {
                width1 = screenResolution.x
            }
            if (height1 > screenResolution.y) {
                height1 = screenResolution.y
            }
            val leftOffset = (screenResolution.x - width1) / 2
            val topOffset = (screenResolution.y - height1) / 2
            framingRect = Rect(
                leftOffset, topOffset, leftOffset + width1,
                topOffset + height1
            )
            framingRectInPreview = null
        } else {
            requestedFramingRectWidth = width1
            requestedFramingRectHeight = height1
        }
    }

    init {
        configManager = CameraConfigurationManager()
        previewCallback = PreviewCallback()
    }

    //预览回调
    private inner class PreviewCallback : Camera.PreviewCallback {
        var decodeThread: DecodeThread? = null
        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            if (decodeThread == null) return
            val cameraResolution = configManager.cameraResolution
            if (cameraResolution == null) return
            var rawResult: Result? = null
            var data1 = data
            val rotatedData = ByteArray(data1.size)
            var width = cameraResolution.x
            var height = cameraResolution.y
            for (y in 0 until height) {
                for (x in 0 until width)
                    rotatedData[x * height + height - y - 1] = data1.get(x + y * width)
            }
            val tmp: Int = width
            width = height
            height = tmp
            data1 = rotatedData
            if (getFramingRectInPreview() == null) return
            val source = PlanarYUVLuminanceSource(
                data1, width, height, 0,
                0, width, height, false
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            try {
                rawResult = decodeThread!!.multiFormatReader.decodeWithState(bitmap)
            } catch (re: ReaderException) {
                //没有找到，重新接吗
                re.printStackTrace()
            } finally {
                decodeThread!!.multiFormatReader.reset()
            }
            if (rawResult == null) {
                if (previewCallback.decodeThread != null)
                    requestPreviewFrame(previewCallback.decodeThread!!)
                else {
                    val decodeEvent = DecodeEvent()
                    decodeEvent.code = Constant.DECODE_FAILED
                    EventBus.getDefault().post(decodeEvent)
                }
            } else {
                val decodeEvent = DecodeEvent()
                decodeEvent.code = Constant.DECODE_SUCCEEDED
                decodeEvent.result = rawResult.text
                EventBus.getDefault().post(decodeEvent)
            }
        }
    }
}