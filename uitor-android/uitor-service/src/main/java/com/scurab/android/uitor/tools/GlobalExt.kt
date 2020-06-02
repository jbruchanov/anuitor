package com.scurab.android.uitor.tools

import android.content.res.TypedArray
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun <R> TypedArray.use(block: (TypedArray) -> R): R {
    val r = block(this)
    recycle()
    return r
}

fun ByteArray.base64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun Bitmap.save(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
                quality: Int = 80,
                recycle: Boolean = true): ByteArray {
    val bos = ByteArrayOutputStream()
    compress(format, quality, bos)
    val result = bos.toByteArray()
    if (recycle) {
        recycle()
    }
    return result
}