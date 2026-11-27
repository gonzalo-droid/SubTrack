package com.gondroid.subtrack.core.util

import androidx.core.graphics.set
import androidx.core.graphics.createBitmap
import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
    val hints = mapOf(EncodeHintType.MARGIN to 1)
    val bits = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp = createBitmap(size, size)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp[x, y] = if (bits[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return bmp
}

fun maskCci(number: String): String {
    if (number.length < 8) return number
    val prefix = number.take(5)
    val suffix = number.takeLast(4)
    return "$prefix•••••$suffix"
}
