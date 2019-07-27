package com.scurab.android.anuitor.tools

import android.graphics.*
import android.os.Build
import android.view.View
import com.scurab.android.anuitor.extract2.DetailExtractor

internal val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

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

/**
 * Render view into bitma
 * @param includeLocationOnScreen - add spacing if the rootview is doesn't have origin [0,0],
 * this is necessary for ScreenPreview (just easier to avoid coords recalculation)
 */
fun View.render(includeLocationOnScreen : Boolean = false): Bitmap {
    val location = intArrayOf(0, 0)
    val renderArea = Rect(0, 0, width, height)

    if (includeLocationOnScreen) {
        getLocationOnScreen(location)
    }
    DetailExtractor.getRenderArea(this)?.getRenderArea(this, renderArea)
    val w = location[0] + renderArea.width()
    val h = location[1] + renderArea.height()
    return if ((location[0] != 0 || location[1] != 0) ||// includeLocationOnScreen
            renderArea.width() != width || renderArea.height() != height)//or custom renderArea
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).run {
                //clear white background to get transparency
                drawRect(0f, 0f, w.toFloat(), h.toFloat(), clearPaint)
                //custom render area
                translate((-renderArea.left).toFloat(), (-renderArea.top).toFloat())
                //includeLocationOnScreen
                translate(location[0].toFloat(), location[1].toFloat())
                draw(this)
            }
        } else {
        destroyDrawingCache()
        buildDrawingCache(false)
        drawingCache
    }
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