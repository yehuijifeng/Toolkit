package com.wwxd.calculator

import android.text.TextUtils
import android.view.View
import com.wwxd.base.BaseFragment
import com.wwxd.calculator.R
import kotlinx.android.synthetic.main.fragment_calculator.*

/**
 * user：LuHao
 * time：2020/11/26 17:40
 * describe：普通计算器
 */
class CalculatorFragment : BaseFragment() {
    //存储过去和当前的math
    private var mathPast = ""
    private var mathNow = ""
    private var precision = 9
    private var equal_flag = 0 //0:
    private val scienceCalculator = BaseCalculator()  //一个科学计算器

    override fun getContentView(): Int {
        return R.layout.fragment_calculator
    }

    override fun init(view: View) {
        textShow1.setFocusable(false) //不可得到焦点
        textShow1.setFocusableInTouchMode(false) //不获取焦点
        textShow1.setLongClickable(false) //没有长按事件
        textShow1.setTextIsSelectable(false) //文本不可选择
        textShow1.setCursorVisible(false) //光标不可见
        textShow2.setFocusable(false)//不可得到焦点
        textShow2.setFocusableInTouchMode(false)//不获取焦点
        textShow2.setLongClickable(false)//没有长按事件
        textShow2.setTextIsSelectable(false)//文本不可选择
        textShow2.setCursorVisible(false)//光标不可见
        setShow1Content("")
        setShow2Content("0")
        textParenthesesLeft.setOnClickListener {
            //左括号
            if (equal_flag == 1) {
                mathNow = ""
                equal_flag = 0
            }
            if (mathNow.length == 0) {                                //1.mathNow为空，+(
                mathNow += OperatorType.bracketLeft.getValue()
            } else if (isOper(mathNow[mathNow.length - 1])) {  //2.mathNow最后一个字符是+-/*，+(
                mathNow += OperatorType.bracketLeft.getValue()
            } else if (mathNow[mathNow.length - 1] == OperatorType.bracketRight.getValue()) {   //4.mathNow最后一个字符是)，说明用户是在补全右括号，+)
                mathNow += OperatorType.bracketRight.getValue()
            }
            setShow2Content(mathNow)
        }
        textParenthesesRight.setOnClickListener {
            //右括号
            var leftNum = 0
            var rightNum = 0
            for (i in 0 until mathNow.length) {
                if (mathNow[i] == OperatorType.bracketLeft.getValue()) leftNum++
                if (mathNow[i] == OperatorType.bracketRight.getValue()) rightNum++
            }
            val missingNum = leftNum - rightNum //缺失的 ) 数量
            //最后一个字符是数字，或者是）
            if (missingNum > 0
                && (isNum(mathNow[mathNow.length - 1])
                        || mathNow[mathNow.length - 1] == OperatorType.bracketRight.getValue())
            ) {
                mathNow += OperatorType.bracketRight.getValue()
            }
            setShow2Content(mathNow)
        }
        textClean.setOnClickListener {
            //清除所有
            mathNow = ""
            mathPast = ""
            equal_flag = 0
            setShow1Content("")
            setShow2Content("0")
        }
        imgRemove.setOnClickListener {
            //退格
            if (equal_flag == 1) {
                mathNow = ""
                equal_flag = 0
                setShow2Content("0")
            } else {
                if (!TextUtils.isEmpty(mathNow) && mathNow.length >= 2) {
                    mathNow = mathNow.substring(0, mathNow.length - 1)
                    setShow2Content(mathNow)
                } else {
                    mathNow = ""
                    setShow2Content("0")
                }
            }

        }
        textDivision.setOnClickListener {
            //除法
            inputOperator(OperatorType.div.getValue())
        }
        textMultiplication.setOnClickListener {
            //乘法
            inputOperator(OperatorType.mul.getValue())
        }
        textSubtraction.setOnClickListener {
            //减法
            inputOperator(OperatorType.sub.getValue())
        }
        textAddition.setOnClickListener {
            //加法
            inputOperator(OperatorType.add.getValue())
        }
        textNumberResult.setOnClickListener {
            //计算结果
            //右括号自动补全
            //右括号自动补全
            var leftNum = 0
            var rightNum = 0
            for (i in 0 until mathNow.length) {
                if (mathNow[i] == OperatorType.bracketLeft.getValue()) leftNum++
                if (mathNow[i] == OperatorType.bracketRight.getValue()) rightNum++
            }
            var missingNum = leftNum - rightNum //缺失的 ) 数量
            while (missingNum > 0) {
                mathNow += OperatorType.bracketRight.getValue()
                missingNum--
            }
            setShow2Content(mathNow)
            mathPast = mathNow.trimIndent() //使得呈现的mathPast自动换行
            val result=scienceCalculator.cal(mathNow)
            if ( result== Double.MAX_VALUE)
                mathNow = "Math Error"
            else {
//                val b = BigDecimal(result)
                //四舍五入保留相应位数小数
//                val double1 = b.setScale(precision, BigDecimal.ROUND_HALF_UP).toDouble()
                mathNow = result.toString()
                if (mathNow[mathNow.length - 2] == OperatorType.num_point.getValue()
                    && mathNow[mathNow.length - 1] == OperatorType.num_0.getValue()
                ) {
                    mathNow = mathNow.substring(0, mathNow.length - 2)
                }
            }
            mathPast = "$mathPast=$mathNow"
            //用tvPast.set(mathPast)不能实现自动滚动到最新运算过程
            setShow1Content(mathPast)
            setShow2Content(mathNow)
            //设置flag=1
            equal_flag = 1
        }
        textNumber9.setOnClickListener {
            //9
            input1_9(OperatorType.num_9.getValue())
        }
        textNumber8.setOnClickListener {
            //8
            input1_9(OperatorType.num_8.getValue())
        }
        textNumber7.setOnClickListener {
            //7
            input1_9(OperatorType.num_7.getValue())
        }
        textNumber6.setOnClickListener {
            //6
            input1_9(OperatorType.num_6.getValue())
        }
        textNumber5.setOnClickListener {
            //5
            input1_9(OperatorType.num_5.getValue())
        }
        textNumber4.setOnClickListener {
            //4
            input1_9(OperatorType.num_4.getValue())
        }
        textNumber3.setOnClickListener {
            //3
            input1_9(OperatorType.num_3.getValue())
        }
        textNumber2.setOnClickListener {
            //2
            input1_9(OperatorType.num_2.getValue())
        }
        textNumber1.setOnClickListener {
            //1
            input1_9(OperatorType.num_1.getValue())
        }

        textNumber0.setOnClickListener {
            //0
            //如果flag=1，表示要输入新的运算式，清空mathNow并设置flag=0
            if (equal_flag == 1) {
                mathNow = ""
                equal_flag = 0
            }
            if (mathNow.length == 0) {                    //1.mathNow为空，+0
                mathNow += OperatorType.num_0.getValue()
            } else if (mathNow.length == 1) {             //2.mathNow 长度为1
                if (mathNow[0] == OperatorType.num_0.getValue()) {                 //2.1 如果该字符为0，不加
                    mathNow += ""
                } else if (isNum(mathNow[0])) {          //2.2 如果该字符为1-9，+0
                    mathNow += OperatorType.num_0.getValue()
                }
            } else if (!isNum(mathNow[mathNow.length - 2]) && mathNow[mathNow.length - 1] == OperatorType.num_0.getValue()) {
                mathNow += ""//3.属于2.1的一般情况，在math中间出现 比如：×0 +0
            } else {//4.除此之外，+0
                mathNow += OperatorType.num_0.getValue()
            }
            setShow2Content(mathNow)
        }
        textNumber_.setOnClickListener {
            //.
            if (equal_flag == 1) {
                mathNow = ""
                equal_flag = 0
            }
            if (mathNow.length == 0) {                                //1.mathNow为空，+0.
                mathNow += OperatorType.num_0.getValue() + "" + OperatorType.num_point.getValue()
            } else if (isOper(mathNow[mathNow.length - 1])
                || mathNow[mathNow.length - 1] == OperatorType.bracketLeft.getValue()
            ) {  //2.mathNow的最后一个字符为+-*/，+0.
                mathNow += OperatorType.num_0.getValue() + "" + OperatorType.num_point.getValue()
            } else if (isPoint(mathNow)) {
                mathNow += OperatorType.num_point.getValue()
            }
            setShow2Content(mathNow)
        }
    }

