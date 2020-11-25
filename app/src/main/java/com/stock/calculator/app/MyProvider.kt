package com.stock.calculator.app

import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileNotFoundException

/**
 * 适配外部sd卡存储
 */
class MyProvider : FileProvider() {
    override fun attachInfo(context: Context, info: ProviderInfo) {
        super.attachInfo(context, info) // Sanity check our security
        if (info.exported) {
            throw SecurityException("Provider must not be exported")
        }
        if (!info.grantUriPermissions) {
            throw SecurityException("Provider must grant uri permissions")
        }
    }


    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        if (!TextUtils.isEmpty(uri.path)) {
            val file = File(uri.path)
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        }
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun delete(uri: Uri, s: String?, `as`: Array<String>?): Int {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun getType(uri: Uri): String? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun insert(uri: Uri, contentvalues: ContentValues?): Uri? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun query(
        uri: Uri,
        `as`: Array<String>?,
        s: String?,
        as1: Array<String>?,
        s1: String?
    ): Cursor? {
        throw UnsupportedOperationException("Not supported by this provider")
    }

    override fun update(
        uri: Uri,
        contentvalues: ContentValues?,
        s: String?,
        `as`: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Not supported by this provider")
    }
}