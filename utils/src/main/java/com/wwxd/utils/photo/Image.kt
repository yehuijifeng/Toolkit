package com.wwxd.utils.photo

import android.os.Parcel
import android.os.Parcelable

/**
 * user：LuHao
 * time：2019/12/20 11:06
 * describe：图片信息
 */
class Image() : Parcelable {
    var path: String? = null
    var uriId = 0L
    var size //图片大小，gif图片不得超过500KB;单位：字节 byte
            : Long = 0

    constructor(parcel: Parcel) : this() {
        path = parcel.readString()
        uriId = parcel.readLong()
        size = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeLong(uriId)
        parcel.writeLong(size)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }

}