package com.wwxd.translation

import android.view.View
import com.wwxd.base.BaseFragment
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
    }
}