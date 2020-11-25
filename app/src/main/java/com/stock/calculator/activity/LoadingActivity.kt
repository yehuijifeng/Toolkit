package com.stock.calculator.activity

import android.os.Handler
import com.stock.calculator.R

/**
 * user：LuHao
 * time：2020/11/25 14:23
 * describe：loaindg页
 */
class LoadingActivity : BaseActivity() {
    override fun setContentView(): Int {
        return R.layout.activity_loading
    }

    override fun init() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                startActivity(MainActivity::class)
            }
        }, 1000)
    }
}