package com.wwxd.toolkit.base

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.reflect.KClass

/**
 * user：LuHao
 * time：2020/11/25 14:05
 * describe：activity的基类
 */
abstract class BaseActivity : AppCompatActivity() {
    //layout资源文件
    protected abstract fun getContentView(): Int
    protected abstract fun init()
//    private lateinit var binding: ActivityMainBinding
    //root视图
    protected var root: ViewGroup? = null
        get() {
            if (field == null) {
                field = findViewById(android.R.id.content)
            }
            return field
        }
        private set

    //是否全屏
    protected open fun isFullWindow(): Boolean {
        return false
    }

    //底部导航栏的背景色
    protected open fun getNavigationBarColor(): Int {
        return R.color.getNavigationBarColor
    }

    //顶部状态栏背景色
    protected open fun getStatusBarColor(): Int {
        return R.color.getStatusBarColor
    }

    //顶部状态栏字体高亮显示；
    //true，浅色，白色；
    //false，深色，黑色
    protected open fun statusBarTextColor(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //屏幕方向垂直
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //是否全屏
        if (isFullWindow()) {
            //去除导航栏栏
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                window.statusBarColor = Color.TRANSPARENT
            } else {
                //去除状态栏
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        } else {
            //设置顶部状态栏字体颜色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //设置状态栏颜色
                window.statusBarColor =
                    ResourcesCompat.getColor(resources, getStatusBarColor(), null)
                //底部导航栏的颜色
                window.navigationBarColor =
                    ResourcesCompat.getColor(resources, getNavigationBarColor(), null)
            }
        }
        //通知栏字体颜色；true,白色;false,黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (statusBarTextColor())
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            else
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(getContentView())
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this)
        }
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (defaultDialog != null) {
            defaultDialog!!.clear()
            defaultDialog = null
        }
        if (loadingView != null) {
            loadingView!!.dismiss()
            loadingView = null
        }
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    //是否注册eventbus，默认不注册
    protected open fun isRegisterEventBus(): Boolean {
        return false
    }

    //软键盘监听对象
    private var inputMethodManager: InputMethodManager? = null

    //获得软键盘实例
    private fun getInputMethodManager(): InputMethodManager {
        if (inputMethodManager == null) inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return inputMethodManager!!
    }

    //隐藏软键盘
    fun hideSoftInputFromWindow(view: View) {
        getInputMethodManager().hideSoftInputFromWindow(view.windowToken, 0)
    }

    //显示软键盘
    fun showSoftInputFromWindow(view: View) {
        getInputMethodManager().showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    protected fun startActivity(cla: KClass<*>) {
        startActivity(cla, null)
    }

    protected fun startActivity(cls: KClass<*>, bundle: Bundle?) {
        val intent = Intent(this@BaseActivity, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
    }

    protected fun startActivityForResult(cls: KClass<*>, requestCode: Int) {
        startActivityForResult(cls, null, requestCode)
    }

    protected fun startActivityForResult(cls: KClass<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this@BaseActivity, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    private var defaultDialog: DefaultDialog? = null

    fun getDefaultDialog(): DefaultDialog {
        if (defaultDialog == null)
            synchronized(DefaultDialog::class) {
                if (defaultDialog == null)
                    defaultDialog = DefaultDialog(this)
            }
        return defaultDialog!!
    }


    private var loadingView: LoadingView? = null

    private fun getLoadingView(): LoadingView? {
        if (loadingView == null)
            loadingView = LoadingView(this)
        return loadingView
    }

    fun showLoadingView() {
        if (getLoadingView() != null)
            getLoadingView()?.getBuilder()?.show()
    }

    fun showLoadingView(content: String) {
        if (getLoadingView() != null)
            getLoadingView()?.getBuilder()?.content(content)?.show()
    }

    fun showLoadingView(contentRes: Int) {
        if (getLoadingView() != null)
            getLoadingView()?.getBuilder()?.content(getString(contentRes))?.show()
    }

    fun closeLoadingView() {
        if (getLoadingView() != null)
            getLoadingView()?.getBuilder()?.close()
    }

    //返回当前intent中的bundle
    private fun getBundle(): Bundle? {
        val intent = intent
        return intent?.extras
    }

    //获得bundle中的特定int值
    fun getInt(key: String, defaultValue: Int): Int {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getInt(key, defaultValue) else defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getLong(key, defaultValue) else defaultValue)
    }

    //获得bundle中的特定int值
    fun getString(key: String, defaultValue: String): String {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getString(key, defaultValue) else defaultValue)
    }

    //获得bundle中的特定boolean值
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getBoolean(key, defaultValue) else defaultValue)
    }

    //获得bundle中的特定double值
    fun getDouble(key: String, defaultValue: Double): Double {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getDouble(key, defaultValue) else defaultValue)
    }

    //获得bundle中的特定float值
    fun getFloat(key: String, defaultValue: Float): Float {
        val bundle = getBundle()
        return (if (bundle != null) bundle.getFloat(key, defaultValue) else defaultValue)
    }

    //获得bundle中的特定序列化值
    fun getParcelable(key: String): Parcelable? {
        val bundle = getBundle()
        return bundle?.getParcelable(key)
    }

    //获得bundle中的特定string集合
    fun getStringArrayList(key: String): ArrayList<String>? {
        val bundle = getBundle()
        return bundle?.getStringArrayList(key)
    }

    //获得bundle中的特定int集合
    fun getIntegerArrayList(key: String): ArrayList<Int>? {
        val bundle = getBundle()
        return bundle?.getIntegerArrayList(key)
    }

    //获得bundle中的特定序列化集合
    fun getParcelableList(key: String): ArrayList<out Parcelable>? {
        val bundle = getBundle()
        return bundle?.getParcelableArrayList(key)
    }
}