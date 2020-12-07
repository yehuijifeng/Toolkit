package com.wwxd.translation

import android.view.View
import com.wwxd.base.BaseFragment
import com.wwxd.utils.SharedPreferencesUtil
import kotlinx.android.synthetic.main.fragment_translation.*

/**
 * user：LuHao
 * time：2020/12/4 16:14
 * describe：翻译
 */
class TranslationFragment : BaseFragment() {

    override fun getContentView(): Int {
        return R.layout.fragment_translation
    }

    override fun init(view: View) {
        btnTransDef.setOnClickListener {
            startActivity(TransDefActivity::class)
        }
        btnTransVoice.setOnClickListener {
            startActivity(TransVoiceActivity::class)
        }
        btnTransCamera.setOnClickListener {
            startActivity(TransCameraActivity::class)
        }
    }

    override fun onResume() {
        super.onResume()
        btnTransDef.text = String.format(getString(R.string.str_trans_def), getDefUseNum())
        btnTransVoice.text = String.format(getString(R.string.str_trans_voice), getVoiceUseNum())
        btnTransCamera.text = String.format(getString(R.string.str_trans_camera), getCameraUseNum())
    }

    private fun getDefUseNum(): Int {
        return TransContanst.TransDefMaxNum - SharedPreferencesUtil.getInt(
            TransContanst.TransDefNum,
            0
        )
    }

    private fun getVoiceUseNum(): Int {
        return TransContanst.TransVoiceMaxNum - SharedPreferencesUtil.getInt(
            TransContanst.TransVoiceNum,
            0
        )
    }

    private fun getCameraUseNum(): Int {
        return TransContanst.TransCameraMaxNum - SharedPreferencesUtil.getInt(
            TransContanst.TransCameraNum,
            0
        )
    }
}