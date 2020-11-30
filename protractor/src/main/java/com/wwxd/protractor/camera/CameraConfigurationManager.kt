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

import android.annotation.SuppressLint
import android.graphics.Point
import android.hardware.Camera
import android.os.Build
import com.wwxd.utils.DisplayUtil.getWindowHeight
import com.wwxd.utils.DisplayUtil.getWindowWidth
import java.lang.Boolean
import java.util.regex.Pattern

class CameraConfigurationManager {
    var screenResolution: Point? = null
        private set
    var cameraResolution: Point? = null
        private set

    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    fun initFromCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        screenResolution = Point(getWindowWidth(), getWindowHeight())
        val screenResolutionForCamera = Point()
        screenResolutionForCamera.x = screenResolution!!.x
        screenResolutionForCamera.y = screenResolution!!.y

        // preview size is always something like 480*320, other 320*480
        if (screenResolution!!.x < screenResolution!!.y) {
            screenResolutionForCamera.x = screenResolution!!.y
            screenResolutionForCamera.y = screenResolution!!.x
        }
        // custem crecet.
        // screenResolution = new Point(display.getWidth(),
        // display.getHeight());
        cameraResolution = getCameraResolution(
            parameters,
            screenResolutionForCamera
        )
    }

    /**
     * Sets the camera up to take preview images which are used for both preview
     * and decoding. We detect the preview format here so that
     * buildLuminanceSource() can build an appropriate LuminanceSource subclass.
     * In the future we may want to force YUV420SP as it's the smallest, and the
     * planar Y can be used for barcode scanning without a copy in some cases.
     */
    @SuppressLint("NewApi")
    fun setDesiredCameraParameters(camera: Camera) {
        val parameters = camera.parameters
        parameters.setPreviewSize(cameraResolution!!.x, cameraResolution!!.y)
        setFlash(parameters)
        setZoom(parameters)
        camera.setDisplayOrientation(90)
        camera.parameters = parameters
    }

    private fun setFlash(parameters: Camera.Parameters) {
        //
        // Restrict Behold II check to Cupcake, per Samsung's advice
        // if (Build.MODEL.contains("Behold II") &&
        // CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
        if (Build.MODEL.contains("Behold II")) { // 3
            parameters["flash-value"] = 1
        } else {
            parameters["flash-value"] = 2
        }
        // This is the standard setting to turn the flash off that all devices
        // should honor.
        parameters["flash-mode"] = "off"
    }

    private fun setZoom(parameters: Camera.Parameters) {
        val zoomSupportedString = parameters["zoom-supported"]
        if (zoomSupportedString != null
            && !Boolean.parseBoolean(zoomSupportedString)
        ) {
            return
        }
        var tenDesiredZoom = 27
        val maxZoomString = parameters["max-zoom"]
        if (maxZoomString != null) {
            try {
                val tenMaxZoom = (10.0 * maxZoomString.toDouble()).toInt()
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }
        }
        val takingPictureZoomMaxString = parameters["taking-picture-zoom-max"]
        if (takingPictureZoomMaxString != null) {
            try {
                val tenMaxZoom = takingPictureZoomMaxString.toInt()
                if (tenDesiredZoom > tenMaxZoom) {
                    tenDesiredZoom = tenMaxZoom
                }
            } catch (nfe: NumberFormatException) {
                nfe.printStackTrace()
            }
        }
        val motZoomValuesString = parameters["mot-zoom-values"]
        if (motZoomValuesString != null) {
            tenDesiredZoom = findBestMotZoomValue(
                motZoomValuesString,
                tenDesiredZoom
            )
        }
        val motZoomStepString = parameters["mot-zoom-step"]
        if (motZoomStepString != null) {
            try {
                val motZoomStep = motZoomStepString
                    .trim { it <= ' ' }.toDouble()
                val tenZoomStep = (10.0 * motZoomStep).toInt()
                if (tenZoomStep > 1) {
                    tenDesiredZoom -= tenDesiredZoom % tenZoomStep
                }
            } catch (nfe: NumberFormatException) {
                // continue
            }
        }

        // Set zoom. This helps encourage the user to pull back.
        // Some devices like the Behold have a zoom parameter
        if (maxZoomString != null || motZoomValuesString != null) {
            parameters["zoom"] = (tenDesiredZoom / 10.0).toString()
        }

        // Most devices, like the Hero, appear to expose this zoom parameter.
        // It takes on values like "27" which appears to mean 2.7x zoom
        if (takingPictureZoomMaxString != null) {
            parameters["taking-picture-zoom"] = tenDesiredZoom
        }
    }

    companion object {
        private val COMMA_PATTERN = Pattern.compile(",")
        private fun getCameraResolution(
            parameters: Camera.Parameters,
            screenResolution: Point
        ): Point {
            var previewSizeValueString = parameters["preview-size-values"]
            // saw this on Xperia
            if (previewSizeValueString == null) {
                previewSizeValueString = parameters["preview-size-value"]
            }
            var cameraResolution: Point? = null
            if (previewSizeValueString != null) {
                cameraResolution = findBestPreviewSizeValue(
                    previewSizeValueString,
                    screenResolution
                )
            }
            if (cameraResolution == null) {
                // Ensure that the camera resolution is a multiple of 8, as the
                // screen may not be.
                cameraResolution = Point(
                    screenResolution.x shr 3 shl 3,
                    screenResolution.y shr 3 shl 3
                )
            }
            return cameraResolution
        }

        private fun findBestPreviewSizeValue(
            previewSizeValueString: CharSequence, screenResolution: Point
        ): Point? {
            var bestX = 0
            var bestY = 0
            var diff = Int.MAX_VALUE
            for (previewSize in COMMA_PATTERN.split(previewSizeValueString)) {
                var previewSize1=previewSize
                previewSize1 = previewSize1.trim { it <= ' ' }
                val dimPosition = previewSize1.indexOf('x')
                if (dimPosition < 0) {
                    continue
                }
                var newX: Int
                var newY: Int
                try {
                    newX = previewSize1.substring(0, dimPosition).toInt()
                    newY = previewSize1.substring(dimPosition + 1).toInt()
                } catch (nfe: NumberFormatException) {
                    continue
                }
                val newDiff = (Math.abs(newX - screenResolution.x)
                        + Math.abs(newY - screenResolution.y))
                if (newDiff == 0) {
                    bestX = newX
                    bestY = newY
                    break
                } else if (newDiff < diff) {
                    bestX = newX
                    bestY = newY
                    diff = newDiff
                }
            }
            return if (bestX > 0 && bestY > 0) {
                Point(bestX, bestY)
            } else null
        }

        private fun findBestMotZoomValue(
            stringValues: CharSequence,
            tenDesiredZoom: Int
        ): Int {
            var tenBestValue = 0
            for (stringValue in COMMA_PATTERN.split(stringValues)) {
                var stringValue1=stringValue
                stringValue1 = stringValue1.trim { it <= ' ' }
                var value: Double
                value = try {
                    stringValue1.toDouble()
                } catch (nfe: NumberFormatException) {
                    return tenDesiredZoom
                }
                val tenValue = (10.0 * value).toInt()
                if (Math.abs(tenDesiredZoom - value) < Math.abs(
                        tenDesiredZoom
                                - tenBestValue
                    )
                ) {
                    tenBestValue = tenValue
                }
            }
            return tenBestValue
        }
    }
}