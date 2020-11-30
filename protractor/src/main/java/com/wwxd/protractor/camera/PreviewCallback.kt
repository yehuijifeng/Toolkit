/*
 * Copyright (C) 2010 ZXing authors
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

class PreviewCallback(val configManager: CameraConfigurationManager) :
    Camera.PreviewCallback {
    private var previewHandler: Handler? = null
    private var previewMessage = 0
    fun setHandler(previewHandler: Handler?, previewMessage: Int) {
        this.previewHandler = previewHandler
        this.previewMessage = previewMessage
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val cameraResolution = configManager.cameraResolution
        if (previewHandler != null) {
            val message = previewHandler!!.obtainMessage(
                previewMessage, cameraResolution!!.x,
                cameraResolution.y, data
            )
            message.sendToTarget()
            previewHandler = null
        }
    }
}