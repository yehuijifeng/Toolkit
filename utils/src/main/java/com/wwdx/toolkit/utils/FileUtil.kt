package com.wwdx.toolkit.utils

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.wwxd.toolkit.base.AppConstant
import java.io.*
import java.nio.charset.Charset
import java.util.*

/**
 * user：LuHao
 * time：2019/10/28 15:23
 * describe：文件工具类
 */
object FileUtil {

    // 判断是否缺少权限。
    fun checkSdCard(): Boolean {
        return ContextCompat.checkSelfPermission(
            AppConstant.getApp(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            AppConstant.getApp(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    //适配android10的app各个路径
    fun getAppFilePath(environment: String?): String {
        var file: File?
        if (AppUtil.isAndroidQ()) {
            file = AppConstant.getApp().getExternalFilesDir(environment)
        } else {
            file = Environment.getExternalStorageDirectory()
            if (file == null) return ""
            file = File(file.absolutePath + "/stock")
        }
        return if (file != null && (file.exists() || createFileDirectory(
                file.absolutePath,
                false
            ))
        ) file.absolutePath else ""
    }

    /**
     * 复制文件
     *
     * @param file     待复制的文件
     * @param filePath 粘贴的文件路径
     * @return 复制成功后的文件
     */
    fun copyFile(file: File?, filePath: String?): File? {
        return try {
            val input = FileInputStream(file)
            copyFile(input, filePath)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 复制文件
     *
     * @param inputStream 文件输入流
     * @param filePath    粘贴的文件路径
     * @return 复制成功后的文件
     */
    fun copyFile(inputStream: InputStream?, filePath: String?): File? {
        return try {
            if (inputStream == null) return null
            val copyFile = File(filePath)
            if (copyFile.isDirectory) return null
            if (copyFile.exists() && !copyFile.delete()) return null else if (!copyFile.exists() && !copyFile.createNewFile()) return null
            //这种方法会覆盖原来文件内容
            val output = FileOutputStream(copyFile)
            val buf = ByteArray(1024)
            var bytesRead: Int
            while (inputStream.read(buf).also { bytesRead = it } > 0) {
                output.write(buf, 0, bytesRead)
            }
            inputStream.close()
            output.close()
            copyFile
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 查看文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小 B
     */
    fun getFileSize(filePath: String?): Long {
        var size: Long = 0
        try {
            if (!TextUtils.isEmpty(filePath)) {
                val file = File(filePath)
                if (file.exists() && file.isFile) {
                    size = file.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 查看文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小
     */
    fun getFileSizeStr(filePath: String?): String {
        val size = getFileSize(filePath) / 1024
        val sizeStr: String
        sizeStr = if (size / 1024 > 0) {
            if (size / 1024 / 1024 > 0) {
                if (size / 1024 / 1024 / 1024 > 0) {
                    (size / 1024 / 1024 / 1024).toString() + " GB"
                } else {
                    (size / 1024 / 1024).toString() + " MB"
                }
            } else {
                (size / 1024).toString() + " KB"
            }
        } else {
            "$size B"
        }
        return sizeStr
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    fun deleteFile(file: File?): Boolean {
        return if (file == null || !checkSdCard()) false else try {
            if (file.exists()) {
                if (file.isFile) {
                    file.delete()
                } else if (file.isDirectory) {
                    val files = file.listFiles()
                    var isAllDel = true
                    if (files != null && files.size > 0) {
                        for (file1 in files) {
                            if (file1.exists() && file1.isFile) {
                                if (!file.delete()) {
                                    isAllDel = false
                                }
                            }
                        }
                        isAllDel
                    } else {
                        file.delete()
                    }
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     */
    fun deleteFile(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) return false
        val file = File(filePath)
        return deleteFile(file)
    }

    /**
     * 文件重命名
     *
     * @param filePath    文件绝对路径
     * @param newFilePath 新文件绝对路径
     */
    fun renameFile(filePath: String?, newFilePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(newFilePath)) return true
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            val newfile = File(newFilePath)
            return if (newfile.exists() || newfile.isDirectory) false else file.renameTo(newfile)
        }
        return false
    }

    //音乐类型文件
    fun music(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("mp3")
        fileTypes.add("WAV")
        fileTypes.add("MIDI")
        fileTypes.add("MP3Pro")
        fileTypes.add("WMA")
        fileTypes.add("SACD")
        fileTypes.add("QuickTime")
        fileTypes.add("lrc")
        return fileTypes
    }

    //视频类型文件
    fun video(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("avi")
        fileTypes.add("mp4")
        fileTypes.add("mpeg")
        fileTypes.add("wmv")
        fileTypes.add("WMA")
        fileTypes.add("mov")
        fileTypes.add("flv")
        fileTypes.add("VQF")
        fileTypes.add("rmvb")
        return fileTypes
    }

    //文档类型文件
    fun word(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("doc")
        fileTypes.add("docx")
        return fileTypes
    }

    //表格类型文件
    fun excel(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("xls")
        fileTypes.add("xlsx")
        return fileTypes
    }

    //表格类型文件
    fun ppt(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("ppt")
        fileTypes.add("pptx")
        return fileTypes
    }

    //记事本类型文件
    fun txt(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("txt")
        return fileTypes
    }

    //app类型文件
    fun apk(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("apk")
        return fileTypes
    }

    //图片类型文件
    fun image(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("BMP")
        fileTypes.add("GIF")
        fileTypes.add("JPEG")
        fileTypes.add("TIFF")
        fileTypes.add("PSD")
        fileTypes.add("PNG")
        fileTypes.add("3gp")
        fileTypes.add("jpg")
        return fileTypes
    }

    //压缩文件类型文件
    fun zip(): List<String> {
        val fileTypes: MutableList<String> = ArrayList()
        fileTypes.add("zip")
        fileTypes.add("rar")
        fileTypes.add("iso")
        fileTypes.add("tar")
        fileTypes.add("jar")
        fileTypes.add("UUEuue")
        fileTypes.add("ARJ")
        fileTypes.add("KZ")
        return fileTypes
    }

    /**
     * 创建文件
     *
     * @param filePath   文件路径
     * @param isReCreate 如果文件存在，是否删除新建
     */
    fun createFile(filePath: String?, isReCreate: Boolean): File? {
        if (TextUtils.isEmpty(filePath)) return null
        try {
            val file = File(filePath)
            if (file.exists()) { //存在
                if (isReCreate) {
                    if (file.delete() && file.createNewFile()) return file
                } else {
                    return file
                }
            } else if (file.createNewFile()) {
                return file
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 创建文件目录
     *
     * @param filePath   文件目录路径
     * @param isReCreate 如果目录存在，是否删除新建
     */
    fun createFileDirectory(filePath: String?, isReCreate: Boolean): Boolean {
        if (TextUtils.isEmpty(filePath)) return false
        try {
            val file = File(filePath)
            if (file.exists()) { //存在
                if (isReCreate) {
                    if (file.delete()) return file.mkdirs()
                } else {
                    return true
                }
            } else {
                return file.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun insertFile(inputStream: InputStream?, filePath: String?): File? {
        if (inputStream == null) return null
        try {
            //androidQ的沙盒模式
            val file = File(filePath)
            var isCreateFile = false
            if (file.exists()) {
                if (file.delete()) {
                    isCreateFile = file.createNewFile()
                }
            } else {
                isCreateFile = file.createNewFile()
            }
            //文件创建成功
            if (isCreateFile) {
                val outputStream: OutputStream = FileOutputStream(file)
                val buf = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buf).also { bytesRead = it } > 0) {
                    outputStream.write(buf, 0, bytesRead)
                }
                inputStream.close()
                outputStream.flush()
                outputStream.close()
                return file
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    /*
     * 创建文件
     * */
    fun insertFile(content: String?, filePath: String?): File? {
        if (TextUtils.isEmpty(content)) return null
        try {
            //androidQ的沙盒模式
            val file = File(filePath)
            var isCreateFile = false
            if (file.exists()) {
                if (file.delete()) {
                    isCreateFile = file.createNewFile()
                }
            } else {
                isCreateFile = file.createNewFile()
            }
            //文件创建成功
            if (isCreateFile) {
                val fos = FileOutputStream(file)
                val osw = OutputStreamWriter(fos, Charset.forName("UTF-8"))
                osw.write(content)
                osw.flush()
                fos.flush()
                osw.close()
                fos.close()
                return file
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    /**
     * 将uri写入文件
     *
     * @param uri
     * @param filePath
     */
    fun insertFile(uri: Uri?, filePath: String?): File? {
        return if (uri == null) null else try {
            val inputStream = AppConstant.getApp().contentResolver.openInputStream(uri)
            insertFile(inputStream, filePath)
        } catch (e: Exception) {
            null
        }
    }

    //保存bitmap到sd卡中
    fun saveImageFilePath(bitmap: Bitmap?, newFilePath: String?): Boolean {
        return if (bitmap == null) false else try {
            deleteFile(newFilePath)
            val file = File(newFilePath)
            //创建新的文件
            if (!file.createNewFile()) return false
            //将bitmap保存到文件中
            val fos = FileOutputStream(file)
            // 把Bitmap对象解析成流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            true
        } catch (e: Exception) {
            false
        }
    }

    //获得uri的文件路径
    fun queryUriFilePath(uriId: Int): String? {
        var filePath = ""
        //查询的条件语句
        val selection = MediaStore.Images.Media._ID + "=? "
        val cursor = AppConstant.getApp().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DATA),
            selection, arrayOf(uriId.toString() + ""),
            null
        )
        //是否查询到了
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        }
        cursor?.close()
        return filePath
    }

    //获得文件后缀
    fun getFileSuffix(filePath: String): String {
        var suffix = ""
        if (!TextUtils.isEmpty(filePath)) {
            val index = filePath.lastIndexOf(".")
            if (index > -1) {
                suffix = filePath.substring(index)
            }
        }
        return suffix
    }

    //获得图片文件后缀
    fun getImageFileSuffix(filePath: String): String? {
        var suffix = getFileSuffix(filePath)
        if (TextUtils.isEmpty(suffix)) suffix = ".jpg"
        return suffix
    }

    //是否是文件
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }

    //是否是文件
    fun isFile(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) return false
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    //是否是文件夹
    fun isDirectory(filePath: String?): Boolean {
        if (TextUtils.isEmpty(filePath)) return false
        val file = File(filePath)
        return file.exists() && file.isDirectory
    }

    //将本地图片转换成uri
    fun toUri(filePath: String?): Uri? {
        return try {
            if (TextUtils.isEmpty(filePath)) return null
            val file = File(filePath)
            if (!file.exists()) null else toUri(file)
        } catch (e: Exception) {
            null
        }
    }

    //将本地图片转换成uri
    fun toUri(file: File?): Uri? {
        return try {
            val uri: Uri
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                uri = Uri.fromFile(file)
            } else {
                uri = FileProvider.getUriForFile(
                    AppConstant.getApp(),
                    AppConstant.fileProvider,
                    file!!
                )
            }
            Uri.decode(uri.toString())
            uri
        } catch (e: Exception) {
            null
        }
    }

    //获得uri的文件名
    fun queryUriFileName(uriId: Int): String? {
        var fileName = ""
        //兼容androidQ和以下版本
        val queryPathKey = MediaStore.Images.Media._ID
        //查询的条件语句
        val selection = "$queryPathKey=? "
        val cursor = AppConstant.getApp().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DATA),
            selection, arrayOf(uriId.toString() + ""),
            null
        )
        //是否查询到了
        if (cursor != null && cursor.moveToFirst()) {
            fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)) //图片名字
        }
        cursor?.close()
        return fileName
    }

    //将drawable保存到本地
    fun saveDrawableByFile(bitmapDrawable: BitmapDrawable?, filePath: String?): Boolean {
        try {
            if (bitmapDrawable != null) {
                val img = bitmapDrawable.bitmap
                val file = File(filePath)
                var bl = false
                if (file.exists()) {
                    if (file.delete()) {
                        bl = true
                    }
                } else {
                    bl = true
                }
                if (bl) {
                    val os: OutputStream = FileOutputStream(filePath)
                    img.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


}