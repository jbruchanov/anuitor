package com.scurab.android.anuitor.nanoplugin

import android.content.Context
import com.scurab.android.anuitor.model.FSItem
import com.scurab.android.anuitor.tools.FileSystemTools
import com.scurab.android.anuitor.tools.HttpTools
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URLDecoder

private const val FILE = "storage.json"
private const val PATH = "/$FILE"

class FileStoragePlugin(context: Context) : BasePlugin() {
    private val context:Context = context.applicationContext

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = APP_JSON
    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri

    override fun serveFile(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        var inputStream: InputStream
        var mime: String
        var content: String? = null
        try {
            val files: List<FSItem>
            val json: String
            val f = session.queryParameterString
                    ?.parseQueryStringPath()
                    ?.let { File(it) }

            if (f?.isFile == true) {
                inputStream = FileInputStream(f)
                mime = HttpTools.getMimeType(f)//try get proper mime to show it directly in browser, otherwise download
                content = "inline; filename=" + f.name//add download filename
            } else { //folder
                mime = APP_JSON
                files = if (f == null) FileSystemTools.get(context) else FileSystemTools.get(f)
                json = BasePlugin.JSON.toJson(files)
                inputStream = ByteArrayInputStream(json.toByteArray())
            }
        } catch (e: Exception) {
            inputStream = ByteArrayInputStream((e.message ?: "Null exception message").toByteArray())
            mime = APP_JSON
        }

        val response = OKResponse(mime, inputStream)
        if (content != null) {
            response.addHeader("Content-Disposition", content)
        }
        return response
    }

    private fun String.parseQueryStringPath(): String? {
        return this.takeIf { it.isNotEmpty() }
                .let { HttpTools.parseQueryString(this)["path"] }
                ?.takeIf { it.isNotEmpty() }
                ?.let { URLDecoder.decode(it) }
    }
}
