package com.scurab.android.anuitor.nanoplugin

import android.graphics.*
import android.view.View
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

private const val SCREEN_PNG = "screen.png"
private const val PATH = "/$SCREEN_PNG"

class ScreenViewPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri
    override fun mimeType(): String = IMAGE_PNG
    override fun files(): Array<String> = arrayOf(SCREEN_PNG)

    override fun handleRequest(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        val bos = ByteArrayOutputStream()
        val view = session.queryParameterString.currentRootView()
        val response: NanoHTTPD.Response

        if (view != null) {
            //null can happen if app is not running
            val resultStream = Executor.runInMainThreadBlockingOnlyIfCrashing {
                view.render()
            }?.let { bitmap ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                val stream = ByteArrayInputStream(bos.toByteArray())
                bitmap.recycle()
                stream
            } ?: ByteArrayInputStream(ByteArray(0))

            response = OKResponse(IMAGE_PNG, resultStream)
        } else {
            response = Response(NanoHTTPD.Response.Status.NOT_FOUND, IMAGE_PNG, null as InputStream?)
        }
        return response
    }

    private fun View.render(): Bitmap {
        val location = intArrayOf(0, 0)
        getLocationOnScreen(location)
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
}
