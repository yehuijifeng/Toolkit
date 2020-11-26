package com.wwxd.toolkit.file

import android.os.Environment
import android.text.TextUtils
import com.wwdx.toolkit.utils.FileUtil

/**
 * user：LuHao
 * time：2020/9/20 14:40
 * describe：app需要用到的文件夹
 */
enum class AppFile {
    LOG_ERROR {
        override fun getFilePath(): String {
            return "error_log"
        }

        override fun getEnvironment(): String {
            return Environment.DIRECTORY_DOCUMENTS
        }

    },
    LOG_TEST {
        override fun getFilePath(): String {
            return "test_log"
        }

        override fun getEnvironment(): String {
            return Environment.DIRECTORY_DOCUMENTS
        }

    },
    IMAGE_CACHE {
        override fun getFilePath(): String {
            return "cache_image"
        }

        override fun getEnvironment(): String {
            return Environment.DIRECTORY_PICTURES
        }
    },

    IMAGE_SAVE {
        override fun getFilePath(): String {
            return "save_image"
        }

        override fun getEnvironment(): String {
            return Environment.DIRECTORY_PICTURES
        }
    },
    DOWNLOADS_APP {
        override fun getFilePath(): String {
            return "app_downloads"
        }

        override fun getEnvironment(): String {
            return Environment.DIRECTORY_DOWNLOADS
        }
    };

    abstract fun getFilePath(): String//获得子路径
    protected abstract fun getEnvironment(): String//获得父路径
    private var cacheFilePath = ""//缓存获取的路径

    //获得创建路径
    open fun ObtainAppFilePath(): String {
        if (TextUtils.isEmpty(cacheFilePath)) {
            val path = FileUtil.getAppFilePath(getEnvironment())
            if (!TextUtils.isEmpty(path)) {
                val path1 = path + "/" + getFilePath() + "/"
                if (FileUtil.createFileDirectory(path1, false)) {
                    cacheFilePath = path1
                } else
                    cacheFilePath = ""
            } else
                cacheFilePath = ""
        }
        return cacheFilePath
    }
}