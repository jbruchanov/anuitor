package com.scurab.android.anuitor.tools

import android.graphics.*
import android.os.Build
import android.view.View

private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

/**
 * Return absolute scale of view.
 * Any parent scale is also part of the result
 */
fun View.absoluteScale(): Pair<Float, Float> {
    var view = this
    val scale = floatArrayOf(1f, 1f)
    atLeastApi(Build.VERSION_CODES.HONEYCOMB) {
        scale[0] = view.scaleX
        scale[1] = view.scaleY
        this.parentViews().forEach { parent ->
            scale[0] *= parent.scaleX
            scale[1] *= parent.scaleY
        }
    }
    return Pair(scale[0], scale[1])
}

/**
 * Return true if the view has [View#width] and [View#height] non zero value
 */
fun View.hasSize() = width != 0 && height != 0

fun View.render(includeLocationOnScreen : Boolean = false): Bitmap {
    val location = intArrayOf(0, 0)
    if (includeLocationOnScreen) {
        getLocationOnScreen(location)
    }
    val w = location[0] + width
    val h = location[1] + height
    return if (location[0] != 0 || location[1] != 0)
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).run {
                //clear white background to get transparency
                drawRect(0f, 0f, w.toFloat(), h.toFloat(), clearPaint)
                translate(location[0].toFloat(), location[1].toFloat())
                draw(this)
            }
        } else {
        destroyDrawingCache()
        buildDrawingCache(false)
        drawingCache
    }
}

//TODO: use ^ render
fun View.render(renderArea: Rect): Bitmap {
    val b = Bitmap.createBitmap(renderArea.width(), renderArea.height(), Bitmap.Config.ARGB_8888)

    val c = Canvas(b)
    c.drawRect(0f, 0f, b.width.toFloat(), b.height.toFloat(), clearPaint)//clear white background to get transparency
    c.translate((-renderArea.left).toFloat(), (-renderArea.top).toFloat())
    draw(c)
    return b
}

/**
 * Render background into a bitmap if view has size
 */
fun View.renderBackground(): Bitmap? {
    return background
            ?.takeIf { hasSize() }
            ?.let { drawable ->
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                    Canvas(this).run {
                        //clear white background to get transparency
                        drawRect(0f, 0f, width.toFloat(), height.toFloat(), clearPaint)
                        drawable.draw(this)
                    }
                }
            }
}