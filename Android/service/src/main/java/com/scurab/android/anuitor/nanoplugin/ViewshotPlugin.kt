package com.scurab.android.anuitor.nanoplugin

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.*
import com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

private const val VIEW_PNG = "view.png"
private const val PATH = "/$VIEW_PNG"

class ViewshotPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

    override fun files(): Array<String> = arrayOf(VIEW_PNG)
    override fun mimeType(): String = IMAGE_PNG
    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri

    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }
    private val emptyBitmap: Bitmap by lazy {
        val b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.drawRect(0f, 0f, 1f, 1f, clearPaint)
        b
    }

    override fun handleRequest(uri: String,
                               headers: Map<String, String>,
                               session: NanoHTTPD.IHTTPSession,
                               file: File,
                               mimeType: String): NanoHTTPD.Response {
        val queryString = session.queryParameterString
        val len = queryString?.length ?: 0
        var resultInputStream: ByteArrayInputStream? = null

        if (len > 0) {
            val qsValue = HttpTools.parseQueryString(queryString)
            if (qsValue.containsKey(POSITION)) {
                resultInputStream = qsValue.currentRootView()
                        ?.let { DetailExtractor.findViewByPosition(it, qsValue[POSITION]?.toInt() ?: 0) }
                        ?.let { view ->
                            val bitmap = view
                                    .takeIf { it.visibility == View.VISIBLE && it.hasSize() }
                                    ?.let {
                                        Executor.runInMainThreadBlockingOnlyIfCrashing {
                                            view.takeIf { it is ViewGroup && !DetailExtractor.isExcludedViewGroup(it.javaClass.name) }
                                                    ?.let { view.renderBackground() }
                                                    ?: view.render()
                                        }
                                    }?.let {
                                        var bitmap = it
                                        val (scaleX, scaleY) = view.absoluteScale()
                                        if (scaleX != 1f || scaleY != 1f && it.width > 0 && it.height > 0) {
                                            val scaledW = ((it.width * scaleX) + 0.5f).toInt()
                                            val scaledH = ((it.height * scaleY) + 0.5f).toInt()
                                            if (scaledW > 0 && scaledH > 0) {//just prevention about some nonsense
                                                val scaled = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, false)
                                                it.recycle()
                                                bitmap = scaled
                                            }
                                        }
                                        bitmap
                                    } ?: emptyBitmap
                            ByteArrayInputStream(bitmap.save())
                        }
            }


        }
        if (resultInputStream == null) {
            resultInputStream = ByteArrayInputStream(ByteArray(0))
        }
        return OKResponse(IMAGE_PNG, resultInputStream)
    }
}