    //是否可以加.
    private fun isPoint(content: String): Boolean {
        //纯数字，+-*/，没有值
        val ch = content[content.length - 1]
        if (ch == OperatorType.num_point.getValue()
            || ch == OperatorType.bracketRight.getValue()
        )
            return false
        if (content.contains(OperatorType.num_point.getValue())) {
            //包含了小数点，则需要计算
            if (content.contains(OperatorType.add.getValue())
                || content.contains(OperatorType.sub.getValue())
                || content.contains(OperatorType.mul.getValue())
                || content.contains(OperatorType.div.getValue())
            ) {
                //包含标点符号，需要计算
                var isHaveOper = false

                for (i in (content.length - 1) downTo 0) {
                    if (isOper(mathNow[i])) {
                        //是标点符号则重新计算
                        isHaveOper = true
                    } else if (mathNow[i] == OperatorType.num_point.getValue()) {
                        //小数点在+-*/之前，则可以添加小数点
                        break
                    }
                }
                return isHaveOper
            } else {
                return false
            }
        } else {
            if (isNum(content[content.length - 1]))
                return true
            else
                return false
        }
    }

    //输入运算符-+/*
    private fun inputOperator(operator: Char) {
        if (mathNow.length > 0) {
            val ch = mathNow[mathNow.length - 1]
            if (isNum(ch) || ch == OperatorType.bracketRight.getValue())
                mathNow += operator
            setShow2Content(mathNow)
            equal_flag = 0 //可能用运算结果直接运算，flag直接设0
        }
    }

