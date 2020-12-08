package com.wwxd.toolkit.activity

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import com.wwxd.base.*
import com.wwxd.toolkit.R
import com.wwxd.toolkit.enums.MainMenuType
import com.wwxd.toolkit.fragment.RewardFragment
import com.wwxd.utils.*
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
                getDefaultDialog().getBuilder()
                    .setTitle(getString(R.string.str_reward_title))
                    .setContent(getString(R.string.str_reward_content))
                    .setOkText(getString(R.string.str_reward_ok))
                    .setCancelText(getString(R.string.str_reward_cancel))
                    .isBackDismiss(true)
                    .setCancelClick(object : IDefaultDialogClickListener {
                        @SuppressLint("WrongConstant")
                        override fun onClick(v: View) {
                            Handler().postDelayed(object :Runnable{
                                override fun run() {
                                    getDefaultDialog().getBuilder()
                                        .setContent(getString(R.string.str_wx_content))
                                        .setOkText(getString(R.string.str_wx_ok))
                                        .isBackDismiss(true)
                                        .isNoCancle(true)
                                        .isShowTiltle(false)
                                        .setOkClick(object : IDefaultDialogClickListener {
                                            override fun onClick(v: View) {
                                                val filePath =
                                                    AppFile.IMAGE_SAVE.ObtainAppFilePath() + "Toolkit_Wx.jpg"
                                                val isCreate: Boolean
                                                if (!FileUtil.isFile(filePath)) {
                                                    isCreate = FileUtil.saveImageFilePath(
                                                        BitmapUtil.createBitmap(R.drawable.ic_reward_wx),
                                                        filePath
                                                    )
                                                } else isCreate = true
                                                if (isCreate) {
                                                    BitmapUtil.updatePhonePictures(filePath)
                                                    val intent = Intent()
                                                    intent.component =
                                                        ComponentName(
                                                            "com.tencent.mm",
                                                            "com.tencent.mm.ui.LauncherUI"
                                                        )
                                                    intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
                                                    intent.flags =
                                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                                    intent.action = Intent.ACTION_VIEW
                                                    startActivity(intent)
                                                } else {
                                                    showFragment(RewardFragment::class)
                                                }
                                            }
                                        })
                                        .show()
                                }
                            },500L)
                        }
                    })
                    .setOkClick(object : IDefaultDialogClickListener {
                        override fun onClick(v: View) {
                            //跳转到支付宝付款界面
                            if (AppUtil.checkApkExist("com.eg.android.AlipayGphone")) {
                                try {
                                    val intentFullUrl =
                                        "intent://platformapi/startapp?saId=10000007&" +
                                                "clientVersion=3.7.0.0718&qrcode=" +
                                                "HTTPS://QR.ALIPAY.COM/FKX04615LYUKBSTECVZQ56?t=1607419600762" +//付款吗扫描结果替换
                                                "?_s=Dweb-other&_t=1472443966571#Intent;" +
                                                "scheme=alipayqr;package=com.eg.android.AlipayGphone;end"
                                    startActivity(
                                        Intent.parseUri(
                                            intentFullUrl,
                                            Intent.URI_INTENT_SCHEME
                                        )
                                    )
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                showFragment(RewardFragment::class)
                            }
                        }
                    })
                    .show()
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