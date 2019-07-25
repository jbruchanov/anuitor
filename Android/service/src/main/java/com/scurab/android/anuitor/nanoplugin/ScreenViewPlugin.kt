package com.scurab.android.anuitor.nanoplugin

import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.HttpTools.MimeType.IMAGE_PNG
import com.scurab.android.anuitor.tools.renderToPng
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

private const val SCREEN_PNG = "screen.png"
private const val PATH = "/$SCREEN_PNG"

class ScreenViewPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri
    override fun mimeType(): String = IMAGE_PNG
    override fun files(): Array<String> = arrayOf(SCREEN_PNG)

    override fun handleRequest(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        val view = session.queryParameterString.currentRootView()
        val response: NanoHTTPD.Response

        if (view != null) {
            //null can happen if app is not running
            val resultStream = ByteArrayInputStream(
                    Executor.runInMainThreadBlockingOnlyIfCrashing { view.renderToPng(true) }
                            ?: (ByteArray(0)))
            response = OKResponse(IMAGE_PNG, resultStream)
        } else {
            response = Response(NanoHTTPD.Response.Status.NOT_FOUND, IMAGE_PNG, null as InputStream?)
        }
        return response
    }
}
