package com.wwxd.utils.glide

import android.content.Context
import android.os.Build
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.module.AppGlideModule

/**
 * user：LuHao
 * time：2019/12/25 17:01
 * describe：文件缓存路径
 */
@GlideModule
class GlideCache : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val diskCacheSizeBytes = 1024 * 1024 * 100L // 100 MB
        //手机app路径
        var imageCachePath: String
        imageCachePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (file != null) file.absolutePath else {
                return
            }
        } else {
            Environment.getExternalStorageDirectory().path + "/qyqx"
        }
        imageCachePath += "/cache_glide/"
        builder.setDiskCache( DiskLruCacheFactory(imageCachePath, diskCacheSizeBytes)
        )
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
    }
}