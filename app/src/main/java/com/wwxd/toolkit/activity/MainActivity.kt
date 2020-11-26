package com.wwxd.toolkit.activity

import android.view.Gravity
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import com.wwdx.toolkit.utils.DateUtil
import com.wwdx.toolkit.utils.ToastUtil
import com.wwxd.toolkit.R
import com.wwxd.toolkit.base.BaseActivity
import com.wwxd.toolkit.calculator.CalculatorFragment
import com.wwxd.toolkit.fragment.HomeFragment
import com.wwxd.toolkit.fragment.PyramidFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass


/**
 * user：LuHao
 * time：2020/11/25 14:05
 * describe：主页
 */
class MainActivity : BaseActivity() {
    private var pyramidFragment: PyramidFragment? = null
    private var homeFragment: HomeFragment? = null
    private var calculatorFragment: CalculatorFragment? = null

    override fun isFullWindow(): Boolean {
        return true
    }

    override fun setContentView(): Int {
        return R.layout.activity_main
    }

    override fun init() {
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
            dlHome.closeDrawers()
        }
        llCalculator.setOnClickListener {
            showFragment(CalculatorFragment::class)
            dlHome.closeDrawers()
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
        val name = clazz.simpleName
        if (name.equals(PyramidFragment::class.simpleName)) {
            if (pyramidFragment == null || pyramidFragment!!.isHidden) {
                if (pyramidFragment == null) {
                    pyramidFragment = PyramidFragment()
                    fragmentTransaction.add(R.id.flHome, pyramidFragment!!)
                    fragmentTransaction.setMaxLifecycle(
                        pyramidFragment!!,
                        Lifecycle.State.CREATED
                    )
                }
                fragmentTransaction.setMaxLifecycle(pyramidFragment!!, Lifecycle.State.RESUMED)
                fragmentTransaction.show(pyramidFragment!!)
            }
        } else if (name.equals(HomeFragment::class.simpleName)) {
            if (homeFragment == null || homeFragment!!.isHidden) {
                if (homeFragment == null) {
                    homeFragment = HomeFragment()
                    fragmentTransaction.add(R.id.flHome, homeFragment!!)
                    fragmentTransaction.setMaxLifecycle(
                        homeFragment!!,
                        Lifecycle.State.CREATED
                    )
                }
                fragmentTransaction.setMaxLifecycle(homeFragment!!, Lifecycle.State.RESUMED)
                fragmentTransaction.show(homeFragment!!)
            }
        } else if (name.equals(CalculatorFragment::class.simpleName)) {
            if (calculatorFragment == null || calculatorFragment!!.isHidden) {
                if (calculatorFragment == null) {
                    calculatorFragment = CalculatorFragment()
                    fragmentTransaction.add(R.id.flHome, calculatorFragment!!)
                    fragmentTransaction.setMaxLifecycle(
                        calculatorFragment!!,
                        Lifecycle.State.CREATED
                    )
                }
                fragmentTransaction.setMaxLifecycle(calculatorFragment!!, Lifecycle.State.RESUMED)
                fragmentTransaction.show(calculatorFragment!!)
            }
        }
        hideFragment(clazz, fragmentTransaction)
        fragmentTransaction.commitAllowingStateLoss()
    }

    //隐藏出目标外的fragment
    private fun hideFragment(clazz: KClass<*>, fragmentTransaction: FragmentTransaction) {
        val fragmentName = clazz.simpleName
        if (!PyramidFragment::class.simpleName.equals(fragmentName)
            && pyramidFragment != null
            && !pyramidFragment!!.isHidden
        ) {
            fragmentTransaction.hide(pyramidFragment!!)
        }
        if (!HomeFragment::class.simpleName.equals(fragmentName)
            && homeFragment != null
            && !homeFragment!!.isHidden
        ) {
            fragmentTransaction.hide(homeFragment!!)
        }
        if (!CalculatorFragment::class.simpleName.equals(fragmentName)
            && calculatorFragment != null
            && !calculatorFragment!!.isHidden
        ) {
            fragmentTransaction.hide(calculatorFragment!!)
        }
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
                moveTaskToBack(true)
                return true
            }
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}