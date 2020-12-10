package com.wwxd.toolkit.listener

import com.wwxd.toolkit.enums.MainMenuType

/**
 * user：LuHao
 * time：2020/12/10 11:54
 * describe：切换功能
 */
interface IHomeFunctionListener {
    fun selectFunction(mainMenuType: MainMenuType)
}