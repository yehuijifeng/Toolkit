package com.wwxd.calculator

/**
 * user：LuHao
 * time：2020/11/26 17:52
 * describe：运算符
 */
enum class OperatorType {
    add {
        override fun getValue(): Char {
            return '+'
        }
    },//+
    sub {
        override fun getValue(): Char {
            return '-'
        }
    },//-
    mul {
        override fun getValue(): Char {
            return '×'
        }
    },//*
    div {
        override fun getValue(): Char {
            return '÷'
        }
    },///
    bracketLeft {
        override fun getValue(): Char {
            return '('
        }
    },//括号
    bracketRight {
        override fun getValue(): Char {
            return ')'
        }
    },
    num_0 {
        override fun getValue(): Char {
            return '0'
        }
    },
    num_1 {
        override fun getValue(): Char {
            return '1'
        }
    },
    num_2 {
        override fun getValue(): Char {
            return '2'
        }
    },
    num_3 {
        override fun getValue(): Char {
            return '3'
        }
    },
    num_4 {
        override fun getValue(): Char {
            return '4'
        }
    },
    num_5 {
        override fun getValue(): Char {
            return '5'
        }
    },
    num_6 {
        override fun getValue(): Char {
            return '6'
        }
    },
    num_7 {
        override fun getValue(): Char {
            return '7'
        }
    },
    num_8 {
        override fun getValue(): Char {
            return '8'
        }
    },
    num_9 {
        override fun getValue(): Char {
            return '9'
        }
    },
    num_point {
        override fun getValue(): Char {
            return '.'
        }
    },
    gt {
        override fun getValue(): Char {
            return '>'
        }
    },
    lt {
        override fun getValue(): Char {
            return '<'
        }
    },
    eq {
        override fun getValue(): Char {
            return '='
        }
    },
    last {
        override fun getValue(): Char {
            return '#'
        }
    },
    ;//括号

    abstract fun getValue(): Char
}