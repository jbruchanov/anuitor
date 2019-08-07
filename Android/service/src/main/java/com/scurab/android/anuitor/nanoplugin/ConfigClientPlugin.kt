package com.scurab.android.anuitor.nanoplugin

import com.scurab.android.anuitor.tools.HttpTools
import fi.iki.elonen.NanoHTTPD
import java.io.File

private const val FILE = "config.json"
private const val PATH = "/$FILE"

class ConfigClientPlugin(private val data: Map<String, Any>) : BasePlugin() {

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = HttpTools.MimeType.APP_JSON
    override fun canServeUri(uri: String, rootDir: File) = PATH == uri

    override fun serveFile(uri: String,
                           headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {
        return OKResponse(HttpTools.MimeType.TEXT_PLAIN, BasePlugin.JSON.toJson(data))
    }
}
