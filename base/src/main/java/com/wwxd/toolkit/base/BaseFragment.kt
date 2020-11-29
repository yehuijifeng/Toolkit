package com.wwxd.toolkit.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList
import kotlin.reflect.KClass

/**
 * user：LuHao
 * time：2020/11/25 17:57
 * describe：fragment的基类
 */
abstract class BaseFragment : Fragment() {
    //当前fragment的view的资源id
    protected abstract fun setContentView(): Int

    //初始化，每个fragment实例化成功后都会进入的方法
    protected abstract fun init(view: View)

    private var baseActivity1: BaseActivity? = null

    protected fun getBaseActivity(): BaseActivity {
        if (baseActivity1 == null && isActivity) {
            baseActivity1 = activity as BaseActivity
        }
        return baseActivity1!!
    }

    //得到activity实例
    private var isActivity = false

    //提供给父类使用；手动调用当前fragment是显示还是隐藏
    open fun setShow(isShow: Boolean) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (defaultDialog != null) {
            defaultDialog!!.clear()
            defaultDialog = null
        }
        if(loadingView!=null){
            loadingView!!.dismiss()
            loadingView=null
        }
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this)
        }
    }

    //是否注册eventbus，默认不注册
    protected open fun isRegisterEventBus(): Boolean {
        return false
    }
    /**
     * 创建视图,传入根view
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(setContentView(), container, false)
        return view
    }

    /**
     * 视图创建,当前视图被调用的时候，activity才会被传入进来
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view)
    }

    /**
     * 此方法以后才能获取activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        isActivity = true
        if(isRegisterEventBus())
            EventBus.getDefault().register(getBaseActivity())
    }

    //软键盘监听对象
    private var inputMethodManager: InputMethodManager? = null

    //获得软键盘实例
    private fun getInputMethodManager(): InputMethodManager {
        if (inputMethodManager == null) inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
        val intent = Intent(context, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivity(intent)
    }

    protected fun startActivityForResult(cls: KClass<*>, requestCode: Int) {
        startActivityForResult(cls, null, requestCode)
    }

    protected fun startActivityForResult(cls: KClass<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(context, cls.java)
        if (bundle != null) intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    private var defaultDialog: DefaultDialog? = null

    fun getDefaultDialog(): DefaultDialog {
        if (defaultDialog == null && context != null)
            synchronized(DefaultDialog::class) {
                if (defaultDialog == null)
                    defaultDialog = DefaultDialog(context!!)
            }
        return defaultDialog!!
    }


    private var loadingView: LoadingView? = null

    private fun getLoadingView(): LoadingView? {
        if (loadingView == null && context != null)
            loadingView = LoadingView(context!!)
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
        val intent = requireActivity().intent
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