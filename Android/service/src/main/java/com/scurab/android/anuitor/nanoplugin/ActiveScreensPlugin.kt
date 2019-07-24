package com.scurab.android.anuitor.nanoplugin

import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File

private const val FILE = "screens.json"
private const val PATH = "/$FILE"

class ActiveScreensPlugin(private val windowManager: WindowManager) : ActivityPlugin(windowManager) {

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = APP_JSON
    override fun canServeUri(uri: String, rootDir: File) = PATH == uri

    override fun handleRequest(uri: String,
                               headers: Map<String, String>,
                               session: NanoHTTPD.IHTTPSession,
                               file: File,
                               mimeType: String): NanoHTTPD.Response {
        val json = BasePlugin.JSON.toJson(windowManager.viewRootNames)
        return OKResponse(APP_JSON, ByteArrayInputStream(json.toByteArray()))
    }
}
