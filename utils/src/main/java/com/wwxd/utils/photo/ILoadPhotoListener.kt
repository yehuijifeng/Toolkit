package com.wwxd.utils.photo

/**
 * user：LuHao
 * time：2019/12/20 14:11
 * describe：加载系统相册回调监听
 */
interface ILoadPhotoListener {
    fun over(folders: List<Folder>)
}