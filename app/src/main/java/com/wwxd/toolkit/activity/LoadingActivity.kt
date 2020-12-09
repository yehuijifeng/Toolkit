package com.wwxd.toolkit.activity

import android.os.Handler
import android.view.View
import com.wwxd.base.AppConstant
import com.wwxd.base.BaseActivity
import com.wwxd.base.IDefaultDialogClickListener
import com.wwxd.toolkit.R
import com.wwxd.toolkit.dialog.AgreementDialog
import com.wwxd.toolkit.listener.IAgreementListener
import com.wwxd.utils.AppUtil
import com.wwxd.utils.PermissionsUtil
import com.wwxd.utils.SharedPreferencesUtil

/**
 * user：LuHao
 * time：2020/11/25 14:23
 * describe：loaindg页
 */
class LoadingActivity : BaseActivity() {
    override fun isFullWindow(): Boolean {
        return true
    }

    private val sdCode = 111
    override fun getContentView(): Int {
        return R.layout.activity_loading
    }

    override fun init() {
        if (SharedPreferencesUtil.getBoolean(AppConstant.isStartPrivacy, false)) {
            startApp()
        } else {
            val agreementDialog = AgreementDialog(this)
            agreementDialog.showView(object : IAgreementListener {
                override fun clean() {
                    AppUtil.exitApp()
                }

                override fun confirm() {
                    SharedPreferencesUtil.saveBoolean(AppConstant.isStartPrivacy, true)
                    startApp()
                }
            })
        }
    }

    private fun startApp() {
        if (!PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
            startMain()
        } else {
            PermissionsUtil.requestPermissions(
                this,
                PermissionsUtil.getSdCardPermissions(),
                sdCode
            )
        }
    }

    private fun startMain() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(MainActivity::class)
                finish()
            }
        }, 500)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == sdCode) {
            if (!PermissionsUtil.lacksPermission(PermissionsUtil.getSdCardPermissions())) {
                startMain()
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