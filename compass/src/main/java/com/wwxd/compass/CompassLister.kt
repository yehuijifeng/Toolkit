package com.wwxd.compass

/**
 * user：LuHao
 * time：2020/12/3 15:36
 * describe：方向回调
 */
interface CompassLister {
    fun onOrientationChange(orientation: Float, location: String, directionStr: String)
}