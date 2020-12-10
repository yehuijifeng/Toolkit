package com.wwxd.toolkit.fragment

import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import com.wwxd.base.AppConstant
import com.wwxd.base.BaseFragment
import com.wwxd.base.NoDoubleClickListener
import com.wwxd.subtitles.MarqueeTextView
import com.wwxd.toolkit.R
import com.wwxd.toolkit.enums.MainMenuType
import com.wwxd.toolkit.listener.IHomeFunctionListener
import com.wwxd.utils.GsonUtil
import com.wwxd.utils.SharedPreferencesUtil
import com.wwxd.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * user：LuHao
 * time：2020/11/26 15:33
 * describe：首页卡片
 */
class HomeFragment : BaseFragment() {
    private var functionBeans = ArrayList<MainMenuType>()
    private var cacheFunctionBeans = ArrayList<MainMenuType>()
    private var recyclerAdapter: RecyclerAdapter? = null
    private var isShowDelTag = false
    var iHomeFunctionListener: IHomeFunctionListener? = null
    override fun getContentView(): Int {
        return R.layout.fragment_home
    }

    override fun init(view: View) {
        val json = SharedPreferencesUtil.getString(AppConstant.HomeFunctionList, "")
        if (TextUtils.isEmpty(json)) {
            functionBeans.add(MainMenuType.other)
            val json1 = GsonUtil.toJson(functionBeans)
            SharedPreferencesUtil.saveString(AppConstant.HomeFunctionList, json1)
        } else {
            val typeToken: TypeToken<ArrayList<MainMenuType>> =
                object : TypeToken<ArrayList<MainMenuType>>() {}
            val list = GsonUtil.fromJson<ArrayList<MainMenuType>>(json, typeToken.getType())
            if (list != null) {
                functionBeans = list
            } else {
                functionBeans.add(MainMenuType.other)
                val json1 = GsonUtil.toJson(functionBeans)
                SharedPreferencesUtil.saveString(AppConstant.HomeFunctionList, json1)
            }
        }
        rvFunction.setLayoutManager(GridLayoutManager(context, 5))
        recyclerAdapter = RecyclerAdapter()
        rvFunction.adapter = recyclerAdapter!!
        val itemTouchHelper = ItemTouchHelper(ItemDrag())
        itemTouchHelper.attachToRecyclerView(rvFunction)
        btnComplete.setOnClickListener {
            isShowDelTag = false
            recyclerAdapter?.notifyDataSetChanged()
            btnComplete.visibility = View.GONE
            btnCancel.visibility = View.GONE
            val json2 = GsonUtil.toJson(functionBeans)
            SharedPreferencesUtil.saveString(AppConstant.HomeFunctionList, json2)
        }
        btnCancel.setOnClickListener {
            isShowDelTag = false
            functionBeans.clear()
            functionBeans.addAll(cacheFunctionBeans)
            recyclerAdapter?.notifyDataSetChanged()
            btnComplete.visibility = View.GONE
            btnCancel.visibility = View.GONE
        }
    }


