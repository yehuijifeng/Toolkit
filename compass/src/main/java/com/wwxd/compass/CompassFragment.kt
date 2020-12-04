package com.wwxd.compass

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.wwxd.base.BaseFragment
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.utils.PermissionsUtil
import kotlinx.android.synthetic.main.fragment_compass.*


/**
 * user：LuHao
 * time：2020/12/3 13:58
 * describe：指南针
 */
class CompassFragment : BaseFragment() {
    private val addressCode = 111
    private var compassManager: CompassManager? = null

    override fun getContentView(): Int {
        return R.layout.fragment_compass
    }

    override fun init(view: View) {
        if (PermissionsUtil.lacksPermission(PermissionsUtil.getAddressPermissions())) {
            PermissionsUtil.requestPermissions(
                this,
                PermissionsUtil.getAddressPermissions(),
                addressCode
            )
        } else {
            initCompassManager()
        }
    }

    private fun initCompassManager() {
        compassManager = CompassManager(context!!)
        compassManager!!.mCompassLister = object : CompassLister {
            override fun onOrientationChange(
                orientation: Float,
                location: String,
                directionStr: String
            ) {
                compassview.setVal(orientation)
//                textAltitude.text = directionStr
//                textCoord.text = location
//                setImageAnimation(orientation)
//                mCurrentDegree = -orientation
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (compassManager != null) {
            compassManager!!.unbind()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == addressCode) {
            if (PermissionsUtil.lacksPermission(PermissionsUtil.getAddressPermissions())) {
                getDefaultDialog().getBuilder()
                    .isShowTiltle(false)
                    .isBackDismiss(false)
                    .setContent(getString(R.string.str_addres_permission_error))
                    .setOkText(getString(R.string.str_go_settings))
                    .setCancelText(getString(R.string.str_cancel))
                    .setCancelClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                        }
                    })
                    .setOkClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            PermissionsUtil.goToSetting(getBaseActivity())
                        }
                    })
                    .show()
            } else {
                initCompassManager()
            }
        }
    }
}