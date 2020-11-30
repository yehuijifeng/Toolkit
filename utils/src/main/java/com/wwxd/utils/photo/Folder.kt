package com.wwxd.utils.photo

/**
 * user：LuHao
 * time：2019/12/20 11:06
 * describe：相册文件夹
 */
class Folder {
    var name //相册名字
            : String  = ""
    var cover //封面
            : Image? = null
    var path //相册路径
            : String? = null
    var images: ArrayList<Image> = ArrayList() //相册下所有的图片

    fun addImage(images: Image) {
        this.images.add(images)
    }
}