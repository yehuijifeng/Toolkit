package com.wwxd.toolkit.fragment

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.wwxd.toolkit.base.BaseFragment
import com.wwxd.toolkit.pyramid.R
import kotlinx.android.synthetic.main.fragment_pyramid.*
import java.lang.StringBuilder

/**
 * user：LuHao
 * time：2020/11/25 18:19
 * describe：金字塔
 */
class PyramidFragment : BaseFragment() {
    private val defPhaseNum = 10
    private val defConnectStr = "     -->     "
    private val startValue = 0.5//阶段分割，0.5个百分点
    override fun setContentView(): Int {
        return R.layout.fragment_pyramid
    }

    override fun init(view: View) {
        etPrice.setOnKeyListener { v, keyCode, event ->
            if (event != null
                && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                && KeyEvent.ACTION_DOWN == event.getAction()
                && !TextUtils.isEmpty(etPrice.text)
            ) {
                updatePyramid(etPrice.text.toString().toDouble())
                true
            }
            false
        }
    }

    //更新开盘价的金字塔价格
    private fun updatePyramid(openingPrice: Double) {
        llPyramidBuy.removeAllViews()
        llPyramidSell.removeAllViews()
        for (i in 0 until defPhaseNum) {
            val itemBuyView = View.inflate(context, R.layout.item_pyramid_buy, null)
            val textBuyView = itemBuyView.findViewById<TextView>(R.id.textPyramidBuy)
            textBuyView.text = StringBuilder()
                .append(upBuyOrSellPercentage(i, true))
                .append(defConnectStr)
                .append(upBuyOrSellValue(i, openingPrice, true))
            llPyramidBuy.addView(itemBuyView)

            val itemSellView = View.inflate(context, R.layout.item_pyramid_sell, null)
            val textSellView = itemSellView.findViewById<TextView>(R.id.textPyramidSell)
            textSellView.text = StringBuilder()
                .append(upBuyOrSellPercentage(i, false))
                .append(defConnectStr)
                .append(upBuyOrSellValue(i, openingPrice, false))
            llPyramidSell.addView(itemSellView)
        }
    }

    //增/减量值
    private fun upBuyOrSellPercentage(phase: Int, isBuy: Boolean): String {
        val endValue = startValue * (phase + 1)
        return "${if (isBuy) "-" else "+"}${endValue}%"
    }

    //增/减量
    private fun upBuyOrSellValue(phase: Int, openingPrice: Double, isBuy: Boolean): String {
        var endValue: Double
        if (isBuy) {
            endValue = openingPrice * (100 - (startValue * (phase + 1)))
        } else {
            endValue = openingPrice * (100 + (startValue * (phase + 1)))
        }
        endValue /= 100
        return String.format("%.3f", endValue)
    }
}