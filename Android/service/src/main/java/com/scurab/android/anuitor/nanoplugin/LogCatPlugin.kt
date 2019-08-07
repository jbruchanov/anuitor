package com.scurab.android.anuitor.nanoplugin

import com.scurab.android.anuitor.tools.HttpTools
import com.scurab.android.anuitor.tools.LogCatProvider
import fi.iki.elonen.NanoHTTPD
import java.io.File

private const val FILE = "logcat.txt"
private const val PATH = "/$FILE"

class LogCatPlugin : BasePlugin() {

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = HttpTools.MimeType.TEXT_PLAIN
    override fun canServeUri(uri: String, rootDir: File) = PATH == uri

    override fun serveFile(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        val type = session.queryParameterString
                ?.let { HttpTools.parseQueryString(it) }
                ?.get("type")
        return OKResponse(HttpTools.MimeType.TEXT_PLAIN, LogCatProvider.dumpLogcat(type))
    }
}
