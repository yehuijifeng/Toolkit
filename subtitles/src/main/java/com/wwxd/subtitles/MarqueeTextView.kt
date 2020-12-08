package com.wwxd.subtitles

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by LuHao on 2017/7/6.
 * 跑马灯textview
 */
class MarqueeTextView : AppCompatTextView {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
    }

    private fun initView() {
        //跑马灯缺少以下两种任何一个属性都不行
        marqueeRepeatLimit = -1
        ellipsize = TextUtils.TruncateAt.MARQUEE //超出文本的省略号显示方式，这里选择没有省略号
        isSingleLine = true //因为文字只能显示一行，在一行内实现跑马灯。所以设置属性单行模式
    }

    //跑马灯属性只有在获得焦点的情况下才有动画。
    //所以，这里我们让这个textview自动获得焦点
    override fun isFocused(): Boolean {
        return true
    }
}