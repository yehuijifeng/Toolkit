package com.wwxd.toolkit.enums

import com.wwxd.calculator.CalculatorFragment
import com.wwxd.compass.CompassFragment
import com.wwxd.protractor.ProtractorFragment
import com.wwxd.pyramid.PyramidFragment
import com.wwxd.qr_code1.QR_codeFragment
import com.wwxd.ruler.RuleFragment
import com.wwxd.subtitles.SubtitlesFragment
import com.wwxd.tesseract_ocr.OcrFragment
import com.wwxd.toolkit.R
import com.wwxd.toolkit.fragment.HomeFragment
import com.wwxd.toolkit.fragment.RewardFragment
import com.wwxd.translation.TranslationFragment
import kotlin.reflect.KClass


/**
 * user：LuHao
 * time：2020/12/8 14:17
 * describe：首页菜单
 */
enum class MainMenuType {
    //首页
    home {
        override fun getMenuNameRes(): Int {
            return R.string.str_home
        }

        override fun getMenuIconRes(): Int {
            return R.mipmap.ic_launcher_round
        }

        override fun getMenuFragment(): KClass<*> {
            return HomeFragment::class
        }
    },

    //股市金字塔
    pyramid {
        override fun getMenuNameRes(): Int {
            return R.string.str_pyramid
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_pyramid
        }

        override fun getMenuFragment(): KClass<*> {
            return PyramidFragment::class
        }
    },

    //计算器
    calculator {
        override fun getMenuNameRes(): Int {
            return R.string.str_calculator
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_calculator
        }

        override fun getMenuFragment(): KClass<*> {
            return CalculatorFragment::class
        }
    },

    //二维码
    QR_code {
        override fun getMenuNameRes(): Int {
            return R.string.str_qr_code
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_qr_code
        }

        override fun getMenuFragment(): KClass<*> {
            return QR_codeFragment::class
        }
    },

    //尺子
    ruler {
        override fun getMenuNameRes(): Int {
            return R.string.str_ruler
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_ruler
        }

        override fun getMenuFragment(): KClass<*> {
            return RuleFragment::class
        }
    },

    //量角器
    protractor {
        override fun getMenuNameRes(): Int {
            return R.string.str_protractor
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_protractor
        }

        override fun getMenuFragment(): KClass<*> {
            return ProtractorFragment::class
        }
    },

    //文字识别
    ocr {
        override fun getMenuNameRes(): Int {
            return R.string.str_ocr
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_ocr
        }

        override fun getMenuFragment(): KClass<*> {
            return OcrFragment::class
        }
    },

    //指南针
    compass {
        override fun getMenuNameRes(): Int {
            return R.string.str_compass
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_compass
        }

        override fun getMenuFragment(): KClass<*> {
            return CompassFragment::class
        }
    },

    //翻译
    translation {
        override fun getMenuNameRes(): Int {
            return R.string.str_translation
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_translation
        }

        override fun getMenuFragment(): KClass<*> {
            return TranslationFragment::class
        }
    },
    //LED字幕
    subtitles {
        override fun getMenuNameRes(): Int {
            return R.string.str_subtitles
        }

        override fun getMenuIconRes(): Int {
            return R.drawable.ic_subtitles
        }

        override fun getMenuFragment(): KClass<*> {
            return SubtitlesFragment::class
        }
    },

    ;

    //菜单名
    abstract fun getMenuNameRes(): Int

    //菜单图标
    abstract fun getMenuIconRes(): Int

    //菜单对象
    abstract fun getMenuFragment(): KClass<*>
}