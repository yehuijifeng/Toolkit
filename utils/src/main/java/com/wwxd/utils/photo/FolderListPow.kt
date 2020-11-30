package com.wwxd.utils.photo

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.wwxd.toolkit.R
import com.wwxd.base.BasePopupWindow
import com.wwxd.base.NoDoubleClickListener

/**
 * user：LuHao
 * time：2019/12/20 13:24
 * describe：系统相册文件夹
 */
class FolderListPow(
    context: Context?,
    folders: List<Folder>?,
    private val onSelectFolderListener: OnSelectFolderListener?
) : BasePopupWindow(context) {
    private var llFolder: LinearLayout? = null


    //背景色
    override fun getBackDrawable(): Int {
        return R.color.transparent
    }

    /**
     * 居中显示
     *
     * @param parent 外部传递进来的，pow依赖的view
     */
    override fun showAtLocation(parent: View) {
        val location = IntArray(2)
        // 获得位置
        parent.getLocationOnScreen(location)
        showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] + parent.height)
    }

    override fun isFullWindow(): Boolean {
        return false
    }

    override fun getWidthAndHeight(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun getRootView(): Int {
        return R.layout.pow_folder_list
    }

    override fun isFocusable(): Boolean {
        return true
    }

    override fun isOutsideTouchable(): Boolean {
        return true
    }

    override fun initView(view: View) {
        llFolder = view.findViewById(R.id.llFolder)
    }

    private fun getFolderView(index: Int, folder: Folder) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pow_folder, null, false)
        val llRoot = view.findViewById<LinearLayout>(R.id.llRoot)
        val textFolder = view.findViewById<TextView>(R.id.textFolder)
        textFolder.text =
            StringBuilder(folder.name!!).append("(").append(folder.images.size).append(")")
        textFolder.isSelected = index == 0
        llRoot.setOnClickListener(OnSelectClick(index, folder))
        llFolder!!.addView(view)
    }

    private inner class OnSelectClick(private val index: Int, private val folder: Folder) :
        NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View) {
            for (i in 0 until llFolder!!.childCount) {
                val textFolder = llFolder!!.getChildAt(i).findViewById<TextView>(R.id.textFolder)
                if (i == index) {
                    if (!textFolder.isSelected) {
                        textFolder.isSelected = true
                        onSelectFolderListener?.onSelect(folder)
                    }
                } else {
                    if (textFolder.isSelected) {
                        textFolder.isSelected = false
                    }
                }
            }
            dismiss()
        }
    }

    init {
        if (folders != null && folders.size > 0) {
            for (i in folders.indices) {
                getFolderView(i, folders[i])
            }
            llFolder!!.requestLayout()
        }
    }
}