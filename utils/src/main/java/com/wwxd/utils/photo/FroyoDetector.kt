package com.wwxd.utils.photo

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector

/**
 * user：LuHao
 * time：2019/12/23 10:08
 * describe：
 */
class FroyoDetector(context: Context, iGestureListener: IGestureListener) :
    EclairDetector(context, iGestureListener) {
    private val mDetector: ScaleGestureDetector

    // Needs to be an inner class so that we don't hit
    // VerifyError's on API 4.
    private inner class OnScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            getmListener().onScale(detector.scaleFactor, detector.focusX, detector.focusY)
            return true
        }

        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            // NO-OP
        }
    }

    override val isScaling: Boolean
        get() = mDetector.isInProgress


    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        mDetector.onTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    init {
        mDetector = ScaleGestureDetector(context, OnScaleGestureListener())
    }
}