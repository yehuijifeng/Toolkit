package com.wwxd.utils

object Base64Util {
    private const val BASELENGTH = 128
    private const val LOOKUPLENGTH = 64
    private const val TWENTYFOURBITGROUP = 24
    private const val EIGHTBIT = 8
    private const val SIXTEENBIT = 16
    private const val FOURBYTE = 4
    private const val SIGN = -128
    private const val PAD = '='
    private val base64Alphabet = ByteArray(BASELENGTH)
    private val lookUpBase64Alphabet = CharArray(LOOKUPLENGTH)
    private fun isWhiteSpace(octect: Char): Boolean {
        return octect.toInt() == 0x20 || octect.toInt() == 0xd || octect.toInt() == 0xa || octect.toInt() == 0x9
    }

    private fun isPad(octect: Char): Boolean {
        return octect == PAD
    }

    private fun isData(octect: Char): Boolean {
        return octect.toInt() < BASELENGTH && base64Alphabet[octect.toInt()].toInt() != -1
    }

    /**
     * Encodes hex octects into Base64
     *
     * @param binaryData
     * Array containing binaryData
     * @return Encoded Base64 array
     */
    fun encode(binaryData: ByteArray?): String? {
        if (binaryData == null) {
            return null
        }
        val lengthDataBits = binaryData.size * EIGHTBIT
        if (lengthDataBits == 0) {
            return ""
        }
        val fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP
        val numberTriplets = lengthDataBits / TWENTYFOURBITGROUP
        val numberQuartet = if (fewerThan24bits != 0) numberTriplets + 1 else numberTriplets
        var encodedData: CharArray? = null
        encodedData = CharArray(numberQuartet * 4)
        var k: Byte = 0
        var l: Byte = 0
        var b1: Byte = 0
        var b2: Byte = 0
        var b3: Byte = 0
        var encodedIndex = 0
        var dataIndex = 0
        for (i in 0 until numberTriplets) {
            b1 = binaryData[dataIndex++]
            b2 = binaryData[dataIndex++]
            b3 = binaryData[dataIndex++]
            l = (b2.toInt() and 0x0f) as Byte
            k = (b1.toInt() and 0x03) as Byte
            val val1 = if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2) as Byte else (b1.toInt() shr 2 xor 0xc0)
            val val2 = if (b2.toInt() and SIGN == 0) (b2.toInt() shr 4) as Byte else (b2.toInt() shr 4 xor 0xf0)
            val val3 = if (b3.toInt() and SIGN == 0) (b3.toInt() shr 6) as Byte else (b3.toInt() shr 6 xor 0xfc)
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1.toInt()]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val2.toInt() or (k.toInt() shl 4)]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[l.toInt() shl 2 or val3.toInt()]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[b3.toInt() and 0x3f]
        }

        // form integral number of 6-bit groups
        if (fewerThan24bits == EIGHTBIT) {
            b1 = binaryData[dataIndex]
            k = (b1.toInt() and 0x03) as Byte
            val val1 = if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2) as Byte else (b1.toInt() shr 2 xor 0xc0)
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1.toInt()]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[k.toInt() shl 4]
            encodedData[encodedIndex++] = PAD
            encodedData[encodedIndex++] = PAD
        } else if (fewerThan24bits == SIXTEENBIT) {
            b1 = binaryData[dataIndex]
            b2 = binaryData[dataIndex + 1]
            l = (b2.toInt() and 0x0f) as Byte
            k = (b1.toInt() and 0x03) as Byte
            val val1 = if (b1.toInt() and SIGN == 0) (b1.toInt() shr 2) as Byte else (b1.toInt() shr 2 xor 0xc0)
            val val2 = if (b2.toInt() and SIGN == 0) (b2.toInt() shr 4) as Byte else (b2.toInt() shr 4 xor 0xf0)
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val1.toInt()]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[val2.toInt() or (k.toInt() shl 4)]
            encodedData[encodedIndex++] = lookUpBase64Alphabet[l.toInt() shl 2]
            encodedData[encodedIndex++] = PAD
        }
        return String(encodedData)
    }

    /**
     * Decodes Base64 data into octects
     *
     * @param encoded
     * string containing Base64 data
     * @return Array containind decoded data.
     */
    fun decode(encoded: String?): ByteArray? {
        if (encoded == null) {
            return null
        }
        val base64Data = encoded.toCharArray()
        // remove white spaces
        val len = removeWhiteSpace(base64Data)
        if (len % FOURBYTE != 0) {
            return null // should be divisible by four
        }
        val numberQuadruple = len / FOURBYTE
        if (numberQuadruple == 0) {
            return ByteArray(0)
        }
        var decodedData: ByteArray? = null
        var b1: Byte = 0
        var b2: Byte = 0
        var b3: Byte = 0
        var b4: Byte = 0
        var d1 = 0.toChar()
        var d2 = 0.toChar()
        var d3 = 0.toChar()
        var d4 = 0.toChar()
        var i = 0
        var encodedIndex = 0
        var dataIndex = 0
        decodedData = ByteArray(numberQuadruple * 3)
        while (i < numberQuadruple - 1) {
            if (!isData(base64Data[dataIndex++].also { d1 = it })
                || !isData(base64Data[dataIndex++].also { d2 = it })
                || !isData(base64Data[dataIndex++].also { d3 = it })
                || !isData(base64Data[dataIndex++].also { d4 = it })
            ) {
                return null
            } // if found "no data" just return null
            b1 = base64Alphabet[d1.toInt()]
            b2 = base64Alphabet[d2.toInt()]
            b3 = base64Alphabet[d3.toInt()]
            b4 = base64Alphabet[d4.toInt()]
            decodedData[encodedIndex++] = (b1.toInt() shl 2 or b2.toInt() shr 4) as Byte
            decodedData[encodedIndex++] = (b2.toInt() and 0xf shl 4 or (b3.toInt() shr 2 and 0xf)) as Byte
            decodedData[encodedIndex++] = (b3.toInt() shl 6 or b4.toInt()) as Byte
            i++
        }
        if (!isData(base64Data[dataIndex++].also { d1 = it })
            || !isData(base64Data[dataIndex++].also { d2 = it })
        ) {
            return null // if found "no data" just return null
        }
        b1 = base64Alphabet[d1.toInt()]
        b2 = base64Alphabet[d2.toInt()]
        d3 = base64Data[dataIndex++]
        d4 = base64Data[dataIndex++]
        if (!isData(d3) || !isData(d4)) { // Check if they are PAD characters
            return if (isPad(d3) && isPad(d4)) {
                if (b2.toInt() and 0xf != 0) // last 4 bits should be zero
                {
                    return null
                }
                val tmp = ByteArray(i * 3 + 1)
                System.arraycopy(decodedData, 0, tmp, 0, i * 3)
                tmp[encodedIndex] = (b1.toInt() shl 2 or b2.toInt() shr 4) as Byte
                tmp
            } else if (!isPad(d3) && isPad(d4)) {
                b3 = base64Alphabet[d3.toInt()]
                if (b3.toInt() and 0x3 != 0) // last 2 bits should be zero
                {
                    return null
                }
                val tmp = ByteArray(i * 3 + 2)
                System.arraycopy(decodedData, 0, tmp, 0, i * 3)
                tmp[encodedIndex++] = (b1.toInt() shl 2 or b2.toInt() shr 4) as Byte
                tmp[encodedIndex] = (b2.toInt() and 0xf shl 4 or (b3.toInt() shr 2 and 0xf)) as Byte
                tmp
            } else {
                null
            }
        } else { // No PAD e.g 3cQl
            b3 = base64Alphabet[d3.toInt()]
            b4 = base64Alphabet[d4.toInt()]
            decodedData[encodedIndex++] = (b1.toInt() shl 2 or b2.toInt() shr 4) as Byte
            decodedData[encodedIndex++] = (b2.toInt() and 0xf shl 4 or (b3.toInt() shr 2 and 0xf)) as Byte
            decodedData[encodedIndex++] = (b3.toInt() shl 6 or b4.toInt()) as Byte
        }
        return decodedData
    }

    /**
     * remove WhiteSpace from MIME containing encoded Base64 data.
     *
     * @param data
     * the byte array of base64 data (with WS)
     * @return the new length
     */
    private fun removeWhiteSpace(data: CharArray?): Int {
        if (data == null) {
            return 0
        }

        // count characters that's not whitespace
        var newSize = 0
        val len = data.size
        for (i in 0 until len) {
            if (!isWhiteSpace(data[i])) {
                data[newSize++] = data[i]
            }
        }
        return newSize
    }

    init {
        for (i in 0 until BASELENGTH) {
            base64Alphabet[i] = -1
        }
        run {
            var i = 'Z'.toInt()
            while (i >= 'A'.toInt()) {
                base64Alphabet[i] = (i - 'A'.toInt()) as Byte
                i--
            }
        }
        run {
            var i = 'z'.toInt()
            while (i >= 'a'.toInt()) {
                base64Alphabet[i] = (i - 'a'.toInt() + 26) as Byte
                i--
            }
        }
        run {
            var i = '9'.toInt()
            while (i >= '0'.toInt()) {
                base64Alphabet[i] = (i - '0'.toInt() + 52) as Byte
                i--
            }
        }
        base64Alphabet['+'.toInt()] = 62
        base64Alphabet['/'.toInt()] = 63
        for (i in 0..25) {
            lookUpBase64Alphabet[i] = ('A'.toInt() + i).toChar()
        }
        run {
            var i = 26
            var j = 0
            while (i <= 51) {
                lookUpBase64Alphabet[i] = ('a'.toInt() + j) as Char
                i++
                j++
            }
        }
        var i = 52
        var j = 0
        while (i <= 61) {
            lookUpBase64Alphabet[i] = ('0'.toInt() + j) as Char
            i++
            j++
        }
        lookUpBase64Alphabet[62] = '+'
        lookUpBase64Alphabet[63] = '/'
    }
}