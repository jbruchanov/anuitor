package com.scurab.android.anuitor.nanoplugin

import android.graphics.*
import android.view.View
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG
import com.scurab.android.anuitor.tools.render
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream

private const val SCREEN_PNG = "screen.png"
private const val PATH = "/$SCREEN_PNG"

class ScreenViewPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

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
                view.render(true)
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
}
