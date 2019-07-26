package com.scurab.android.anuitor.tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import java.io.ByteArrayOutputStream

/**
 * Render drawable with sizes if bounds are not set
 */
fun Drawable.render(xmlW: Int, xmlH: Int): ByteArray {
    val w = intrinsicWidth.takeIf { it != -1 } ?: xmlW
    val h = intrinsicHeight.takeIf { it != -1 } ?: xmlH
    return renderWithSize(w, h)
}

fun Drawable.renderWithBounds(): ByteArray {
    return renderWithSize(bounds.width(), bounds.height(), false)
}

fun Drawable.renderWithSize(w: Int, h: Int): ByteArray {
    return renderWithSize(w, h, true)
}

fun Drawable.renderWithSize(w: Int, h: Int, setBounds: Boolean): ByteArray {
    val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    if (setBounds) {
        setBounds(0, 0, w, h)
    }
    Canvas(b).run {
        drawRect(0f, 0f, w.toFloat(), h.toFloat(), clearPaint)
        draw(this)
    }

    return ByteArrayOutputStream(b.byteCount).apply {
        b.compress(Bitmap.CompressFormat.PNG, 100, this)
        b.recycle()
    }.toByteArray()
}

fun NinePatchDrawable.renderNinePatchDrawable() : ByteArray {

}