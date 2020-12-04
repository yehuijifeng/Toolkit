package com.wwxd.compass

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.wwxd.utils.StringUtil
import kotlinx.android.synthetic.main.fragment_compass.*
import java.util.*

/**
 * 指南针管理
 */
class CompassManager(context: Context) {
    private val mSensorManager: SensorManager//传感器管理
    private val accelerometer: Sensor //加速度传感器
    private val magnetic: Sensor // 地磁场传感器
    private val mLocationManager: LocationManager//位置管理
    private var mLocationProvider: String = "GPS" // 位置提供者名称，GPS设备还是网络
    private var accelerometerValues = FloatArray(3)
    private var magneticFieldValues = FloatArray(3)
    private var mRotation = 0f
    private val mSensorEventListener = CompassSensorEventListener()
    private val mOrientaionText = arrayOf("北", "东北", "东", "东南", "南", "西南", "西", "西北")
    var mCompassLister: CompassLister? = null

    internal inner class CompassSensorEventListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values
            }
            calculateOrientation()
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    fun setRotation(rotation: Float) {
        mRotation = rotation % 360
    }

    private fun calculateOrientation() {
        val values = FloatArray(3)
        val R = FloatArray(9)
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues)
        SensorManager.getOrientation(R, values)
        values[0] = Math.toDegrees(values[0].toDouble()).toFloat()
        var orientation = values[0]
        if (orientation < 0) {
            orientation = 360 + orientation
        }
        orientation += mRotation
        if (orientation > 360) {
            orientation -= 360f
        }
        if (mCompassLister != null)
            mCompassLister!!.onOrientationChange(orientation,
                updateLocation(),
                mOrientaionText[(orientation + 22.5f).toInt() % 360 / 45])
    }

    /**
     * 更新位置信息
     */
    @SuppressLint("MissingPermission")
    private fun updateLocation(): String {
        val location = mLocationManager.getLastKnownLocation(mLocationProvider)
        return updateLocation(location)
    }

    private fun updateLocation(location: Location?): String {
        val sb = StringBuilder()
        if (null == location) {
            sb.append(StringUtil.getString(R.string.str_cannot_get_location))
        } else {
            val longitude: Double = location.getLongitude()
            val latitude: Double = location.getLatitude()
            if (latitude >= 0.0f) {
                sb.append(
                    String.format(
                        StringUtil.getString(R.string.str_location_north),
                        latitude.toString()
                    )
                )
            } else {
                sb.append(
                    String.format(
                        StringUtil.getString(R.string.str_location_south),
                        (-1.0 * latitude).toString()
                    )
                )
            }
            sb.append("    ")
            if (longitude >= 0.0f) {
                sb.append(
                    String.format(
                        StringUtil.getString(R.string.str_location_east),
                        longitude.toString()
                    )
                )
            } else {
                sb.append(
                    String.format(
                        StringUtil.getString(R.string.str_location_west),
                        (-1.0 * longitude).toString()
                    )
                )
            }
        }
        return String.format(StringUtil.getString(R.string.str_correct_coord), sb.toString())
    }

    fun unbind() {
        mSensorManager.unregisterListener(mSensorEventListener)
    }

    val mLocationListener: LocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location?) {
            updateLocation(location)
        }

        @SuppressLint("MissingPermission")
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            if (status != Criteria.NO_REQUIREMENT) {
                updateLocation()
            }
        }

        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    init {
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 初始化加速度传感器
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        // 初始化地磁场传感器
        magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        // 初始化位置管理器
        mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria() // 条件对象，即指定条件过滤获得LocationProvider
        criteria.accuracy = Criteria.ACCURACY_FINE // 较高精度
        criteria.isAltitudeRequired = true // 是否需要高度信息
        criteria.isBearingRequired = true // 是否需要方向信息
        criteria.isCostAllowed = true // 是否产生费用
        criteria.powerRequirement = Criteria.POWER_LOW // 设置低电耗
        // 获取条件最好的Provider,若没有权限，mLocationProvider 为null
        mLocationProvider = mLocationManager.getBestProvider(criteria, true)
            ?: context.getString(R.string.str_cannot_get_location)

        //注册监听
        mSensorManager.registerListener(
            mSensorEventListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        mSensorManager.registerListener(
            mSensorEventListener,
            magnetic,
            SensorManager.SENSOR_DELAY_UI
        )
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationManager.requestLocationUpdates(
                mLocationProvider, 3000L, 10f, mLocationListener
            )
        }
    }
}