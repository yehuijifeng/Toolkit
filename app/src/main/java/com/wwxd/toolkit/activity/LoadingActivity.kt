package com.wwxd.toolkit.activity

import android.os.Handler
import com.wwxd.toolkit.R
import com.wwxd.toolkit.base.BaseActivity

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
                finish()
            }
        }, 1000)
    }
}