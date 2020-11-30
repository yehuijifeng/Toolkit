package com.wwxd.toolkit.activity

import android.os.Handler
import android.view.View
import com.wwdx.toolkit.utils.AppUtil
import com.wwdx.toolkit.utils.PermissionsUtil
import com.wwdx.toolkit.utils.ToastUtil
import com.wwxd.toolkit.R
import com.wwxd.toolkit.base.BaseActivity
import com.wwxd.toolkit.base.IDefaultDialogClickListener

/**
 * user：LuHao
 * time：2020/11/25 14:23
 * describe：loaindg页
 */
class LoadingActivity : BaseActivity() {
    private val sdCode = 111
    override fun getContentView(): Int {
        return R.layout.activity_loading
    }

    override fun init() {
        if (!PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
            Handler().postDelayed(object : Runnable {
                override fun run() {
                    startActivity(MainActivity::class)
                    finish()
                }
            }, 1000)
        } else {
            PermissionsUtil.requestPermissions(this, PermissionsUtil.getSdCardPermissions(), sdCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == sdCode) {
            if (!PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
                Handler().postDelayed({
                    startActivity(MainActivity::class)
                    finish()
                }, 1000)
            } else {
                getDefaultDialog().getBuilder()
                    .isShowTiltle(false)
                    .isBackDismiss(false)
                    .setContent(getString(R.string.str_sd_card_permission_error))
                    .setOkText(getString(R.string.str_go_settings))
                    .setCancelText(getString(R.string.str_exit))
                    .setCancelClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            AppUtil.exitApp()
                        }
                    })
                    .setOkClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            PermissionsUtil.goToSetting(this@LoadingActivity)
                        }
                    })
                    .show()

            }
        }
    }
}