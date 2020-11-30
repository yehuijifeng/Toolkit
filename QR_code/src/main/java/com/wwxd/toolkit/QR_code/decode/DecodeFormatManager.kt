package com.wwxd.toolkit.QR_code.decode

import android.content.Intent
import android.net.Uri
import com.google.zxing.BarcodeFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap
//解码格式管理器
object DecodeFormatManager {
    /**
     * By default, sending this will decode all barcodes that we understand. However it
     * may be useful to limit scanning to certain formats. Use
     * [android.content.Intent.putExtra] with one of the values below.
     *
     * Setting this is effectively shorthand for setting explicit formats with [.FORMATS].
     * It is overridden by that setting.
     */
    private val MODE = "SCAN_MODE"

    /**
     * Decode only UPC and EAN barcodes. This is the right choice for shopping apps which get
     * prices, reviews, etc. for products.
     */
    private val PRODUCT_MODE = "PRODUCT_MODE"

    /**
     * Decode only 1D barcodes.
     */
    private val ONE_D_MODE = "ONE_D_MODE"

    /**
     * Decode only QR codes.
     */
    private val QR_CODE_MODE = "QR_CODE_MODE"

    /**
     * Decode only Data Matrix codes.
     */
    private val DATA_MATRIX_MODE = "DATA_MATRIX_MODE"

    /**
     * Decode only Aztec.
     */
    private val AZTEC_MODE = "AZTEC_MODE"

    /**
     * Decode only PDF417.
     */
    private val PDF417_MODE = "PDF417_MODE"

    /**
     * Comma-separated list of formats to scan for. The values must match the names of
     * [com.google.zxing.BarcodeFormat]s, e.g. [com.google.zxing.BarcodeFormat.EAN_13].
     * Example: "EAN_13,EAN_8,QR_CODE". This overrides [.MODE].
     */
    private val FORMATS = "SCAN_FORMATS"

    private val COMMA_PATTERN = Pattern.compile(",")
    var PRODUCT_FORMATS: Set<BarcodeFormat>
    var INDUSTRIAL_FORMATS: EnumSet<BarcodeFormat>
    var ONE_D_FORMATS: EnumSet<BarcodeFormat>
    val QR_CODE_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.QR_CODE)
    val DATA_MATRIX_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.DATA_MATRIX)
    val AZTEC_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.AZTEC)
    val PDF417_FORMATS: Set<BarcodeFormat> = EnumSet.of(BarcodeFormat.PDF_417)
    private var FORMATS_FOR_MODE: HashMap<String, Set<BarcodeFormat>>
    fun parseDecodeFormats(intent: Intent): Set<BarcodeFormat>? {
        var scanFormats: Iterable<String>? = null
        val scanFormatsString: CharSequence? = intent.getStringExtra(FORMATS)
        if (scanFormatsString != null) {
            scanFormats = Arrays.asList(*COMMA_PATTERN.split(scanFormatsString))
        }
        return parseDecodeFormats(scanFormats, intent.getStringExtra(MODE))
    }

    fun parseDecodeFormats(inputUri: Uri): Set<BarcodeFormat>? {
        var formats = inputUri.getQueryParameters(FORMATS)
        if (formats != null && formats.size == 1 && formats[0] != null) {
            formats = Arrays.asList(
                *COMMA_PATTERN.split(
                    formats[0]
                )
            )
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(MODE))
    }

    private fun parseDecodeFormats(
        scanFormats: Iterable<String?>?,
        decodeMode: String?
    ): Set<BarcodeFormat>? {
        if (scanFormats != null) {
            val formats: MutableSet<BarcodeFormat> = EnumSet.noneOf(
                BarcodeFormat::class.java
            )
            try {
                for (format in scanFormats) {
                    formats.add(BarcodeFormat.valueOf(format!!))
                }
                return formats
            } catch (iae: IllegalArgumentException) {
                // ignore it then
            }
        }
        return if (decodeMode != null) {
            FORMATS_FOR_MODE[decodeMode]
        } else null
    }

    init {
        PRODUCT_FORMATS = EnumSet.of(
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.RSS_14,
            BarcodeFormat.RSS_EXPANDED
        )
        INDUSTRIAL_FORMATS = EnumSet.of(
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.CODE_128,
            BarcodeFormat.ITF,
            BarcodeFormat.CODABAR
        )
        ONE_D_FORMATS = EnumSet.copyOf(PRODUCT_FORMATS)
        ONE_D_FORMATS.addAll(INDUSTRIAL_FORMATS)
    }

    init {
        FORMATS_FOR_MODE = HashMap()
        FORMATS_FOR_MODE[ONE_D_MODE] = ONE_D_FORMATS
        FORMATS_FOR_MODE[PRODUCT_MODE] =
            PRODUCT_FORMATS
        FORMATS_FOR_MODE[QR_CODE_MODE] = QR_CODE_FORMATS
        FORMATS_FOR_MODE[DATA_MATRIX_MODE] = DATA_MATRIX_FORMATS
        FORMATS_FOR_MODE[AZTEC_MODE] =
            AZTEC_FORMATS
        FORMATS_FOR_MODE[PDF417_MODE] = PDF417_FORMATS
    }
}