    //输入1-9的数字
    private fun input1_9(number: Char) {
        if (equal_flag == 1) {
            mathNow = ""
            equal_flag = 0
        }
        if (mathNow.length == 0) {
            mathNow += number
        } else {
            //math的最后一个字符是：1-9；+-/*； (； .；
            val ch = mathNow[mathNow.length - 1]
            if (isNum(ch)
                || isOper(ch)
                || ch == OperatorType.bracketLeft.getValue()
                || ch == OperatorType.num_point.getValue()
            )
                mathNow += number
        }
        setShow2Content(mathNow)
    }

    //判断当前字符是否为数字
    private fun isNum(c: Char): Boolean {
        val num = charArrayOf(
            OperatorType.num_0.getValue(),
            OperatorType.num_1.getValue(),
            OperatorType.num_2.getValue(),
            OperatorType.num_3.getValue(),
            OperatorType.num_4.getValue(),
            OperatorType.num_5.getValue(),
            OperatorType.num_6.getValue(),
            OperatorType.num_7.getValue(),
            OperatorType.num_8.getValue(),
            OperatorType.num_9.getValue(),
        )
        for (i in 0 until num.size) {
            if (num[i] == c) return true
        }
        return false
    }

    //判断当前字符是否为运算符
    private fun isOper(c: Char): Boolean {
        val oper = charArrayOf(
            OperatorType.add.getValue(),
            OperatorType.sub.getValue(),
            OperatorType.mul.getValue(),
            OperatorType.div.getValue()
        )
        for (i in 0 until oper.size) {
            if (oper[i] == c) return true
        }
        return false
    }

    private fun setShow2Content(content: String) {
        textShow2.setText(content)
        textShow2.setSelection(content.length)
    }

    private fun setShow1Content(content: String) {
        textShow1.setText(content)
        textShow1.setSelection(content.length)
    }
}