package com.scurab.android.anuitor.nanoplugin

import android.app.Activity
import android.content.ContextWrapper
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.extract2.ExtractingContext
import com.scurab.android.anuitor.reflect.ActivityThreadReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

private const val FILE_DEEP = "screenstructure.json"

class ScreenStructurePlugin(private val windowManager: WindowManager) : BasePlugin() {

    private val activityThread = ActivityThreadReflector()
    private val files = arrayOf(FILE_DEEP)
    private val path = "/$FILE_DEEP"

    override fun files(): Array<String> = files
    override fun mimeType(): String = APP_JSON
    override fun canServeUri(uri: String, rootDir: File): Boolean = path == uri

    override fun serveFile(uri: String, headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {

        return try {
            val json = BasePlugin.JSON.toJson(deepStructure())
            OKResponse(APP_JSON, ByteArrayInputStream(json.toByteArray()))
        } catch (e: Throwable) {
            Response(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    ByteArrayInputStream((e.message ?: "Null exception message").toByteArray()))
        }
    }

    private fun deepStructure(): List<Map<String, Any>> {
        val viewRootNames = windowManager.viewRootNames
        val resultDataSet = mutableListOf<Map<String, Any>>()

        for (rootName in viewRootNames) {
            val v = windowManager.getRootView(rootName)
            var c = v.context
            val data = TreeMap<String, Any>()
            data["RootName"] = rootName
            resultDataSet.add(data)
            var activity = activityThread.activities
                    .firstOrNull { it.window.decorView.rootView == v.rootView }
            if (activity == null && c is Activity) {
                activity = c
            }

            if (activity != null) {
                DetailExtractor.getExtractor(activity.javaClass).apply {
                    fillValues(activity, ExtractingContext(data))
                }
            } else {
                DetailExtractor
                        .getExtractor(v)
                        .fillValues(v, ExtractingContext(data))

                if (c is ContextWrapper) {
                    c = c.baseContext
                    if (c is Activity) {
                        val sub = TreeMap<String, Any>()
                        data["OwnerActivity"] = sub
                        DetailExtractor.getExtractor(c.javaClass).apply {
                            fillValues(c, ExtractingContext(sub))
                        }
                    }
                }
            }
            data.remove(Constants.OWNER)
        }

        resultDataSet.reverse()//stack order
        return resultDataSet
    }
}