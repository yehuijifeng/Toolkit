package com.wwxd.toolkit.calculator

import java.util.*

/**
 * Created by Eric
 * on 2016/11/2.
 */
class BaseCalculator {
    private val operSet = charArrayOf(
        OperatorType.add.getValue(),
        OperatorType.sub.getValue(),
        OperatorType.mul.getValue(),
        OperatorType.div.getValue(),
        OperatorType.bracketLeft.getValue(),
        OperatorType.bracketRight.getValue(),
        OperatorType.last.getValue(),
    )

    //Map结构方便后面取运算符的下标
    private val operMap: Map<Char, Int> = object : HashMap<Char, Int>() {
        init {
            put(OperatorType.add.getValue(), 0)
            put(OperatorType.sub.getValue(), 1)
            put(OperatorType.mul.getValue(), 2)
            put(OperatorType.div.getValue(), 3)
            put(OperatorType.bracketLeft.getValue(), 4)
            put(OperatorType.bracketRight.getValue(), 5)
            put(OperatorType.last.getValue(), 6)
        }
    }

    //运算符优先级表，operPrior[oper1下标][oper2下标]
    private val operPrior = arrayOf(
        charArrayOf('>', '>', '<', '<', '<', '>', '>'),
        charArrayOf('>', '>', '<', '<', '<', '>', '>'),
        charArrayOf('>', '>', '>', '>', '<', '>', '>'),
        charArrayOf('>', '>', '>', '>', '<', '>', '>'),
        charArrayOf('<', '<', '<', '<', '<', '=', ' '),
        charArrayOf('>', '>', '>', '>', ' ', '>', '>'),
        charArrayOf('<', '<', '<', '<', '<', ' ', '=')
    )

    //返回2个运算符优先级比较的结果'<','=','>'
    private fun getPrior(oper1: Char, oper2: Char): Char {
        return operPrior[operMap[oper1]!!][operMap[oper2]!!] //Map.get方法获取运算符的下标
    }

    //简单四则运算
    private fun operate(a: Double, oper: Char, b: Double): Double {
        return when (oper) {
            OperatorType.add.getValue() -> a + b
            OperatorType.sub.getValue() -> a - b
            OperatorType.mul.getValue() -> a * b
            OperatorType.div.getValue() -> {
                if (b == 0.0) {
                    Double.MAX_VALUE //处理异常
                } else a / b
            }
            else -> 0.0
        }
    }

    //计算普通的运算式
    private fun calSubmath(math: String): Double {
        return if (math.length == 0) {
            Double.MAX_VALUE
        } else {
            if (!hasOper(math.substring(1, math.length)) || math.contains("E-")) {
                return math.toDouble()
            }
            //设置flag用于存储math开始位置的负数，如-3-5中的-3，避免-被识别成运算符而出错
            var flag = 0
            var math1 = math
            if (math1[0] == OperatorType.sub.getValue()) {
                flag = 1
                math1 = math1.substring(1, math1.length)
            }
            val operStack = Stack<Char>() //oper栈
            val numStack = Stack<Double>() //num栈
            operStack.push(OperatorType.last.getValue()) //设置栈底元素
            math1 += OperatorType.last.getValue()
            var tempNum = "" //暂存数字str

            //计算math1
            var i = 0
            while (i < math1.length) {
                var charOfMath = math1[i] //遍历math1中的char

                //(1)num进栈
                if (!isOper(charOfMath) //1.不是oper
                    || charOfMath == OperatorType.sub.getValue() && math1[i - 1] == OperatorType.bracketLeft.getValue()
                ) {    //2.是'-'并且'-'左边有'('，说明是在math1中间用负数
                    tempNum += charOfMath

                    //1.1 获取下一个char
                    i++
                    charOfMath = math1[i]

                    //1.2 判断下一个char是不是oper,如果是oper，就将num压入numStack
                    if (isOper(charOfMath)) {   //此条件成功时，下次for循环就直接跳到else语句了
                        var num = tempNum.toDouble()
                        if (flag == 1) {        //恢复math1首位的负数
                            num = -num
                            flag = 0
                        }
                        numStack.push(num) //push num
                        tempNum = "" //重置tempNum
                    }

                    //1.3 //回退，以免下次循环for语句自身的i++使得跳过了这个char
                    i--
                } else {
                    when (getPrior(operStack.peek(), charOfMath)) {
                        OperatorType.lt.getValue() -> operStack.push(charOfMath)
                        OperatorType.eq.getValue() -> operStack.pop()
                        OperatorType.gt.getValue() -> {
                            val oper = operStack.pop()
                            val b = numStack.pop()
                            val a = numStack.pop()
                            if (operate(a, oper, b) == Double.MAX_VALUE)
                                return Double.MAX_VALUE
                            numStack.push(operate(a, oper, b))
                            i-- //继续比较该oper与栈顶oper的关系
                        }
                    }
                }
                i++
            }
            numStack.peek() //最后的math1变成一个num了
        }
    }

    //计算math，添加了一些特殊math的处理
    fun cal(math: String): Double {
        return if (math.length == 0) { //处理异常
            Double.MAX_VALUE
        } else {
            //运算式只是数字的特征：从第2个char开始math中没有oper
            if (!hasOper(math.substring(1, math.length)) || math.contains("E-")) {
                math.toDouble()
            } else {
                calSubmath(math)
            }
        }
    }

    //判断String中是否有运算符
    private fun hasOper(s: String): Boolean {
        return s.contains(OperatorType.add.getValue())
                || s.contains(OperatorType.sub.getValue())
                || s.contains(OperatorType.mul.getValue())
                || s.contains(OperatorType.div.getValue())
    }

    //判断字符是否为运算符
    private fun isOper(c: Char): Boolean {
        var i: Int
        i = 0
        while (i < operSet.size) {
            if (c == operSet[i]) {
                break
            }
            i++
        }
        //break出来，说明是oper，i != operSize
        return i != operSet.size
    }
}