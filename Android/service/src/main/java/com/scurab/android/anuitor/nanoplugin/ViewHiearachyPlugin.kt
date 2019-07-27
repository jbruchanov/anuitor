package com.scurab.android.anuitor.nanoplugin

import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.Executor
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

private const val TREE_JSON = "viewhierarchy.json"
private const val PATH = "/$TREE_JSON"
private const val DEFAULT_TIMEOUT = 20000//20s


class ViewHierarchyPlugin(windowManager: WindowManager) : ActivityPlugin(windowManager) {

    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri
    override fun files(): Array<String> = arrayOf(TREE_JSON)
    override fun mimeType(): String = APP_JSON

    override fun onRequest(uri: String, headers: Map<String, String>, session: NanoHTTPD.IHTTPSession, file: File, mimeType: String): NanoHTTPD.Response {
        val view = session.queryParameterString?.currentRootView()

        val response: NanoHTTPD.Response
        if (view != null) {
            val json = Executor.runInMainThreadBlocking(DEFAULT_TIMEOUT) {
                val vn = DetailExtractor.parse(view, false)
                try {
                    vn.toJson().toString()
                } catch (e: Throwable) {
                    e.printStackTrace()
                    String.format("{\"exception\":\"%s\"}", e.message)
                }
            }
            response = OKResponse(APP_JSON, ByteArrayInputStream(json.toByteArray()))
        } else {
            response = Response(NanoHTTPD.Response.Status.NOT_FOUND, APP_JSON, null as InputStream?)
        }

        return response
    }
}
