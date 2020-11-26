package com.wwxd.toolkit.calculator

import android.text.TextUtils
import android.view.View
import com.wwxd.toolkit.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_calculator.*

/**
 * user：LuHao
 * time：2020/11/26 17:40
 * describe：普通计算器
 */
class CalculatorFragment : BaseFragment() {
    var showStr1 = ""
    var showStr2 = ""
    var numbers = ArrayList<Double>()
    var operators = ArrayList<Double>()
    override fun setContentView(): Int {
        return R.layout.fragment_calculator
    }

    override fun init(view: View) {
        textShow1.text = ""
        textShow2.text = "0"
        textPositive.setOnClickListener {
            //正负数

        }
        textPercent.setOnClickListener {
            //模运算

        }
        textClean.setOnClickListener {
            //清除所有
            showStr2 = "0"
            showStr1 = ""
            textShow1.text = showStr1
            textShow2.text = showStr2
        }
        imgRemove.setOnClickListener {
            //退格
            if (!TextUtils.isEmpty(showStr2) && showStr2.length >= 2)
                showStr2 = showStr2.substring(0, showStr2.length - 2)
            else
                showStr2 = "0"
            textShow2.text = showStr2
        }
        textDivision.setOnClickListener {
            //除法
            showStr2 += "÷"
            textShow2.text = showStr2
        }
        textMultiplication.setOnClickListener {
            //乘法
            showStr2 += "×"
            textShow2.text = showStr2
        }
        textSubtraction.setOnClickListener {
            //减法
            showStr2 += "-"
            textShow2.text = showStr2

        }
        textAddition.setOnClickListener {
            //加法
            showStr2 += "+"
            textShow2.text = showStr2
        }
        textNumberResult.setOnClickListener {
            //计算结果
            showStr1 = showStr2
            textShow1.text = showStr1
        }
        textNumber9.setOnClickListener {
            //9
            showStr2 += "9"
            textShow2.text = showStr2
        }
        textNumber8.setOnClickListener {
            //8
            showStr2 += "8"
            textShow2.text = showStr2
        }
        textNumber7.setOnClickListener {
            //7
            showStr2 += "7"
            textShow2.text = showStr2
        }
        textNumber6.setOnClickListener {
            //6
            showStr2 += "6"
            textShow2.text = showStr2
        }
        textNumber5.setOnClickListener {
            //5
            showStr2 += "5"
            textShow2.text = showStr2
        }
        textNumber4.setOnClickListener {
            //4
            showStr2 += "4"
            textShow2.text = showStr2
        }
        textNumber3.setOnClickListener {
            //3
            showStr2 += "3"
            textShow2.text = showStr2
        }
        textNumber2.setOnClickListener {
            //2
            showStr2 += "2"
            textShow2.text = showStr2
        }
        textNumber1.setOnClickListener {
            //1
            showStr2 += "1"
            textShow2.text = showStr2
        }
        textNumber0.setOnClickListener {
            //1
            showStr2 += "0"
            textShow2.text = showStr2
        }
        textNumber_.setOnClickListener {
            //.
            showStr2 += "."
            textShow2.text = showStr2
        }
    }
}