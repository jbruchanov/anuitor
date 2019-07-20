package com.scurab.android.anuitor.nanoplugin

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.scurab.android.anuitor.Constants
import com.scurab.android.anuitor.extract2.DetailExtractor
import com.scurab.android.anuitor.extract2.ExtractingContext
import com.scurab.android.anuitor.extract2.getActivity
import com.scurab.android.anuitor.reflect.ActivityThreadReflector
import com.scurab.android.anuitor.reflect.WindowManager
import com.scurab.android.anuitor.tools.HttpTools
import com.scurab.android.anuitor.tools.HttpTools.MimeType.APP_JSON
import com.scurab.android.anuitor.tools.atLeastApi
import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.File
import java.lang.IllegalStateException
import java.util.*

private const val FILE = "screenstructure.json"
private const val PATH = "/$FILE"
private typealias Node = Map<String, Any>

class ScreenStructurePlugin(private val windowManager: WindowManager) : BasePlugin() {

    private val activityThread = ActivityThreadReflector()

    override fun files(): Array<String> = arrayOf(FILE)
    override fun mimeType(): String = APP_JSON
    override fun canServeUri(uri: String, rootDir: File): Boolean = PATH == uri

    override fun serveFile(uri: String, headers: Map<String, String>,
                           session: NanoHTTPD.IHTTPSession,
                           file: File,
                           mimeType: String): NanoHTTPD.Response {

        val qs = HttpTools.parseQueryString(session.queryParameterString)
        return try {
            val result: Any =
                    if ("simple" == qs["type"]) simpleStructure(activityThread.application)
                    else deepStructure()

            val json = BasePlugin.JSON.toJson(result)
            OKResponse(APP_JSON, ByteArrayInputStream(json.toByteArray()))
        } catch (e: Throwable) {
            Response(NanoHTTPD.Response.Status.INTERNAL_ERROR,
                    NanoHTTPD.MIME_PLAINTEXT,
                    ByteArrayInputStream((e.message ?: "Null exception message").toByteArray()))
        }
    }

    private fun simpleStructure(item: Any): Node {
        val items = mutableListOf<Any>()
        val name = "${item.javaClass.name}@${Integer.toHexString(item.hashCode())}"
        val node: Node = mutableMapOf(name to items)
        when (item) {
            is View -> {
                //nothing, just keep name of view
            }
            is Application -> {
                windowManager.viewRootNames
                        .mapNotNull { Pair(it, windowManager.getRootView(it)) }
                        .forEach { (name, item) ->
                            //naive activity detection => app/activity/view
                            if (name.indexOfFirst { it == '/' } != name.indexOfLast { it == '/' }) {
                                items.add(simpleStructure(item.getActivity()
                                        ?: throw IllegalStateException("View without activity?!")))
                            } else {
                                items.add(simpleStructure(item))
                            }
                        }
            }
            is Activity -> {
                atLeastApi(Build.VERSION_CODES.O) {
                    items.addAll(item.fragmentManager.fragmentsAsNodes())
                }
                if (item is FragmentActivity) {
                    items.addAll(item.supportFragmentManager.fragmentsAsNodes())
                }
            }
            is android.app.Fragment -> {
                atLeastApi(Build.VERSION_CODES.O) {
                    items.addAll(item.childFragmentManager.fragmentsAsNodes())
                }
            }
            is Fragment -> items.addAll(item.childFragmentManager.fragmentsAsNodes())
            else -> throw IllegalArgumentException("Unsupported type:${item.javaClass.name}")
        }
        return node
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

    private fun FragmentManager.fragmentsAsNodes(): List<Node> {
        return fragments
                .filterNotNull()
                .map { f -> simpleStructure(f) }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun android.app.FragmentManager.fragmentsAsNodes(): List<Node> {
        return fragments
                .filterNotNull()
                .map { f -> simpleStructure(f) }
    }
}