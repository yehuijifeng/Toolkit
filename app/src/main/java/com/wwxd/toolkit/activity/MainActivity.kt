package com.wwxd.toolkit.activity

import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Lifecycle
import com.wwxd.utils.AppUtil
import com.wwxd.utils.DateUtil
import com.wwxd.utils.ToastUtil
import com.wwxd.protractor.ProtractorFragment
import com.wwxd.ruler.RuleFragment
import com.wwxd.QR_code.QR_codeFragment
import com.wwxd.base.BaseActivity
import com.wwxd.base.BaseFragment
import com.wwxd.calculator.CalculatorFragment
import com.wwxd.toolkit.fragment.HomeFragment
import com.wwxd.pyramid.PyramidFragment
import com.wwxd.toolkit.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass


/**
 * user：LuHao
 * time：2020/11/25 14:05
 * describe：主页
 */
class MainActivity : BaseActivity() {
    private val fragmentMap = HashMap<KClass<*>, BaseFragment?>()

    override fun isFullWindow(): Boolean {
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        fragmentMap.put(HomeFragment::class, null)
        fragmentMap.put(PyramidFragment::class, null)
        fragmentMap.put(CalculatorFragment::class, null)
        fragmentMap.put(QR_codeFragment::class, null)
        fragmentMap.put(RuleFragment::class, null)
        fragmentMap.put(ProtractorFragment::class, null)
        toolBar.setTitle(R.string.app_name)
        toolBar.setTitleTextAppearance(this, R.style.home_title_text_style)
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_home_title_left)
        toolBar.setNavigationOnClickListener {
            dlHome.openDrawer(Gravity.LEFT)
        }
        showFragment(HomeFragment::class)
        llPyramid.setOnClickListener {
            showFragment(PyramidFragment::class)
        }
        llCalculator.setOnClickListener {
            showFragment(CalculatorFragment::class)
        }
        llQrCode.setOnClickListener {
            showFragment(QR_codeFragment::class)
        }
        llRuler.setOnClickListener {
            showFragment(RuleFragment::class)

        }
        llProtractor.setOnClickListener {
            showFragment(ProtractorFragment::class)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menuAbout -> ToastUtil.showLongToast(R.string.str_about)
            R.id.menuSetting -> ToastUtil.showLongToast(R.string.str_setting)
            R.id.menuExceptional -> ToastUtil.showLongToast(R.string.str_exceptional)
            R.id.menuFeedback -> ToastUtil.showLongToast(R.string.str_feedback)
        }
        return true
    }

    //展示fragment
    private fun showFragment(clazz: KClass<*>) {
        val fragmentTransaction = getSupportFragmentManager().beginTransaction()
        if (!fragmentMap.containsKey(clazz)) return
        fragmentMap.forEach { (key, value) ->
            if (clazz.simpleName.equals(key.simpleName)) {
                if (value == null || value.isHidden) {
                    if (value == null) {
                        val value1 = key.java.newInstance()
                        fragmentMap[key] = value1 as BaseFragment
                        fragmentTransaction.add(R.id.flHome, value1)
                        fragmentTransaction.setMaxLifecycle(value1, Lifecycle.State.CREATED)
                        fragmentTransaction.setMaxLifecycle(value1, Lifecycle.State.RESUMED)
                        fragmentTransaction.show(value1)
                    } else {
                        fragmentTransaction.setMaxLifecycle(value, Lifecycle.State.RESUMED)
                        fragmentTransaction.show(value)
                    }
                }
            } else {
                if (value != null && !value.isHidden) {
                    fragmentTransaction.hide(value)
                }
            }
        }
        fragmentTransaction.commitAllowingStateLoss()
        dlHome.closeDrawers()
    }

    protected var exitTime: Long = 0 //计算用户点击返回键的时间


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) { //表示按返回键时的操作
            exitTime = if (DateUtil.getServerTime() - exitTime > 2000) {
                ToastUtil.showLongToast(R.string.str_again_click_exit)
                DateUtil.getServerTime()
            } else {
                ToastUtil.cancelToast()
                //此处写退向后台的处理
                AppUtil.exitApp()
                return true
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}