    //列表适配器
    private inner class RecyclerAdapter : RecyclerView.Adapter<OnViewHolder>() {
        private var itemHeight = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_function, parent, false)
            return OnViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: OnViewHolder, position: Int) {
            val mainMenuType: MainMenuType = functionBeans.get(position)
            if (isShowDelTag && mainMenuType.getMenuFragment() != null) {
                holder.imgDel.visibility = View.VISIBLE
                holder.imgDel.setOnClickListener(OnDelClick(mainMenuType))
            } else {
                holder.imgDel.visibility = View.GONE
                holder.imgDel.setOnClickListener(null)
            }
            holder.itemView.setOnClickListener(OnItemClick(mainMenuType))
            holder.itemView.setOnLongClickListener(OnItemLongClick())
            holder.textName.setText(mainMenuType.getMenuNameRes())
            if (itemHeight == 0) {
                holder.imgIcon.viewTreeObserver.addOnPreDrawListener(
                    OnShowSignImageWidth(holder.imgIcon, mainMenuType.getMenuIconRes())
                )
            } else {
                holder.imgIcon.setImageResource(mainMenuType.getMenuIconRes())
                holder.imgIcon.layoutParams.height = itemHeight
                holder.imgIcon.layoutParams.width = itemHeight
                holder.imgIcon.requestLayout()
            }
        }

        //长按编辑
        private inner class OnItemLongClick : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                if (isShowDelTag) return true
                isShowDelTag = true
                cacheFunctionBeans.clear()
                cacheFunctionBeans.addAll(functionBeans)
                notifyDataSetChanged()
                btnComplete.visibility = View.VISIBLE
                btnCancel.visibility = View.VISIBLE
                return true
            }
        }

        //进入功能
        private inner class OnItemClick(private val menuType: MainMenuType) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                if (isShowDelTag) return
                if (menuType.getMenuFragment() == null) {
                    showMultiChoiceDialog()
                } else {
                    if (iHomeFunctionListener != null)
                        iHomeFunctionListener!!.selectFunction(menuType)
                }
            }
        }

        //删除
        private inner class OnDelClick(private val menuType: MainMenuType) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                if (!isShowDelTag) return
                functionBeans.remove(menuType)
                notifyDataSetChanged()
            }
        }

        private fun showMultiChoiceDialog() {
            val items1 = ArrayList<MainMenuType>()
            val items2 = ArrayList<String>()
            val selectorFunctions = ArrayList<MainMenuType>()
            MainMenuType.values().forEach { mainMenuType ->
                var isOhterFunction = false
                functionBeans.forEach { functionBean ->
                    if (functionBean == mainMenuType) {
                        isOhterFunction = true
                    }
                }
                if (!isOhterFunction) {
                    items1.add(mainMenuType)
                    items2.add(getString(mainMenuType.getMenuNameRes()))
                }
            }

            val multiChoiceDialog: AlertDialog.Builder = AlertDialog.Builder(context!!)
            multiChoiceDialog.setTitle(getString(R.string.str_function_dialog_title))
            val itemss2 = BooleanArray(items2.size)
            multiChoiceDialog.setMultiChoiceItems(
                items2.toTypedArray(),
                itemss2,
                object : OnMultiChoiceClickListener {
                    override fun onClick(
                        dialog: DialogInterface?,
                        which: Int,
                        isChecked: Boolean
                    ) {
                        if (isChecked) {
                            selectorFunctions.add(items1[which])
                        } else {
                            selectorFunctions.remove(items1[which])
                        }
                    }
                })
            multiChoiceDialog.setPositiveButton(getString(R.string.button_ok),
                { dialog, which ->
                    if (selectorFunctions.size > 0) {
                        selectorFunctions.forEach { selectorFunction ->
                            functionBeans.add(0, selectorFunction)
                        }
                        notifyDataSetChanged()
                        val json = GsonUtil.toJson(functionBeans)
                        SharedPreferencesUtil.saveString(AppConstant.HomeFunctionList, json)
                    }
                })
            multiChoiceDialog.show()
        }

        //测量一张图片的宽度
        private inner class OnShowSignImageWidth(
            private val imageView: ImageView,
            private val res: Int
        ) :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                imageView.viewTreeObserver.removeOnPreDrawListener(this)
                if (itemHeight == 0) {
                    itemHeight = imageView.measuredWidth
                }
                imageView.setImageResource(res)
                imageView.layoutParams.height = itemHeight
                imageView.layoutParams.width = itemHeight
                imageView.requestLayout()
                return true
            }
        }

        override fun getItemCount(): Int {
            return functionBeans.size
        }
    }

    private class OnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgIcon: ImageView
        var textName: MarqueeTextView
        var imgDel: ImageView

        init {
            imgIcon = itemView.findViewById(R.id.imgIcon)
            textName = itemView.findViewById(R.id.textName)
            imgDel = itemView.findViewById(R.id.imgDel)
        }
    }

    private inner class ItemDrag : ItemTouchHelper.Callback() {
        //设置可移动的标志;
        // GridLayout，可移动标志为up,down,left,right;
        // LinerLayout，是up,down.
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            return makeMovementFlags(dragFlags, swipeFlags)
        }


        //移动时会触发这个方法
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition //取得第一个item的position
            val toPosition = target.adapterPosition //取得目标item的position
            if (fromPosition == functionBeans.size - 1 || toPosition == functionBeans.size - 1) return false
            //mChoosed是Recylerview的data集合，将两个item交换
            val item = functionBeans[fromPosition]
            functionBeans.removeAt(fromPosition)
            functionBeans.add(toPosition, item)
//            Collections.swap(functionBeans, fromPosition, toPosition)
            //recylerview的adapter通知交换更新
            recyclerAdapter?.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        //设置是否开启长按可拖拉
        override fun isLongPressDragEnabled(): Boolean {
            return isShowDelTag
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        //设置移动时背景色
        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                viewHolder!!.itemView.setBackgroundResource(R.color.color_8a8a8a)
            }
            super.onSelectedChanged(viewHolder, actionState)
        }

        //移动完成后恢复背景色
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            viewHolder.itemView.setBackgroundResource(R.drawable.bg_function)
        }
    }
}