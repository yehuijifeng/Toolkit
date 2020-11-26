package com.wwxd.toolkit.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wwxd.toolkit.base.BaseActivity
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
}