package com.stock.calculator.activity

import com.stock.calculator.R
import com.stock.calculator.utils.ToastUtil
import kotlinx.android.synthetic.main.menu_home.*


/**
 * user：LuHao
 * time：2020/11/25 14:05
 * describe：主页
 */
class MainActivity : BaseActivity() {
    override fun isFullWindow(): Boolean {
        return true
    }

    override fun setContentView(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        
        llPyramid.setOnClickListener {
            ToastUtil.showSuccessToast(getString(R.string.str_pyramid))
        }
    }


}