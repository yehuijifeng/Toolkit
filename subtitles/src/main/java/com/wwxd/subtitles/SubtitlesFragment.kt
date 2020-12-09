package com.wwxd.subtitles

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wwxd.base.BaseFragment
import com.wwxd.base.NoDoubleClickListener
import kotlinx.android.synthetic.main.fragment_subtitles.*

/**
 * user：LuHao
 * time：2020/12/8 14:31
 * describe：LED字幕
 */
class SubtitlesFragment : BaseFragment() {

    private val colorList: ArrayList<IntArray>
    private var backColor: Int = 0
    private var textColor: Int = 0
    private var textSize: Int = 50
    private var textContent: String = ""

    init {
        colorList = ArrayList()
    }

    override fun getContentView(): Int {
        return R.layout.fragment_subtitles
    }

    override fun init(view: View) {
        colorList.add(intArrayOf(R.color.black, R.string.str_black))
        colorList.add(intArrayOf(R.color.white, R.string.str_white))
        colorList.add(intArrayOf(R.color.red, R.string.str_red))
        colorList.add(intArrayOf(R.color.yello, R.string.str_yello))
        colorList.add(intArrayOf(R.color.gray, R.string.str_gray))
        colorList.add(intArrayOf(R.color.blue, R.string.str_blue))
        colorList.add(intArrayOf(R.color.green, R.string.str_green))
        colorList.add(intArrayOf(R.color.orange, R.string.str_orange))
        colorList.add(intArrayOf(R.color.purple, R.string.str_purple))
        colorList.add(intArrayOf(R.color.color_FFC0CB, R.string.str_FFC0CB))
        colorList.add(intArrayOf(R.color.color_33A1C9, R.string.str_33A1C9))
        colorList.add(intArrayOf(R.color.color_DA70D6, R.string.str_DA70D6))
        colorList.add(intArrayOf(R.color.color_8B4513, R.string.str_8B4513))
        colorList.add(intArrayOf(R.color.color_228B22, R.string.str_228B22))
        colorList.add(intArrayOf(R.color.color_FFD700, R.string.str_FFD700))
        colorList.add(intArrayOf(R.color.color_708069, R.string.str_708069))
        backColor = colorList[0][0]
        textColor = colorList[1][0]
        imgBack.setImageResource(backColor)
        imgText.setImageResource(textColor)
        imgBack.isSelected=true
        imgText.isSelected=true
        rvBack.adapter = RecyclerAdapterBack()
        rvBack.setLayoutManager(GridLayoutManager(context, 5))
        rvText.adapter = RecyclerAdapterText()
        rvText.setLayoutManager(GridLayoutManager(context, 5))
        etSize.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.length == 0) {
                    textSize = 50
                } else {
                    textSize = s.toString().toInt()
                }
            }
        })
        etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null || s.length == 0) {
                    textContent = ""
                } else {
                    textContent = s.toString()
                }
            }
        })
        btnGo.setOnClickListener {
            startSubtitlesActivity()
        }
        // 监听回车键
        etContent.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->

            /**
             *
             * @param v 被监听的对象
             * @param actionId  动作标识符,如果值等于EditorInfo.IME_NULL，则回车键被按下。
             * @param event    如果由输入键触发，这是事件；否则，这是空的(比如非输入键触发是空的)。
             * @return 返回你的动作
             */
            if (actionId== EditorInfo.IME_ACTION_DONE) {
                startSubtitlesActivity()
                true
            } else
                false
        })
    }

    private fun startSubtitlesActivity() {
        val bundle = Bundle()
        bundle.putInt(SubtitlesConstant.textSize, textSize)
        bundle.putString(SubtitlesConstant.textContent, textContent)
        bundle.putInt(SubtitlesConstant.backColor, backColor)
        bundle.putInt(SubtitlesConstant.textColor, textColor)
        startActivity(SubtitlesActivity::class, bundle)
    }

    //列表适配器
    private inner class RecyclerAdapterBack : RecyclerView.Adapter<ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_subtitles, parent, false)
            return ImageViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val images: IntArray = colorList.get(position)
            if (backColor == images[0]) {
                holder.itemView.isSelected = true
            } else {
                holder.itemView.isSelected = false
            }
            holder.itemView.setOnClickListener(OnSelectImageClick(images))
            holder.imgColor.setImageResource(images[0])
            holder.textColor.setText(images[1])
        }

        //查看大图
        private inner class OnSelectImageClick(private val images: IntArray) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                backColor = images[0]
                notifyDataSetChanged()
                imgBack.setImageResource(backColor)
            }
        }


        override fun getItemCount(): Int {
            return colorList.size
        }
    }

    //列表适配器
    private inner class RecyclerAdapterText : RecyclerView.Adapter<ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_subtitles, parent, false)
            return ImageViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val images: IntArray = colorList.get(position)
            if (textColor == images[0]) {
                holder.itemView.isSelected = true
            } else {
                holder.itemView.isSelected = false
            }
            holder.itemView.setOnClickListener(OnSelectImageClick(images))
            holder.imgColor.setImageResource(images[0])
            holder.textColor.setText(images[1])
        }

        //查看大图
        private inner class OnSelectImageClick(private val images: IntArray) :
            NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                textColor = images[0]
                notifyDataSetChanged()
                imgText.setImageResource(textColor)
            }
        }


        override fun getItemCount(): Int {
            return colorList.size
        }
    }

    private inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgColor: ImageView
        var textColor: TextView

        init {
            imgColor = itemView.findViewById(R.id.imgColor)
            textColor = itemView.findViewById(R.id.textColor)
        }
    }
}