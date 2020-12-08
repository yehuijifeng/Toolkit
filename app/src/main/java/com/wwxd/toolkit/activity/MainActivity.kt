package com.wwxd.toolkit.activity

import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.wwxd.base.*
import com.wwxd.calculator.CalculatorFragment
import com.wwxd.compass.CompassFragment
import com.wwxd.protractor.ProtractorFragment
import com.wwxd.pyramid.PyramidFragment
import com.wwxd.qr_code1.QR_codeFragment
import com.wwxd.ruler.RuleFragment
import com.wwxd.tesseract_ocr.OcrFragment
import com.wwxd.toolkit.R
import com.wwxd.toolkit.enums.MainMenuType
import com.wwxd.toolkit.fragment.HomeFragment
import com.wwxd.toolkit.fragment.RewardFragment
import com.wwxd.translation.TranslationFragment
import com.wwxd.utils.AppUtil
import com.wwxd.utils.DateUtil
import com.wwxd.utils.PhoneUtil
import com.wwxd.utils.ToastUtil
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
        toolBar.setTitle(R.string.app_name)
        toolBar.setTitleTextAppearance(this, R.style.home_title_text_style)
        //设置导航图标要在setSupportActionBar方法之后
        setSupportActionBar(toolBar)
        toolBar.setNavigationIcon(R.drawable.ic_home_title_left)
        toolBar.setNavigationOnClickListener {
            dlHome.openDrawer(Gravity.LEFT)
        }
        for (i in 0 until MainMenuType.values().size) {
            val action = MainMenuType.values()[i]
            fragmentMap.put(action.getMenuFragment(), null)
            val itemView = View.inflate(this, R.layout.item_menu_layout, null)
            val imgMainMenu = itemView.findViewById<ImageView>(R.id.imgMainMenu)
            val textMainMenu = itemView.findViewById<TextView>(R.id.textMainMenu)
            imgMainMenu.setImageResource(action.getMenuIconRes())
            textMainMenu.setText(action.getMenuNameRes())
            itemView.setOnClickListener(OnItemMenuClick(action))
            llMenu.addView(itemView)
            if (i == 0) {
                showFragment(action.getMenuFragment())
            }
        }
        fragmentMap.put(RewardFragment::class, null)
    }

    private inner class OnItemMenuClick(private val mainMenuType: MainMenuType) :
        NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View) {
            showFragment(mainMenuType.getMenuFragment())
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
            R.id.menuReward -> {
                //Reward
                showFragment(RewardFragment::class)
            }
            R.id.menuFeedback -> {
                getDefaultDialog().getBuilder()
                    .setTitle(getString(R.string.str_help_title))
                    .setContent(getString(R.string.str_help_content))
                    .setOkText(getString(R.string.str_help_tel))
                    .setCancelText(getString(R.string.str_help_email))
                    .isBackDismiss(true)
                    .setCancelClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            PhoneUtil.sendHelpEmail(this@MainActivity)
                        }
                    })
                    .setOkClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            PhoneUtil.toTel(this@MainActivity, AppConstant.serviceTel)
                        }
                    })
                    .show()

            }
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