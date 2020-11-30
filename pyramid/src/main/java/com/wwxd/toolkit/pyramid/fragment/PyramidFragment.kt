package com.wwxd.toolkit.fragment

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.wwdx.toolkit.utils.ToastUtil
import com.wwxd.toolkit.base.BaseFragment
import com.wwxd.toolkit.pyramid.R
import kotlinx.android.synthetic.main.fragment_pyramid.*
import java.lang.Exception

/**
 * user：LuHao
 * time：2020/11/25 18:19
 * describe：金字塔
 */
class PyramidFragment : BaseFragment() {
    private val defPhaseNum = 20
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
                updatePyramid()
                true
            }
            false
        }
        btnSelect.setOnClickListener { updatePyramid() }
    }

    //更新开盘价的金字塔价格
    private fun updatePyramid() {
        if (TextUtils.isEmpty(etPrice.text)) return
        try {
            val openingPrice = etPrice.text.toString().toDouble()
            hideSoftInputFromWindow(etPrice)
            llPyramidBuy.removeAllViews()
            llPyramidSell.removeAllViews()
            for (i in 0 until defPhaseNum) {
                val itemBuyView = View.inflate(context, R.layout.item_pyramid_buy, null)
                val textPyramidBuy1 = itemBuyView.findViewById<TextView>(R.id.textPyramidBuy1)
                val textPyramidBuy2 = itemBuyView.findViewById<TextView>(R.id.textPyramidBuy2)
                textPyramidBuy1.text = upBuyOrSellPercentage(i, true)
                textPyramidBuy2.text = upBuyOrSellValue(i, openingPrice, true)
                llPyramidBuy.addView(itemBuyView)
                val itemSellView = View.inflate(context, R.layout.item_pyramid_sell, null)
                val textPyramidSell1 = itemSellView.findViewById<TextView>(R.id.textPyramidSell1)
                val textPyramidSell2 = itemSellView.findViewById<TextView>(R.id.textPyramidSell2)
                textPyramidSell1.text = upBuyOrSellPercentage(i, false)
                textPyramidSell2.text = upBuyOrSellValue(i, openingPrice, false)
                llPyramidSell.addView(itemSellView)
            }
        } catch (e: Exception) {
            etPrice.setText("")
            ToastUtil.showFailureToast(getString(R.string.str_price_error))
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