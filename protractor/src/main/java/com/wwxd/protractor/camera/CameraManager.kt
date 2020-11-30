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
package com.wwxd.protractor.camera

import android.hardware.Camera
import android.os.Handler
import android.view.SurfaceHolder
import java.io.IOException

/**
 * This object wraps the Camera service object and expects to be the only one
 * talking to it. The implementation encapsulates the steps needed to take
 * preview-sized images, which are used for both preview and decoding.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
class CameraManager {
    private val configManager: CameraConfigurationManager
    private var camera: Camera? = null
    private var initialized = false
    private var previewing = false
    private var flashlightManager: FlashlightManager

    /**
     * Preview frames are delivered here, which we pass on to the registered
     * handler. Make sure to clear the handler so it will only receive one
     * message.
     */
    private val previewCallback: PreviewCallback

    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which
     * requested them.
     */
    private val autoFocusCallback: AutoFocusCallback

    /**
     * Opens the camera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the camera will draw preview frames
     * into.
     * @throws IOException Indicates the camera driver failed to open.
     */
    @Throws(IOException::class)
    fun openDriver(holder: SurfaceHolder?) {
        if (camera == null) {
            camera = Camera.open()
            if (camera == null) {
                throw IOException()
            }
            camera!!.setPreviewDisplay(holder)
            if (!initialized) {
                initialized = true
                configManager.initFromCameraParameters(camera!!)
            }
            configManager.setDesiredCameraParameters(camera!!)
            flashlightManager.enableFlashlight()
            startPreview()
        }
    }

    /**
     * Closes the camera driver if still in use.
     */
    fun closeDriver() {
        if (camera != null) {
            flashlightManager.disableFlashlight()
            camera!!.release()
            camera = null
        }
    }

    /**
     * Asks the camera hardware to begin drawing preview frames to the screen.
     */
    fun startPreview() {
        if (camera != null && !previewing) {
            camera!!.startPreview()
            previewing = true
        }
    }

    /**
     * Tells the camera to stop drawing preview frames.
     */
    fun stopPreview() {
        if (camera != null && previewing) {
            camera!!.stopPreview()
            previewCallback.setHandler(null, 0)
            autoFocusCallback.setHandler(null, 0)
            previewing = false
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data
     * will arrive as byte[] in the message.obj field, with width and height
     * encoded as message.arg1 and message.arg2, respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    fun requestPreviewFrame(handler: Handler?, message: Int) {
        if (camera != null && previewing) {
            previewCallback.setHandler(handler, message)
            camera!!.setOneShotPreviewCallback(previewCallback)
        }
    }

    /**
     * Asks the camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    fun requestAutoFocus(handler: Handler?, message: Int) {
        if (camera != null && previewing) {
            autoFocusCallback.setHandler(handler, message)
            // Log.d(TAG, "Requesting auto-focus callback");
            camera!!.autoFocus(autoFocusCallback)
        }
    }

    init {
        flashlightManager = FlashlightManager()
        configManager = CameraConfigurationManager()
        previewCallback = PreviewCallback(configManager)
        autoFocusCallback = AutoFocusCallback()
    }
}