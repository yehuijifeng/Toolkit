package com.wwxd.toolkit.QR_code

import android.Manifest
import android.content.Intent
import android.view.View
import com.wwdx.toolkit.utils.PermissionsUtil
import com.wwdx.toolkit.utils.StringUtil
import com.wwdx.toolkit.utils.ToastUtil
import com.wwxd.toolkit.QR_code.bean.ZxingConfig
import com.wwxd.toolkit.QR_code.common.Constant
import com.wwxd.toolkit.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_qr_code.*


/**
 * user：LuHao
 * time：2020/11/26 18:16
 * describe：二维码扫描
 */
class QR_codeFragment : BaseFragment() {

    private val cameraCode = 111

    override fun setContentView(): Int {
        return R.layout.fragment_qr_code
    }

    override fun init(view: View) {
        btnPicture.setOnClickListener {
            if (PermissionsUtil.checkCamera()) {
                startQRCodeActivty()
            } else {
                PermissionsUtil.requestPermissions(
                    this,
                    Manifest.permission.CAMERA,
                    cameraCode
                )
            }
        }
    }

    private fun startQRCodeActivty() {
        val intent = Intent(context, CaptureActivity::class.java)
        val config = ZxingConfig()
        config.isPlayBeep = true
        config.isShake = true
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config)
        startActivityForResult(intent, cameraCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraCode) {
            if (PermissionsUtil.lacksPermission(Manifest.permission.CAMERA)) {
                ToastUtil.showLongToast(StringUtil.getString(R.string.str_open_camera_permission))
            } else {
                startQRCodeActivty()
            }
        }
    }

}