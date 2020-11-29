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
package com.wwxd.toolkit.QR_code.camera

import android.annotation.SuppressLint
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.os.AsyncTask
import java.util.concurrent.RejectedExecutionException

/*
 * 聚焦管理
 * */
internal class AutoFocusManager(private val camera: Camera) : AutoFocusCallback {
    private var stopped = false
    private var focusing = false
    private val useAutoFocus = true
    private var outstandingTask: AsyncTask<*, *, *>? = null

    init {
        start()
    }

    @Synchronized
    override fun onAutoFocus(success: Boolean, theCamera: Camera) {
        focusing = false
        autoFocusAgainLater()
    }

    @SuppressLint("NewApi")
    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && outstandingTask == null) {
            val newTask = AutoFocusTask()
            try {
                newTask.execute()
                outstandingTask = newTask
            } catch (ree: RejectedExecutionException) {
                ree.printStackTrace()
            }
        }
    }

    @Synchronized
    private fun start() {
        if (useAutoFocus) {
            outstandingTask = null
            if (!stopped && !focusing) {
                try {
                    camera.autoFocus(this)
                    focusing = true
                } catch (re: RuntimeException) {
                    // Have heard RuntimeException reported in Android 4.0.x+; continue?
                    // Try again later to keep cycle going
                    autoFocusAgainLater()
                }
            }
        }
    }

    @Synchronized
    private fun cancelOutstandingTask() {
        if (outstandingTask != null) {
            if (outstandingTask!!.status != AsyncTask.Status.FINISHED) {
                outstandingTask!!.cancel(true)
            }
            outstandingTask = null
        }
    }

    @Synchronized
    fun stop() {
        stopped = true
        if (useAutoFocus) {
            cancelOutstandingTask()
            // Doesn't hurt to call this even if not focusing
            try {
                camera.cancelAutoFocus()
            } catch (re: RuntimeException) {
                // Have heard RuntimeException reported in Android 4.0.x+; continue?
                re.printStackTrace()
            }
        }
    }

    private inner class AutoFocusTask : AsyncTask<Any, Any, Any>() {
        override fun doInBackground(vararg voids: Any): Any? {
            try {
                /*聚焦间隔*/
                Thread.sleep(1000L)
            } catch (e: InterruptedException) {
                // continue
                e.printStackTrace()
            }
            start()
            return null
        }
    }


}