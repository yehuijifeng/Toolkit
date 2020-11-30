package com.wwxd.utils.photo

/**
 * user：LuHao
 * time：2019/12/23 10:09
 * describe：
 */
interface IGestureListener {
    fun onDrag(dx: Float, dy: Float)
    fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float)
    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float)
}