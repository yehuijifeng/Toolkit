/*
 * Copyright (C) 2012 ZXing authors
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
package com.wwxd.QR_code.camera

import android.annotation.SuppressLint
import android.hardware.Camera
import android.hardware.Camera.CameraInfo

//打开相机接口
object OpenCameraInterface {
    /**
     * Opens the requested camera with [Camera.open], if one exists.
     *
     * @param cameraId camera ID of the camera to use. A negative value means "no preference"
     * @return handle to [Camera] that was opened
     */
    @SuppressLint("NewApi")
    fun open(cameraId: Int): Camera? {
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {
            return null
        }
        val explicitRequest = cameraId >= 0
        var cameraId1 = cameraId
        if (!explicitRequest) {
            // Select a camera if no explicit camera requested
            var index = 0
            while (index < numCameras) {
                val cameraInfo = CameraInfo()
                Camera.getCameraInfo(index, cameraInfo)
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    break
                }
                index++
            }
            cameraId1 = index
        }
        val camera: Camera?
        camera = if (cameraId1 < numCameras) {
            Camera.open(cameraId1)
        } else {
            if (explicitRequest) {
                null
            } else {
                Camera.open(0)
            }
        }
        return camera
    }

    /**
     * Opens a rear-facing camera with [Camera.open], if one exists, or opens camera 0.
     *
     * @return handle to [Camera] that was opened
     */
    @JvmStatic
    fun open(): Camera? {
        return open(-1)
    }
}