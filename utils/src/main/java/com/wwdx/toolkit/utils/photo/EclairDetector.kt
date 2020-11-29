package com.wwdx.toolkit.utils.photo

import android.content.Context
import android.view.MotionEvent

/**
 * user：LuHao
 * time：2019/12/23 10:08
 * describe：
 */
open class EclairDetector(context: Context, iGestureListener: IGestureListener) :
    CupcakeDetector(context, iGestureListener) {
    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    override fun getActiveX(ev: MotionEvent): Float {
        return try {
            ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            ev.x
        }
    }

    override fun getActiveY(ev: MotionEvent): Float {
        return try {
            ev.getY(mActivePointerIndex)
        } catch (e: Exception) {
            ev.y
        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val action = ev!!.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mActivePointerId = ev.getPointerId(0)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> mActivePointerId =
                INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex =
                    ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                    setmLastTouchX(ev.getX(newPointerIndex))
                    setmLastTouchY(ev.getY(newPointerIndex))
                }
            }
        }
        mActivePointerIndex =
            ev.findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID) mActivePointerId else 0)
        return super.onTouchEvent(ev)
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}