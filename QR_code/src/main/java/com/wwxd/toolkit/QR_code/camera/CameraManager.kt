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

package com.wwxd.toolkit.QR_code.camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.google.zxing.PlanarYUVLuminanceSource;
import com.wwxd.toolkit.QR_code.android.CaptureActivityHandler;
import com.wwxd.toolkit.QR_code.common.Constant;
import com.wwxd.toolkit.base.AppConstant;

import java.io.IOException;

/**
 * 该对象封装了相机服务对象,预计将是唯一一个和它说话。
 * 实现封装的步骤需要采取preview-sized图像,用于预览和解码。;
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CameraManager {

    private CameraConfigurationManager configManager;
    private Camera camera;
    private AutoFocusManager autoFocusManager;
    private Rect framingRect;
    private Rect framingRectInPreview;
    private boolean initialized;
    private boolean previewing;
    private int requestedCameraId = -1;
    private int requestedFramingRectWidth;
    private int requestedFramingRectHeight;
    private final PreviewCallback previewCallback;

    public CameraManager() {
        this.configManager = new CameraConfigurationManager(AppConstant.INSTANCE.getApp());
        previewCallback = new PreviewCallback(configManager);
    }

    /**
     * 打开摄像头驱动程序和硬件初始化参数
     *
     * @param holder 物体表面的相机将预览帧
     */
    public synchronized void openDriver(SurfaceHolder holder) {
        Camera theCamera = camera;
        if (theCamera == null) {
            if (requestedCameraId >= 0) {
                theCamera = OpenCameraInterface.open(requestedCameraId);
            } else {
                theCamera = OpenCameraInterface.open();
            }
            if (theCamera != null) {
                camera = theCamera;
            }
        }
        if (theCamera != null) {
            try {
                theCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!initialized) {
                initialized = true;
                configManager.initFromCameraParameters(theCamera);
                if (requestedFramingRectWidth > 0 && requestedFramingRectHeight > 0) {
                    setManualFramingRect(requestedFramingRectWidth,
                            requestedFramingRectHeight);
                    requestedFramingRectWidth = 0;
                    requestedFramingRectHeight = 0;
                }
            }
            Camera.Parameters parameters = theCamera.getParameters();
            // 保存这些,暂时
            String parametersFlattened = parameters == null ? null : parameters.flatten();
            try {
                configManager.setDesiredCameraParameters(theCamera);
            } catch (RuntimeException re) {
                if (parametersFlattened != null) {
                    parameters = theCamera.getParameters();
                    parameters.unflatten(parametersFlattened);
                    try {
                        theCamera.setParameters(parameters);
                        configManager.setDesiredCameraParameters(theCamera);
                    } catch (RuntimeException re2) {
                        re2.printStackTrace();
                    }
                }
            }
        }
    }

    public synchronized boolean isOpen() {
        return camera != null;
    }

    /**
     * 关闭摄像头驱动程序是否仍在使用
     */
    public synchronized void closeDriver() {
        if (camera != null) {
            camera.release();
            camera = null;
            framingRect = null;
            framingRectInPreview = null;
        }
    }


    /*切换闪光灯*/
    public void switchFlashLight(CaptureActivityHandler handler) {
        Camera.Parameters parameters = camera.getParameters();
        Message msg = new Message();
        String flashMode = parameters.getFlashMode();
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            /*关闭闪光灯*/
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            msg.what = Constant.FLASH_CLOSE;
        } else {
            /*打开闪光灯*/
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            msg.what = Constant.FLASH_OPEN;
        }
        camera.setParameters(parameters);
        handler.sendMessage(msg);
    }


    /**
     * 问相机硬件开始画预览帧到屏幕上。
     */
    public synchronized void startPreview() {
        Camera theCamera = camera;
        if (theCamera != null && !previewing) {
            theCamera.startPreview();
            previewing = true;
            autoFocusManager = new AutoFocusManager(camera);
        }
    }

    /**
     * 告诉相机停止预览帧。
     */
    public synchronized void stopPreview() {
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
        if (camera != null && previewing) {
            camera.stopPreview();
            previewCallback.setHandler(null, 0);
            previewing = false;
        }
    }

    /**
     * 一个预览帧将被返回给处理程序提供
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public synchronized void requestPreviewFrame(Handler handler, int message) {
        Camera theCamera = camera;
        if (theCamera != null && previewing) {
            previewCallback.setHandler(handler, message);
            theCamera.setOneShotPreviewCallback(previewCallback);
        }
    }

    /*取景框*/
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                return null;
            }
            int screenResolutionX = screenResolution.x;
            int width = (int) (screenResolutionX * 0.6);
            int height = width;
            /*水平居中  偏上显示*/
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 5;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
        return framingRect;
    }


    /**
     * 但坐标的预览帧,不是UI /屏幕。
     *
     * @return {@link Rect} 表达条形码扫描区域的预览
     */
    public synchronized Rect getFramingRectInPreview() {
        if (framingRectInPreview == null) {
            Rect framingRect = getFramingRect();
            if (framingRect == null) {
                return null;
            }
            Rect rect = new Rect(framingRect);
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();
            if (cameraResolution == null || screenResolution == null) {
                // Called early, before init even finished
                return null;
            }
            //竖屏更改1(cameraResolution.x/y互换)
            rect.left = rect.left * cameraResolution.y / screenResolution.x;
            rect.right = rect.right * cameraResolution.y / screenResolution.x;
            rect.top = rect.top * cameraResolution.x / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.x / screenResolution.y;
            framingRectInPreview = rect;
        }
        return framingRectInPreview;
    }

    /**
     * 允许第三方应用程序指定相机ID,而不是确定自动基于可用摄像机及其取向
     *
     * @param cameraId 相机相机使用的ID。一个负值意味着“没有偏好
     */
    public synchronized void setManualCameraId(int cameraId) {
        requestedCameraId = cameraId;
    }

    /**
     * Allows third party apps to specify the scanning rectangle dimensions,
     * rather than determine them automatically based on screen resolution.
     *
     * @param width  The width in pixels to scan.
     * @param height The height in pixels to scan.
     */
    public synchronized void setManualFramingRect(int width, int height) {
        if (initialized) {
            Point screenResolution = configManager.getScreenResolution();
            if (width > screenResolution.x) {
                width = screenResolution.x;
            }
            if (height > screenResolution.y) {
                height = screenResolution.y;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
                    topOffset + height);
            framingRectInPreview = null;
        } else {
            requestedFramingRectWidth = width;
            requestedFramingRectHeight = height;
        }
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on
     * the format of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data,
                                                         int width, int height) {
        Rect rect = getFramingRectInPreview();
        if (rect == null) {
            return null;
        }
        // Go ahead and assume it's YUV rather than die.
//        return new PlanarYUVLuminanceSource(data, width, height, rect.left,
//                rect.top, rect.width(), rect.height(), false);
        return new PlanarYUVLuminanceSource(data, width, height, 0,
                0, width, height, false);
    }

}
